/*
 Chris Hani Baragwanath Hospital in Soweto sees more than a thousand emergency patients every day,
 and the triage desk must sort them into red, orange, yellow, and green priority levels. Critical
 patients cannot wait behind less serious cases, but patients with the same priority should still be
 treated in the order they arrived. The old paper queue caused dangerous delays when the desk was
 overloaded, so developer Lungelo builds a priority queue from scratch to keep the flow safe and
 predictable. Four FIFO lanes are the right fit because each severity band needs its own queue, and
 the triage desk always serves the highest-priority non-empty lane first. This preserves urgency
 across levels while still guaranteeing FIFO order inside each level.
*/

/**
 * Simulates Baragwanath Hospital's emergency triage system using a custom priority queue.
 * Patients are grouped by severity and served from the highest-priority non-empty lane first.
 */
public class BaragwanathTriageApp {

    /**
     * Represents the four triage priority bands used by the hospital.
     * Higher values mean more urgent treatment.
     */
    enum TriageLevel {
        RED(4),
        ORANGE(3),
        YELLOW(2),
        GREEN(1);

        private final int urgencyRank;

        /**
         * Stores the numeric urgency rank for one triage band.
         *
         * @param urgencyRank higher numbers mean higher priority
         */
        TriageLevel(int urgencyRank) {
            // Time: O(1) — one field assignment.
            this.urgencyRank = urgencyRank;
        }

        /**
         * Returns the urgency rank for comparisons.
         *
         * @return urgency rank value
         */
        int getUrgencyRank() {
            // Time: O(1) — direct field read.
            return urgencyRank;
        }
    }

    /**
     * Stores one patient waiting in a specific triage lane.
     * The record keeps the patient's details and the next pointer for lane traversal.
     */
    static class PatientRecord {
        String patientId;
        String patientName;
        String presentingComplaint;
        int severityScore;
        TriageLevel triageLevel;
        PatientRecord nextPatient;

        /**
         * Creates one emergency patient record.
         *
         * @param patientId hospital case number
         * @param patientName patient's name
         * @param presentingComplaint reason for emergency visit
         * @param severityScore numeric severity score used to map to a priority band
         * @param triageLevel assigned triage level
         */
        PatientRecord(String patientId, String patientName, String presentingComplaint,
                      int severityScore, TriageLevel triageLevel) {
            // Time: O(1) — constant field assignments.
            this.patientId = patientId;
            this.patientName = patientName;
            this.presentingComplaint = presentingComplaint;
            this.severityScore = severityScore;
            this.triageLevel = triageLevel;
            this.nextPatient = null;
        }
    }

    /**
     * A circular queue used for one triage level.
     * FIFO is preserved inside the level, and the fixed array reuses empty slots efficiently.
     */
    static class LevelCircularQueue {
        private final PatientRecord[] triageBuffer;
        private int frontIndex;
        private int rearIndex;
        private int waitingCount;
        private final String levelName;

        /**
         * Creates a circular queue with fixed capacity for one triage lane.
         *
         * @param capacity maximum number of patients in the level lane
         * @param levelName display name of the level
         */
        LevelCircularQueue(int capacity, String levelName) {
            // Time: O(1) — array allocation and field setup.
            this.triageBuffer = new PatientRecord[capacity];
            this.frontIndex = 0;
            this.rearIndex = -1;
            this.waitingCount = 0;
            this.levelName = levelName;
        }

        /**
         * Checks whether this level has no waiting patients.
         *
         * @return true if the lane is empty
         */
        boolean isEmpty() {
            // Time: O(1) — one counter comparison.
            return waitingCount == 0;
        }

        /**
         * Checks whether this level lane is full.
         *
         * @return true if no more patients can fit in this lane
         */
        boolean isFull() {
            // Time: O(1) — one counter comparison.
            return waitingCount == triageBuffer.length;
        }

        /**
         * Adds a patient to the back of this level's lane.
         *
         * @param patient patient record to place in the lane
         * @return true if the patient was added
         */
        boolean enqueue(PatientRecord patient) {
            // Time: O(1) — circular index arithmetic and a single write.
            if (isFull()) {
                System.out.println("[QUEUE FULL] " + levelName + " lane is full for " + patient.patientId + ".");
                return false;
            }

            rearIndex = (rearIndex + 1) % triageBuffer.length;
            triageBuffer[rearIndex] = patient;
            waitingCount++;
            return true;
        }

        /**
         * Removes the front patient from this level's lane.
         *
         * @return the next patient to treat, or null if empty
         */
        PatientRecord dequeue() {
            // Time: O(1) — read front, move pointer, update count.
            if (isEmpty()) {
                return null;
            }

            PatientRecord nextPatient = triageBuffer[frontIndex];
            triageBuffer[frontIndex] = null;
            frontIndex = (frontIndex + 1) % triageBuffer.length;
            waitingCount--;

            if (waitingCount == 0) {
                frontIndex = 0;
                rearIndex = -1;
            }

            return nextPatient;
        }

        /**
         * Looks at the front patient without removing them.
         *
         * @return front patient or null if empty
         */
        PatientRecord peek() {
            // Time: O(1) — direct front access.
            if (isEmpty()) {
                return null;
            }
            return triageBuffer[frontIndex];
        }

        /**
         * Removes a patient by ID from this lane if found.
         *
         * @param patientId case number to remove
         * @return true if the patient was found and removed
         */
        boolean removeById(String patientId) {
            // Time: O(n) — may scan every patient in this lane.
            if (isEmpty()) {
                return false;
            }

            int scanIndex = frontIndex;
            int scanned = 0;

            while (scanned < waitingCount) {
                PatientRecord currentPatient = triageBuffer[scanIndex];
                if (currentPatient != null && currentPatient.patientId.equalsIgnoreCase(patientId)) {
                    removeAtIndex(scanIndex);
                    return true;
                }
                scanIndex = (scanIndex + 1) % triageBuffer.length;
                scanned++;
            }

            return false;
        }

        /**
         * Returns the number of patients waiting in this level.
         *
         * @return queue size
         */
        int getWaitingCount() {
            // Time: O(1) — returns stored counter.
            return waitingCount;
        }

        /**
         * Prints this lane in arrival order.
         */
        void printLaneSnapshot() {
            // Time: O(n) — each patient in the lane is printed once.
            System.out.println("  " + levelName + " lane [" + waitingCount + " waiting]");
            if (isEmpty()) {
                System.out.println("    No patients waiting.");
                return;
            }

            int scanIndex = frontIndex;
            for (int position = 1; position <= waitingCount; position++) {
                PatientRecord currentPatient = triageBuffer[scanIndex];
                System.out.println("    " + position + ". " + currentPatient.patientId
                        + " | " + currentPatient.patientName
                        + " | " + currentPatient.presentingComplaint);
                scanIndex = (scanIndex + 1) % triageBuffer.length;
            }
        }

        /**
         * Estimates the waiting minutes for a patient in this lane.
         *
         * @param minutesPerPatient average treatment time
         * @return estimated wait time in minutes
         */
        int estimateWaitMinutes(int minutesPerPatient) {
            // Time: O(1) — arithmetic based on queue depth.
            return waitingCount * minutesPerPatient;
        }

        /**
         * Removes a patient at a specific buffer index and preserves circular order.
         *
         * @param targetIndex index inside the circular buffer
         */
        private void removeAtIndex(int targetIndex) {
            // Time: O(n) — may shift subsequent patients one slot inside the lane.
            int currentIndex = targetIndex;
            while (currentIndex != rearIndex) {
                int nextIndex = (currentIndex + 1) % triageBuffer.length;
                triageBuffer[currentIndex] = triageBuffer[nextIndex];
                currentIndex = nextIndex;
            }

            triageBuffer[rearIndex] = null;
            rearIndex = (rearIndex - 1 + triageBuffer.length) % triageBuffer.length;
            waitingCount--;

            if (waitingCount == 0) {
                frontIndex = 0;
                rearIndex = -1;
            }
        }
    }

    /**
     * Priority queue built from four FIFO lanes.
     * The highest non-empty triage level is always treated first.
     */
    static class TriagePriorityQueue {
        private final LevelCircularQueue redLane;
        private final LevelCircularQueue orangeLane;
        private final LevelCircularQueue yellowLane;
        private final LevelCircularQueue greenLane;

        /**
         * Creates the triage system with fixed capacity per level.
         *
         * @param laneCapacity capacity for each priority lane
         */
        TriagePriorityQueue(int laneCapacity) {
            // Time: O(1) — create four lanes.
            this.redLane = new LevelCircularQueue(laneCapacity, "Red (critical)");
            this.orangeLane = new LevelCircularQueue(laneCapacity, "Orange (urgent)");
            this.yellowLane = new LevelCircularQueue(laneCapacity, "Yellow (semi-urgent)");
            this.greenLane = new LevelCircularQueue(laneCapacity, "Green (non-urgent)");
        }

        /**
         * Adds a patient to the correct triage lane based on severity.
         *
         * @param patientId case number
         * @param patientName patient name
         * @param presentingComplaint complaint description
         * @param severityScore numeric severity score
         */
        void enqueuePatient(String patientId, String patientName, String presentingComplaint, int severityScore) {
            // Time: O(1) — choose one of four fixed lanes and enqueue once.
            TriageLevel level = mapSeverityToLevel(severityScore);
            PatientRecord newPatient = new PatientRecord(patientId, patientName, presentingComplaint, severityScore, level);
            boolean accepted = laneFor(level).enqueue(newPatient);
            if (accepted) {
                System.out.println("[TRIAGED] " + patientId + " -> " + level + " lane");
            }
        }

        /**
         * Treats the next patient by picking the highest-priority non-empty lane.
         *
         * @return treated patient record, or null if all lanes are empty
         */
        PatientRecord dequeueNextPatient() {
            // Time: O(1) — only four fixed lane checks.
            LevelCircularQueue chosenLane = highestPriorityLaneWithPatients();
            if (chosenLane == null) {
                System.out.println("[DEQUEUE FAILED] No patients waiting in triage.");
                return null;
            }

            PatientRecord nextToTreat = chosenLane.dequeue();
            System.out.println("[TREATED] " + nextToTreat.patientId + " | " + nextToTreat.patientName
                    + " from " + nextToTreat.triageLevel + "");
            return nextToTreat;
        }

        /**
         * Looks at the next patient without removing them.
         *
         * @return next patient to treat or null if empty
         */
        PatientRecord peekNextPatient() {
            // Time: O(1) — checks four fixed lanes only.
            LevelCircularQueue chosenLane = highestPriorityLaneWithPatients();
            if (chosenLane == null) {
                return null;
            }
            return chosenLane.peek();
        }

        /**
         * Removes a patient by ID from whichever lane they are waiting in.
         *
         * @param patientId case number to discharge from the waiting system
         */
        void dischargePatientById(String patientId) {
            // Time: O(n) — may search all patients across all four lanes.
            if (redLane.removeById(patientId)) {
                System.out.println("[DISCHARGED] " + patientId + " removed from red lane.");
                return;
            }
            if (orangeLane.removeById(patientId)) {
                System.out.println("[DISCHARGED] " + patientId + " removed from orange lane.");
                return;
            }
            if (yellowLane.removeById(patientId)) {
                System.out.println("[DISCHARGED] " + patientId + " removed from yellow lane.");
                return;
            }
            if (greenLane.removeById(patientId)) {
                System.out.println("[DISCHARGED] " + patientId + " removed from green lane.");
                return;
            }
            System.out.println("[DISCHARGE FAILED] " + patientId + " not found in triage.");
        }

        /**
         * Prints the full triage dashboard.
         */
        void printTriageDashboard() {
            // Time: O(n) — prints every patient across all lanes once.
            System.out.println("\n=== Baragwanath Triage Dashboard ===");
            redLane.printLaneSnapshot();
            orangeLane.printLaneSnapshot();
            yellowLane.printLaneSnapshot();
            greenLane.printLaneSnapshot();

            System.out.println("\nEstimated wait minutes by lane:");
            System.out.println("  Red: " + redLane.estimateWaitMinutes(8));
            System.out.println("  Orange: " + orangeLane.estimateWaitMinutes(6));
            System.out.println("  Yellow: " + yellowLane.estimateWaitMinutes(4));
            System.out.println("  Green: " + greenLane.estimateWaitMinutes(2));
        }

        /**
         * Returns the total number of waiting patients.
         *
         * @return total waiting count
         */
        int getTotalWaitingCount() {
            // Time: O(1) — fixed four counter reads.
            return redLane.getWaitingCount() + orangeLane.getWaitingCount()
                    + yellowLane.getWaitingCount() + greenLane.getWaitingCount();
        }

        /**
         * Maps severity score to a triage band.
         *
         * @param severityScore numeric severity score
         * @return the matching triage level
         */
        private TriageLevel mapSeverityToLevel(int severityScore) {
            // Time: O(1) — simple threshold checks.
            if (severityScore >= 90) {
                return TriageLevel.RED;
            }
            if (severityScore >= 70) {
                return TriageLevel.ORANGE;
            }
            if (severityScore >= 50) {
                return TriageLevel.YELLOW;
            }
            return TriageLevel.GREEN;
        }

        /**
         * Returns the lane for a given triage level.
         *
         * @param level triage band
         * @return corresponding circular lane queue
         */
        private LevelCircularQueue laneFor(TriageLevel level) {
            // Time: O(1) — one of four fixed branches.
            if (level == TriageLevel.RED) {
                return redLane;
            }
            if (level == TriageLevel.ORANGE) {
                return orangeLane;
            }
            if (level == TriageLevel.YELLOW) {
                return yellowLane;
            }
            return greenLane;
        }

        /**
         * Returns the highest-priority lane that still has patients waiting.
         *
         * @return highest non-empty lane, or null if all are empty
         */
        private LevelCircularQueue highestPriorityLaneWithPatients() {
            // Time: O(1) — four fixed checks in severity order.
            if (!redLane.isEmpty()) {
                return redLane;
            }
            if (!orangeLane.isEmpty()) {
                return orangeLane;
            }
            if (!yellowLane.isEmpty()) {
                return yellowLane;
            }
            if (!greenLane.isEmpty()) {
                return greenLane;
            }
            return null;
        }
    }

    /**
     * Runs a realistic emergency triage scenario with mixed severities and edge cases.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall — printing and lane operations dominate.
        TriagePriorityQueue triageQueue = new TriagePriorityQueue(5);

        System.out.println("=== Chris Hani Baragwanath Hospital Emergency Triage ===");

        // Edge case: nothing waiting.
        triageQueue.dequeueNextPatient();
        triageQueue.dischargePatientById("TR-999");

        // Patients arrive with different severity levels.
        System.out.println("\n-- Incoming patients --");
        triageQueue.enqueuePatient("TR-001", "Nomsa Mokoena", "Chest pain and shortness of breath", 95);
        triageQueue.enqueuePatient("TR-002", "Pieter Jacobs", "Broken arm", 62);
        triageQueue.enqueuePatient("TR-003", "Lerato Dlamini", "Severe bleeding", 88);
        triageQueue.enqueuePatient("TR-004", "Andile Maseko", "Mild headache", 30);
        triageQueue.enqueuePatient("TR-005", "Nomsa Mokoena", "Follow-up pain escalation", 95);

        // Duplicate value edge case: same patient name appears again, both are kept in FIFO order.
        triageQueue.enqueuePatient("TR-006", "Pieter Jacobs", "Return visit for bandage check", 62);

        triageQueue.printTriageDashboard();

        // Single patient treatment from the highest-priority lane.
        System.out.println("\n-- Treatment begins --");
        triageQueue.dequeueNextPatient();

        // Peek at the next patient without removing them.
        PatientRecord nextPatient = triageQueue.peekNextPatient();
        if (nextPatient != null) {
            System.out.println("[PEEK] Next patient: " + nextPatient.patientId + " | " + nextPatient.patientName
                    + " | " + nextPatient.triageLevel);
        }

        // Not-found and found discharge scenarios.
        triageQueue.dischargePatientById("TR-999");
        triageQueue.dischargePatientById("TR-006");

        triageQueue.printTriageDashboard();

        // Drain the remaining queue to show priority order plus FIFO within level.
        System.out.println("\n-- Remaining patients treated in priority order --");
        while (triageQueue.getTotalWaitingCount() > 0) {
            triageQueue.dequeueNextPatient();
        }

        // Edge case: empty after full drain.
        triageQueue.dequeueNextPatient();
        triageQueue.printTriageDashboard();
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - FIFO: Within each triage level, the first patient to arrive is treated first.
 - enqueue: Add a patient to the back of the correct severity lane.
 - dequeue: Remove the front patient from the highest-priority non-empty lane.
 - circular queue: Each lane reuses fixed storage by wrapping indices around.
 - priority queue: Higher-severity lanes are always served before lower-severity lanes.
 - traversal: The dashboard walks through each lane to print waiting patients.
 - stable ordering: Patients with the same severity stay in arrival order.

 Big-O for operations implemented:
 - isEmpty / isFull in each lane: O(1) because they check a stored counter.
 - enqueue in a lane: O(1) because it writes one slot and updates indices.
 - dequeue in a lane: O(1) because it removes one front item and moves the pointer.
 - peek in a lane: O(1) because it reads the front slot only.
 - removeById in a lane: O(n) because it may scan the entire lane.
 - printLaneSnapshot: O(n) because it prints every waiting patient once.
 - enqueuePatient: O(1) because only one lane is chosen and updated.
 - dequeueNextPatient: O(1) because it checks four fixed lanes in priority order.
 - peekNextPatient: O(1) because it checks four fixed lanes in priority order.
 - dischargePatientById: O(n) because it may search all four lanes.
 - printTriageDashboard: O(n) because it prints every waiting patient in every lane.
 Space complexity: O(n) because the lanes store one record per waiting patient.

 Interview questions this code prepares you for:
 - Why is a priority queue the right structure for triage?
 - How do you preserve FIFO ordering within each priority band?
 - Why are enqueue and dequeue O(1) in a circular queue lane?
 - What is the difference between a normal queue and a priority queue?
 - How would you remove a waiting patient by ID from a priority system?

 Most common mistake and how this code avoids it:
 - Mistake: Using a single FIFO queue for triage, which would let less urgent patients block critical ones.
 - Avoided: The implementation separates patients into four severity lanes and always serves the highest lane first.

 When to use this vs the common alternative:
 - Use a priority queue when urgency matters more than arrival time, such as emergency triage or CPU scheduling.
 - Use a plain queue when everyone must be served strictly in arrival order.
 - Built-in comparison: Java's PriorityQueue is useful, but it does not preserve FIFO order among equal priorities unless you add extra tie-breaking logic.
*/
