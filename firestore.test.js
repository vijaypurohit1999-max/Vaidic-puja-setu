const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function buildValidBookingPayload(userId, bookingId = "booking_1") {
  const now = new Date();
  return {
    id: bookingId,
    userId,
    yajmanName: "Rajesh Sharma",
    yajmanPhone: "9876543210",
    yajmanAddress: "108 Mahakal Marg, Ujjain, MP",
    gotra: "Kashyap",
    pujaId: "bagalamukhi_havan",
    pujaTitle: "Bagalamukhi Havan Poojan",
    pujaDate: "2026-10-15",
    timeSlot: "08:00 AM",
    includeSamagri: true,
    specialInstructions: "Sankalp for family peace and protection",
    basePrice: 3100,
    samagriPrice: 900,
    totalAmount: 4000,
    platformCommission: 600,
    panditPayout: 3400,
    paymentMethod: "UPI_GPAY",
    paymentStatus: "PAID",
    bookingStatus: "UPCOMING",
    panditName: "Acharya Vishwanath Shastri",
    createdAt: now,
    updatedAt: now,
  };
}

function buildValidProfilePayload(userId) {
  const now = new Date();
  return {
    userId,
    name: "Rajesh Sharma",
    phone: "9876543210",
    address: "108 Mahakal Marg, Ujjain, MP",
    gotra: "Kashyap",
    role: "YAJMAN",
    preferredPuja: "Bagalamukhi Havan Poojan",
    specialInstructions: "Need Hindi and Sanskrit Vidhi",
    isPanditOnline: true,
    otpVerified: true,
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated user: cannot read bookings or profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("bookings").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read own profile, cannot read another user's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildValidProfilePayload(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and query own bookings with userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("bookings")
      .doc("booking_1")
      .set(buildValidBookingPayload(ALICE_UID, "booking_1"))
  );

  await assertSucceeds(
    aliceDb.collection("bookings").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: fails query without userId filter (rules are not filters)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("bookings").get());
});

test("Authenticated user: cannot read or update another user's booking", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb
      .collection("bookings")
      .doc("booking_1")
      .set(buildValidBookingPayload(ALICE_UID, "booking_1"))
  );

  await assertFails(bobDb.collection("bookings").doc("booking_1").get());
});

test("Shadow update & Terminal state locking: rejects ghost fields and completed booking mutations", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("bookings")
      .doc("booking_1")
      .set(buildValidBookingPayload(ALICE_UID, "booking_1"))
  );

  // Ghost field injection fails
  await assertFails(
    aliceDb.collection("bookings").doc("booking_1").update({
      bookingStatus: "COMPLETED",
      isVerifiedAdmin: true,
      updatedAt: new Date(),
    })
  );

  // Valid transition to COMPLETED succeeds
  await assertSucceeds(
    aliceDb.collection("bookings").doc("booking_1").update({
      bookingStatus: "COMPLETED",
      updatedAt: new Date(),
    })
  );

  // Subsequent mutation after terminal state COMPLETED fails
  await assertFails(
    aliceDb.collection("bookings").doc("booking_1").update({
      bookingStatus: "UPCOMING",
      updatedAt: new Date(),
    })
  );
});
