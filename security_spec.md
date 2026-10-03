# Security Specification (`security_spec.md`)

## 1. Data Invariants
1. **Strict Authentication Gate**: No read or write operation is permitted on `/users/{userId}` or `/bookings/{bookingId}` unless `request.auth != null`.
2. **PII & Multi-User Isolation**:
   - `/users/{userId}` contains PII (`phone`, `address`, `gotra`) and is strictly readable and writable only when `request.auth.uid == userId` and `data.userId == request.auth.uid`.
   - `/bookings/{bookingId}` contains Yajman PII (`yajmanPhone`, `yajmanAddress`, `gotra`) and is strictly readable (`get`, `list`) and writable (`create`, `update`, `delete`) only when `resource.data.userId == request.auth.uid`.
3. **Mathematical Commission & Price Integrity**:
   - `basePrice >= 0`, `samagriPrice >= 0`, `totalAmount == basePrice + samagriPrice`, `platformCommission >= 0`, `panditPayout == totalAmount - platformCommission`.
4. **Terminal State Locking**:
   - Once a booking reaches `bookingStatus == 'COMPLETED'` or `'CANCELLED'`, no subsequent updates are allowed (`existing().bookingStatus == 'UPCOMING'`).
5. **Temporal & Identity Immortality**:
   - `userId` and `createdAt` cannot be modified after creation. All timestamps (`createdAt`, `updatedAt`) must be valid Firestore `timestamp` objects `<= request.time`.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Read (`users`/`bookings`)**: `auth = null` attempting `get` or `list`.
2. **Cross-User PII Read**: User `alice_123` attempting `get` on `/users/bob_456` or `/bookings/bob_booking`.
3. **Unfiltered List Query Scraping**: User `alice_123` executing `db.collection("bookings").get()` without `.where("userId", "==", "alice_123")`.
4. **Identity Spoofing on Create**: User `alice_123` creating a booking with `userId: "bob_456"`.
5. **Shadow Update / Ghost Field Injection**: Sending extra unauthorized field `isVerifiedAdmin: true` on `/users/{userId}` or `/bookings/{bookingId}`.
6. **Immutable Field Mutation**: Attempting to mutate `createdAt` or `userId` during `update` on `/bookings/{bookingId}`.
7. **Terminal State Reversal**: Attempting to update `bookingStatus` of a `'COMPLETED'` booking back to `'UPCOMING'`.
8. **Value Poisoning on Update**: Updating `bookingStatus` to an invalid enum value `"HACKED"` or a 10KB string.
9. **Future Timestamp Injection**: Creating a booking with `createdAt` set in the future (`> request.time`) or as an integer epoch instead of `timestamp`.
10. **Negative Price / Math Mismatch**: Creating a booking where `totalAmount != basePrice + samagriPrice` or `basePrice < 0`.
11. **Path ID Poisoning**: Creating a document with an invalid ID containing spaces or special characters.
12. **Unauthorized Partial Update of Locked Pricing**: Attempting to modify `totalAmount` or `panditPayout` during a status update on `/bookings/{bookingId}`.
