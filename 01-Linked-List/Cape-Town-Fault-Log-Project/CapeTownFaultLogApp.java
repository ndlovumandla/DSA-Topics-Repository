/*
 City Power Cape Town receives fault calls rapidly during load-shedding, and every report has a
 fault number, suburb, severity, and logged time that must be tracked clearly. The old fixed-size
 array system failed when the number of faults surged because capacity was limited and insertion in
 priority order required expensive shifting. Naledi rebuilt the workflow as a linked structure so each
 new report becomes a node connected by pointers, allowing the chain to grow dynamically. The list is
 kept in severity order so critical faults are handled first, while new emergency calls can be inserted
 at the head in O(1) time. Resolved faults are removed by rewiring pointers instead of shifting memory,
 and supervisors can traverse the chain to produce real-time operational reports.
*/

/**
 * Runs an end-to-end simulation of Cape Town's load-shedding fault logging process.
 * The demo shows insertion, traversal, deletion, and edge-case handling with linked lists.
 */
public class CapeTownFaultLogApp {

    /**
     * Represents one electrical fault report in the singly linked chain.
     * Each node carries fault data and a next pointer to the following report.
     */
    static class FaultReportNode {
        String faultNumber;
        String suburb;
        int severityLevel;
        String loggedTime;
        FaultReportNode nextFault;

        /**
         * Creates a node for one reported fault.
         *
         * @param faultNumber unique or operator-entered tracking number
         * @param suburb area where the fault occurred
         * @param severityLevel priority level where higher is more urgent
         * @param loggedTime timestamp string captured at report time
         */
        FaultReportNode(String faultNumber, String suburb, int severityLevel, String loggedTime) {
            // Time: O(1) - only constant field assignments are performed.
            this.faultNumber = faultNumber;
            this.suburb = suburb;
            this.severityLevel = severityLevel;
            this.loggedTime = loggedTime;
            this.nextFault = null;
        }
    }

    /**
     * Represents a mirrored doubly linked snapshot for reverse audit traversal.
     * This is built from the singly list to demonstrate previous pointers explicitly.
     */
    static class FaultReportDoublyNode {
        String faultNumber;
        String suburb;
        int severityLevel;
        String loggedTime;
        FaultReportDoublyNode previousFault;
        FaultReportDoublyNode nextFault;

        /**
         * Creates a doubly linked snapshot node with the same fault data.
         *
         * @param faultNumber tracking number copied from the live list
         * @param suburb suburb copied from the live list
         * @param severityLevel severity copied from the live list
         * @param loggedTime logged time copied from the live list
         */
        FaultReportDoublyNode(String faultNumber, String suburb, int severityLevel, String loggedTime) {
            // Time: O(1) - constant number of assignments.
            this.faultNumber = faultNumber;
            this.suburb = suburb;
            this.severityLevel = severityLevel;
            this.loggedTime = loggedTime;
            this.previousFault = null;
            this.nextFault = null;
        }
    }

    /**
     * Bundles head and tail for a doubly linked snapshot to support reverse traversal.
     */
    static class DoublySnapshot {
        FaultReportDoublyNode snapshotHead;
        FaultReportDoublyNode snapshotTail;

        /**
         * Creates a container for snapshot boundaries.
         *
         * @param snapshotHead first snapshot node
         * @param snapshotTail last snapshot node
         */
        DoublySnapshot(FaultReportDoublyNode snapshotHead, FaultReportDoublyNode snapshotTail) {
            // Time: O(1) - stores two references.
            this.snapshotHead = snapshotHead;
            this.snapshotTail = snapshotTail;
        }
    }

    /**
     * Maintains Cape Town's active fault queue using a custom singly linked list.
     * The chain is ordered by severity so resolution priorities match operational policy.
     */
    static class CapeTownFaultLogList {
        private FaultReportNode logHead;
        private int activeFaultCount;

        /**
         * Initializes an empty fault log before night-shift reports begin.
         */
        CapeTownFaultLogList() {
            // Time: O(1) - initialize references and counters.
            this.logHead = null;
            this.activeFaultCount = 0;
        }

        /**
         * Checks if no fault reports are currently active.
         *
         * @return true if the linked structure has no nodes
         */
        boolean isLogEmpty() {
            // Time: O(1) - single pointer check.
            return logHead == null;
        }

        /**
         * Inserts an emergency report directly at head in constant time.
         * This demonstrates the classic O(1) head insertion property of linked lists.
         *
         * @param faultNumber fault reference number
         * @param suburb suburb where outage fault exists
         * @param severityLevel emergency severity level
         * @param loggedTime report time
         */
        void logCriticalFaultAtHead(String faultNumber, String suburb, int severityLevel, String loggedTime) {
            // Time: O(1) - only a constant number of pointer updates.
            FaultReportNode newCriticalFault = new FaultReportNode(faultNumber, suburb, severityLevel, loggedTime);

            // Singly linked concept: new node points at old head.
            newCriticalFault.nextFault = logHead;

            // Head pointer is moved to the newly inserted node.
            logHead = newCriticalFault;
            activeFaultCount++;

            System.out.println("[Critical Insert @ Head] " + faultNumber + " in " + suburb
                    + " (severity " + severityLevel + ", time " + loggedTime + ")");
        }

        /**
         * Inserts a fault so the list remains sorted by descending severity.
         * Equal severities keep arrival order to preserve fairness within same priority.
         *
         * @param faultNumber fault reference number
         * @param suburb suburb where outage fault exists
         * @param severityLevel fault urgency level
         * @param loggedTime report time
         */
        void logFaultBySeverity(String faultNumber, String suburb, int severityLevel, String loggedTime) {
            // Time: O(n) - may traverse to find the insertion position.
            FaultReportNode newFault = new FaultReportNode(faultNumber, suburb, severityLevel, loggedTime);

            // Duplicate edge case: allow duplicates but warn supervisors for data hygiene.
            if (containsFaultNumber(faultNumber)) {
                System.out.println("[Duplicate Warning] Fault number " + faultNumber
                        + " already exists. Inserting duplicate entry for audit traceability.");
            }

            // If list is empty or new fault outranks current head, insert at head.
            if (logHead == null || severityLevel > logHead.severityLevel) {
                newFault.nextFault = logHead;
                logHead = newFault;
                activeFaultCount++;
                System.out.println("[Severity Insert] " + faultNumber + " became new head due to higher severity.");
                return;
            }

            FaultReportNode faultCursor = logHead;

            // Traversal concept: walk via next pointers to find insertion slot.
            // Using >= keeps existing equal-severity faults ahead (stable ordering).
            while (faultCursor.nextFault != null && faultCursor.nextFault.severityLevel >= severityLevel) {
                faultCursor = faultCursor.nextFault;
            }

            // Pointer rewiring inserts new node between cursor and cursor.next.
            newFault.nextFault = faultCursor.nextFault;
            faultCursor.nextFault = newFault;
            activeFaultCount++;

            System.out.println("[Severity Insert] " + faultNumber + " inserted after " + faultCursor.faultNumber + ".");
        }

        /**
         * Resolves and removes the first matching fault number from the active chain.
         *
         * @param faultNumber fault to remove after repair completion
         */
        void resolveFirstFaultByNumber(String faultNumber) {
            // Time: O(n) - worst case scans to the tail for a match.
            if (logHead == null) {
                System.out.println("[Resolve Failed] Active log is empty.");
                return;
            }

            // Head deletion case: remove first node when it matches.
            if (logHead.faultNumber.equalsIgnoreCase(faultNumber)) {
                System.out.println("[Resolved] " + logHead.faultNumber + " removed from head.");
                logHead = logHead.nextFault;
                activeFaultCount--;
                return;
            }

            FaultReportNode previousFault = logHead;
            FaultReportNode currentFault = logHead.nextFault;

            // Singly linked deletion: track previous node to bypass target node.
            while (currentFault != null && !currentFault.faultNumber.equalsIgnoreCase(faultNumber)) {
                previousFault = currentFault;
                currentFault = currentFault.nextFault;
            }

            // Not-found edge case.
            if (currentFault == null) {
                System.out.println("[Resolve Failed] Fault number " + faultNumber + " not found.");
                return;
            }

            // Bypass the target node so it is removed from the chain.
            previousFault.nextFault = currentFault.nextFault;
            activeFaultCount--;
            System.out.println("[Resolved] " + currentFault.faultNumber + " in " + currentFault.suburb + " removed.");
        }

        /**
         * Resolves all matching duplicates for a given fault number.
         *
         * @param faultNumber fault number to purge from the chain
         */
        void resolveAllDuplicatesByNumber(String faultNumber) {
            // Time: O(n) - single full traversal with pointer updates.
            if (logHead == null) {
                System.out.println("[Duplicate Resolve] Active log is empty.");
                return;
            }

            int removedCount = 0;

            // Remove matching nodes at the head repeatedly (possible duplicate cluster).
            while (logHead != null && logHead.faultNumber.equalsIgnoreCase(faultNumber)) {
                logHead = logHead.nextFault;
                removedCount++;
                activeFaultCount--;
            }

            FaultReportNode previousFault = null;
            FaultReportNode currentFault = logHead;

            // Remove matching nodes in the remaining chain by bypassing duplicates.
            while (currentFault != null) {
                if (currentFault.faultNumber.equalsIgnoreCase(faultNumber)) {
                    previousFault.nextFault = currentFault.nextFault;
                    removedCount++;
                    activeFaultCount--;
                    currentFault = previousFault.nextFault;
                } else {
                    previousFault = currentFault;
                    currentFault = currentFault.nextFault;
                }
            }

            if (removedCount == 0) {
                System.out.println("[Duplicate Resolve] No entries found for " + faultNumber + ".");
            } else {
                System.out.println("[Duplicate Resolve] Removed " + removedCount + " entries for " + faultNumber + ".");
            }
        }

        /**
         * Traverses the live log from head to tail and prints a supervisor report.
         */
        void printActiveFaultStatusReport() {
            // Time: O(n) - visits each node exactly once.
            System.out.println("\nActive Fault Status Report (highest severity first):");

            if (isLogEmpty()) {
                System.out.println("  No active faults in the chain.");
                return;
            }

            FaultReportNode supervisorCursor = logHead;
            while (supervisorCursor != null) {
                System.out.println("  Fault " + supervisorCursor.faultNumber
                        + " | Suburb: " + supervisorCursor.suburb
                        + " | Severity: " + supervisorCursor.severityLevel
                        + " | Logged: " + supervisorCursor.loggedTime);
                supervisorCursor = supervisorCursor.nextFault;
            }
        }

        /**
         * Creates a doubly linked snapshot from the singly chain for reverse audit viewing.
         *
         * @return snapshot containing both head and tail for bidirectional traversal
         */
        DoublySnapshot buildDoublySnapshot() {
            // Time: O(n) - copies each node once and links previous/next references.
            if (logHead == null) {
                return new DoublySnapshot(null, null);
            }

            FaultReportNode sourceCursor = logHead;
            FaultReportDoublyNode snapshotHead = null;
            FaultReportDoublyNode snapshotTail = null;

            while (sourceCursor != null) {
                FaultReportDoublyNode snapshotNode = new FaultReportDoublyNode(
                        sourceCursor.faultNumber,
                        sourceCursor.suburb,
                        sourceCursor.severityLevel,
                        sourceCursor.loggedTime);

                if (snapshotHead == null) {
                    // First copied node becomes both head and tail.
                    snapshotHead = snapshotNode;
                    snapshotTail = snapshotNode;
                } else {
                    // Doubly linked concept: link in both directions.
                    snapshotTail.nextFault = snapshotNode;
                    snapshotNode.previousFault = snapshotTail;
                    snapshotTail = snapshotNode;
                }

                sourceCursor = sourceCursor.nextFault;
            }

            return new DoublySnapshot(snapshotHead, snapshotTail);
        }

        /**
         * Prints faults in reverse using a doubly linked snapshot.
         *
         * @param snapshot doubly linked snapshot built from current active list
         */
        void printReverseAuditFromSnapshot(DoublySnapshot snapshot) {
            // Time: O(n) - reverse traversal through previous pointers touches each node once.
            System.out.println("\nReverse Audit View (doubly linked snapshot):");

            if (snapshot == null || snapshot.snapshotTail == null) {
                System.out.println("  Snapshot is empty; no reverse audit available.");
                return;
            }

            FaultReportDoublyNode reverseCursor = snapshot.snapshotTail;
            while (reverseCursor != null) {
                System.out.println("  Fault " + reverseCursor.faultNumber
                        + " <- Severity " + reverseCursor.severityLevel
                        + " <- " + reverseCursor.suburb);
                reverseCursor = reverseCursor.previousFault;
            }
        }

        /**
         * Returns how many active faults are currently in the chain.
         *
         * @return active node count
         */
        int getActiveFaultCount() {
            // Time: O(1) - value is tracked incrementally.
            return activeFaultCount;
        }

        /**
         * Returns the current highest severity node at head, if available.
         *
         * @return head node or null if list is empty
         */
        FaultReportNode peekHighestSeverityFault() {
            // Time: O(1) - direct head access.
            return logHead;
        }

        /**
         * Checks whether a fault number already exists in the chain.
         *
         * @param faultNumber fault number to search for
         * @return true if at least one matching node exists
         */
        private boolean containsFaultNumber(String faultNumber) {
            // Time: O(n) - linear scan through linked nodes.
            FaultReportNode checkCursor = logHead;
            while (checkCursor != null) {
                if (checkCursor.faultNumber.equalsIgnoreCase(faultNumber)) {
                    return true;
                }
                checkCursor = checkCursor.nextFault;
            }
            return false;
        }
    }

    /**
     * Drives the story scenario: faults are reported, prioritized, resolved, and audited.
     *
     * @param args unused command-line values
     */
    public static void main(String[] args) {
        // Time: O(n) overall for the scenario, dominated by list traversals and searches.
        CapeTownFaultLogList cityPowerFaultLog = new CapeTownFaultLogList();

        System.out.println("=== City Power Cape Town: Load-Shedding Fault Log Simulation ===");

        // Edge case: traversal on empty structure.
        cityPowerFaultLog.printActiveFaultStatusReport();

        // Single-element edge case setup.
        cityPowerFaultLog.logCriticalFaultAtHead("CT-001", "Khayelitsha", 9, "18:05");
        cityPowerFaultLog.printActiveFaultStatusReport();

        // Insert in severity order (descending), demonstrating O(n) placement traversal.
        cityPowerFaultLog.logFaultBySeverity("CT-002", "Mitchells Plain", 6, "18:11");
        cityPowerFaultLog.logFaultBySeverity("CT-003", "Bellville", 8, "18:13");
        cityPowerFaultLog.logFaultBySeverity("CT-004", "Langa", 8, "18:15");

        // Duplicate edge case: same fault number inserted again intentionally.
        cityPowerFaultLog.logFaultBySeverity("CT-003", "Parow", 7, "18:20");

        cityPowerFaultLog.printActiveFaultStatusReport();

        // O(1) peek of head shows current highest priority fault.
        FaultReportNode highestPriorityFault = cityPowerFaultLog.peekHighestSeverityFault();
        if (highestPriorityFault != null) {
            System.out.println("\nHighest priority currently at head: " + highestPriorityFault.faultNumber
                    + " (severity " + highestPriorityFault.severityLevel + ")");
        }

        // Resolve from middle/tail by fault number.
        cityPowerFaultLog.resolveFirstFaultByNumber("CT-004");

        // Not-found edge case.
        cityPowerFaultLog.resolveFirstFaultByNumber("CT-999");

        // Remove duplicate entries by number.
        cityPowerFaultLog.resolveAllDuplicatesByNumber("CT-003");

        cityPowerFaultLog.printActiveFaultStatusReport();

        // Build doubly linked snapshot to demonstrate previous pointers for reverse traversal.
        DoublySnapshot handoverSnapshot = cityPowerFaultLog.buildDoublySnapshot();
        cityPowerFaultLog.printReverseAuditFromSnapshot(handoverSnapshot);

        // Final clear-down to show transition back to empty state.
        cityPowerFaultLog.resolveFirstFaultByNumber("CT-001");
        cityPowerFaultLog.resolveFirstFaultByNumber("CT-002");

        System.out.println("\nFinal active fault count: " + cityPowerFaultLog.getActiveFaultCount());
        cityPowerFaultLog.printActiveFaultStatusReport();
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Node: A node stores one fault report plus pointers to connect it into the chain.
 - head: The head points to the most urgent active fault at the front of the log.
 - next pointer: Each node's next pointer links to the following fault in priority order.
 - singly linked: The live operational log uses one-way links for efficient memory and edits.
 - doubly linked: A snapshot uses previous + next pointers for reverse audit traversal.
 - traversal: Repeatedly following pointers to inspect all active faults.
 - insertion: Rewiring pointers to place a new fault in the correct severity position.
 - deletion: Rewiring pointers to remove resolved faults without array shifting.

 Big-O for operations in this implementation:
 - isLogEmpty: O(1) because only one reference check is needed.
 - logCriticalFaultAtHead: O(1) because insertion updates a constant set of pointers.
 - logFaultBySeverity: O(n) because insertion point search may scan the full list.
 - resolveFirstFaultByNumber: O(n) because the target may be near the tail or absent.
 - resolveAllDuplicatesByNumber: O(n) because each node is visited at most once.
 - printActiveFaultStatusReport: O(n) because it prints each node once.
 - buildDoublySnapshot: O(n) because each live node is copied once.
 - printReverseAuditFromSnapshot: O(n) because reverse traversal visits each snapshot node once.
 - getActiveFaultCount: O(1) because count is maintained incrementally.
 - peekHighestSeverityFault: O(1) because head access is direct.
 - containsFaultNumber: O(n) because it performs a linear search.

 Interview questions this code prepares you for:
 - Why is insertion at the head of a linked list O(1)?
 - How do you delete a node in a singly linked list when you only have head?
 - How do you preserve stable ordering for equal priorities during insertion?
 - What trade-offs exist between singly and doubly linked lists?
 - Why might a linked list outperform an array for frequent insertions/removals?

 Common mistake and how this code avoids it:
 - Mistake: Forgetting to handle head deletion separately, which can break list access.
 - Avoided: Head removal is handled explicitly before general pointer-bypass logic.

 Comparison with the most common alternative:
 - Use linked lists when the system needs frequent insertions/deletions at varying positions and dynamic growth.
 - Use arrays/ArrayList when random index access is frequent and contiguous memory is beneficial.
*/