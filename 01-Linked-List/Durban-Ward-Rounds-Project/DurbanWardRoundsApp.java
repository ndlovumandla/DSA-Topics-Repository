/*
 The morning ward round at King Edward VIII Hospital in Durban follows a fixed bed sequence,
 but real hospital flow is dynamic because overnight admissions and discharges happen often.
 Zanele used paper arrows to keep order, while Sipho modeled each patient as a linked node.
 A linked list is a strong fit here because beds can be inserted or removed without shifting an entire array.
 The doctor can move forward through beds using next references and move backward using previous references.
 This mirrors real ward behavior: continue the round, then quickly revisit a patient if needed.
 For learning fundamentals, this example shows node structure, traversal, insertion, and deletion from scratch.
*/

/**
 * Demonstrates how a Durban hospital ward round can be modeled using linked list fundamentals.
 * The app prints a story-driven simulation of admissions, discharges, and doctor traversal.
 */
public class DurbanWardRoundsApp {

    /**
     * Represents one patient in one bed position in the ward round chain.
     * The node keeps links to both neighboring beds for forward and backward movement.
     */
    static class PatientBedNode {
        String patientName;
        int bedNumber;
        PatientBedNode nextBed;
        PatientBedNode previousBed;

        /**
         * Creates a patient node for the ward chain.
         *
         * @param patientName name used by the duty doctor during rounds
         * @param bedNumber clinical bed reference in the ward
         */
        PatientBedNode(String patientName, int bedNumber) {
            // Time: O(1) - direct field assignments only.
            this.patientName = patientName;
            this.bedNumber = bedNumber;
            this.nextBed = null;
            this.previousBed = null;
        }
    }

    /**
     * Models the full ward round chain as a linked structure managed by Sipho.
     * The head points to the first bed visited in the morning round.
     */
    static class DurbanWardRoundList {
        private PatientBedNode wardHead;
        private PatientBedNode wardTail;
        private int patientCount;

        /**
         * Starts with an empty ward chain before admissions are loaded.
         */
        DurbanWardRoundList() {
            // Time: O(1) - constant initialization.
            this.wardHead = null;
            this.wardTail = null;
            this.patientCount = 0;
        }

        /**
         * Checks whether the ward has no patients in the current chain.
         *
         * @return true when no patient nodes exist
         */
        boolean isWardEmpty() {
            // Time: O(1) - single pointer check.
            return wardHead == null;
        }

        /**
         * Admits a patient at the front of the ward chain.
         * This demonstrates O(1) insertion at head in linked lists.
         *
         * @param patientName patient to place at the first visit position
         * @param bedNumber bed number recorded for that patient
         */
        void admitAtHead(String patientName, int bedNumber) {
            // Time: O(1) - update a fixed number of references.
            PatientBedNode admittedPatient = new PatientBedNode(patientName, bedNumber);

            // Linked-list insertion at head: new node points to old head.
            admittedPatient.nextBed = wardHead;

            // If list was not empty, old head now points back to new head.
            if (wardHead != null) {
                wardHead.previousBed = admittedPatient;
            }

            // Move head pointer to the newly admitted patient.
            wardHead = admittedPatient;

            // If this was the first patient, tail must match head.
            if (wardTail == null) {
                wardTail = admittedPatient;
            }

            patientCount++;
            System.out.println("[Admission] " + patientName + " placed at start of rounds (bed " + bedNumber + ").");
        }

        /**
         * Admits a patient after a specific bed, useful when a clinical insertion is needed.
         *
         * @param targetBedNumber existing bed after which to insert
         * @param patientName patient being admitted
         * @param bedNumber bed number allocated to the new patient
         */
        void admitAfterBed(int targetBedNumber, String patientName, int bedNumber) {
            // Time: O(n) - may traverse through the chain to find target bed.
            PatientBedNode targetBedNode = findNodeByBedNumber(targetBedNumber);

            // Edge case: insertion point not found.
            if (targetBedNode == null) {
                System.out.println("[Admission Failed] Bed " + targetBedNumber + " not found. "
                        + patientName + " could not be inserted.");
                return;
            }

            PatientBedNode admittedPatient = new PatientBedNode(patientName, bedNumber);

            // Save original next node so we can stitch links correctly.
            PatientBedNode originalNext = targetBedNode.nextBed;

            // Forward link: target now points to new patient.
            targetBedNode.nextBed = admittedPatient;

            // Backward link: new patient points to target.
            admittedPatient.previousBed = targetBedNode;

            // Forward link from new patient to old next node.
            admittedPatient.nextBed = originalNext;

            // If there was a node after target, reconnect its backward pointer.
            if (originalNext != null) {
                originalNext.previousBed = admittedPatient;
            } else {
                // If inserted at end, update tail reference.
                wardTail = admittedPatient;
            }

            patientCount++;
            System.out.println("[Admission] " + patientName + " inserted after bed " + targetBedNumber
                    + " (new bed " + bedNumber + ").");
        }

        /**
         * Discharges a patient by name, removing one node from the middle/head/tail safely.
         *
         * @param patientName patient to remove from the ward chain
         */
        void dischargeByName(String patientName) {
            // Time: O(n) - may scan nodes until the patient name is found.
            PatientBedNode patientCursor = wardHead;

            // Traversal over next pointers (singly linked behavior) to locate the target node.
            while (patientCursor != null && !patientCursor.patientName.equalsIgnoreCase(patientName)) {
                patientCursor = patientCursor.nextBed;
            }

            // Edge case: patient not found.
            if (patientCursor == null) {
                System.out.println("[Discharge Failed] " + patientName + " not found in the ward chain.");
                return;
            }

            // If removing head, shift head to next node.
            if (patientCursor.previousBed == null) {
                wardHead = patientCursor.nextBed;
            } else {
                // Bridge previous node over removed node.
                patientCursor.previousBed.nextBed = patientCursor.nextBed;
            }

            // If removing tail, shift tail to previous node.
            if (patientCursor.nextBed == null) {
                wardTail = patientCursor.previousBed;
            } else {
                // Bridge next node back to previous node.
                patientCursor.nextBed.previousBed = patientCursor.previousBed;
            }

            patientCount--;
            System.out.println("[Discharge] " + patientCursor.patientName + " removed from bed "
                    + patientCursor.bedNumber + ".");
        }

        /**
         * Prints the ward round from head to tail.
         * This uses only next pointers, so conceptually it matches singly linked traversal.
         */
        void traverseMorningRoundForward() {
            // Time: O(n) - visits each patient at most once.
            System.out.println("\nDoctor starts morning ward round (forward traversal):");

            // Edge case: empty structure.
            if (isWardEmpty()) {
                System.out.println("  Ward is empty. No patients to review.");
                return;
            }

            PatientBedNode doctorCursor = wardHead;
            while (doctorCursor != null) {
                System.out.println("  Review -> Bed " + doctorCursor.bedNumber + ": " + doctorCursor.patientName);
                doctorCursor = doctorCursor.nextBed;
            }
        }

        /**
         * Prints the ward from tail to head for revisit rounds.
         * This demonstrates the extra power of doubly linked lists via previous pointers.
         */
        void traverseRevisitBackward() {
            // Time: O(n) - visits each patient at most once in reverse.
            System.out.println("\nDoctor performs backward revisit traversal:");

            // Edge case: empty structure.
            if (isWardEmpty()) {
                System.out.println("  Ward is empty. No patients to revisit.");
                return;
            }

            PatientBedNode doctorCursor = wardTail;
            while (doctorCursor != null) {
                System.out.println("  Revisit <- Bed " + doctorCursor.bedNumber + ": " + doctorCursor.patientName);
                doctorCursor = doctorCursor.previousBed;
            }
        }

        /**
         * Shows current patient count for quick admin verification.
         *
         * @return number of patient nodes in the chain
         */
        int getPatientCount() {
            // Time: O(1) - returns stored size value.
            return patientCount;
        }

        /**
         * Finds a node by bed number to support targeted insertion logic.
         *
         * @param bedNumber bed to locate in the linked chain
         * @return matching node or null when not found
         */
        private PatientBedNode findNodeByBedNumber(int bedNumber) {
            // Time: O(n) - sequential scan through next pointers.
            PatientBedNode bedCursor = wardHead;
            while (bedCursor != null) {
                if (bedCursor.bedNumber == bedNumber) {
                    return bedCursor;
                }
                bedCursor = bedCursor.nextBed;
            }
            return null;
        }
    }

    /**
     * Runs a story-driven simulation of ward rounds, admissions, and discharges.
     *
     * @param args command-line arguments (not used for this demo)
     */
    public static void main(String[] args) {
        // Time: O(n) overall for demo actions because traversals and searches dominate.
        DurbanWardRoundList durbanWard = new DurbanWardRoundList();

        System.out.println("=== King Edward VIII Hospital: Durban Ward Round Simulation ===");

        // Edge case demo: traversing an empty linked list.
        durbanWard.traverseMorningRoundForward();

        // Single-element demo: admit one patient, then traverse both directions.
        durbanWard.admitAtHead("Mr. Naidoo", 1);
        durbanWard.traverseMorningRoundForward();
        durbanWard.traverseRevisitBackward();

        // Build a realistic chain with insertions at head and after a target bed.
        durbanWard.admitAtHead("Ms. Khumalo", 0);
        durbanWard.admitAfterBed(1, "Mrs. Dlamini", 2);
        durbanWard.admitAfterBed(2, "Mr. Mthembu", 3);

        // Edge case demo: insertion after non-existent bed.
        durbanWard.admitAfterBed(40, "Baby Pillay", 41);

        durbanWard.traverseMorningRoundForward();
        durbanWard.traverseRevisitBackward();

        // Deletion from middle of chain.
        durbanWard.dischargeByName("Mrs. Dlamini");

        // Edge case demo: deleting a patient who does not exist.
        durbanWard.dischargeByName("Dr. Ghost");

        durbanWard.traverseMorningRoundForward();

        // Remove all remaining patients to show transition back to empty state.
        durbanWard.dischargeByName("Ms. Khumalo");
        durbanWard.dischargeByName("Mr. Naidoo");
        durbanWard.dischargeByName("Mr. Mthembu");

        System.out.println("\nFinal patient count: " + durbanWard.getPatientCount());
        durbanWard.traverseMorningRoundForward();
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Node: A patient record is wrapped in a node that stores data plus pointers.
 - head: The first patient in the chain, where forward traversal begins.
 - next pointer: Link to the next bed in sequence; enables singly linked traversal.
 - singly linked: Forward-only movement is enough for standard morning rounds.
 - doubly linked: Adding previous pointers supports efficient backward revisits.
 - traversal: Sequentially visiting nodes one by one to process all patients.
 - insertion: Rewiring pointers to place a new patient between existing nodes.
 - deletion: Rewiring pointers to remove a node without shifting other data.

 Big-O of operations implemented:
 - admitAtHead: O(1) because only a constant number of pointers are changed.
 - admitAfterBed: O(n) because finding the target bed may require scanning the list.
 - dischargeByName: O(n) because locating a name requires traversal in the worst case.
 - traverseMorningRoundForward: O(n) because each node is visited once.
 - traverseRevisitBackward: O(n) because each node is visited once in reverse.
 - isWardEmpty: O(1) because it checks one pointer.
 - getPatientCount: O(1) because size is stored and returned directly.
 - findNodeByBedNumber: O(n) because it sequentially scans nodes.

 Interview questions this prepares you for:
 - How do insertion and deletion differ between arrays and linked lists?
 - Why is insertion at the head of a linked list O(1)?
 - How do you delete a node safely in a doubly linked list?
 - When would you choose singly linked over doubly linked?
 - What pointer updates are required when deleting head or tail?

 Common mistake and how this code avoids it:
 - Mistake: Updating only one side of links (next but not previous), causing broken chains.
 - Avoided here by always rewiring both directions during insertion and deletion.

 Comparison with common alternative:
 - Use linked lists when frequent middle insertions/deletions are needed and index access is less important.
 - Use arrays/ArrayList when random indexing is frequent and shifting costs are acceptable.
*/