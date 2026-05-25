/*
 Noord Street taxi rank in Johannesburg manages thousands of commuters daily across over 200 destinations.
 The old clipboard-and-marshal system caused skipped commuters and double-counting, prompting the rank
 authority to hire developer Thabo to build a digital solution. Each commuter is modelled as a node that
 holds their name, destination, and a pointer to the next person in line. Commuters join at the back of
 the queue and board taxis from the front, while disabled or elderly passengers can be moved to the front
 instantly by a marshal. A linked list is the right tool because it grows dynamically without a capacity
 ceiling, allows O(1) front operations, and lets the marshal insert or remove any position by rewiring pointers.
*/

/**
 * Runs a full demonstration of the Noord Street taxi rank queue management system
 * modelled as a custom linked list built entirely from scratch.
 */
public class JoburgTaxiRankApp {

    /**
     * Represents one commuter waiting in the Noord Street queue.
     * Each commuter node holds personal details and a reference to the next commuter.
     */
    static class CommuterNode {
        String commuterName;
        String destination;
        CommuterNode nextCommuter;   // singly linked: forward-only reference
        CommuterNode prevCommuter;   // doubly linked: backward reference for revisit traversal

        /**
         * Creates a commuter node ready to be placed in the queue.
         *
         * @param commuterName full name used by the marshal to call the commuter
         * @param destination  taxi route or suburb the commuter is travelling to
         */
        CommuterNode(String commuterName, String destination) {
            // Time: O(1) — constant field assignments only.
            this.commuterName = commuterName;
            this.destination  = destination;
            this.nextCommuter = null;
            this.prevCommuter = null;
        }
    }

    /**
     * Models the full Noord Street commuter queue as a doubly linked list.
     * The queueFront (head) is where taxis board; the queueBack (tail) is where new arrivals join.
     */
    static class NoorStreetQueue {
        private CommuterNode queueFront;   // head: first commuter about to board
        private CommuterNode queueBack;    // tail: last commuter who joined
        private int commuterCount;

        /**
         * Initialises an empty queue before the morning rush begins.
         */
        NoorStreetQueue() {
            // Time: O(1) — initialise two pointers and a counter.
            this.queueFront   = null;
            this.queueBack    = null;
            this.commuterCount = 0;
        }

        /**
         * Reports whether the queue has anyone waiting.
         *
         * @return true when no commuter nodes exist in the chain
         */
        boolean isQueueEmpty() {
            // Time: O(1) — single null check on head pointer.
            return queueFront == null;
        }

        /**
         * A commuter joins at the back of the queue — the normal arrival path.
         * Demonstrates tail insertion: rewire the old tail's next pointer and update tail.
         *
         * @param commuterName name of the arriving commuter
         * @param destination  their intended taxi destination
         */
        void joinAtBack(String commuterName, String destination) {
            // Time: O(1) — direct access to tail; constant pointer updates.
            CommuterNode arrivingCommuter = new CommuterNode(commuterName, destination);

            if (isQueueEmpty()) {
                // Single-element case: front and back are the same node.
                queueFront = arrivingCommuter;
                queueBack  = arrivingCommuter;
            } else {
                // Doubly linked insertion at tail: wire both directions.
                queueBack.nextCommuter       = arrivingCommuter;  // old tail points forward
                arrivingCommuter.prevCommuter = queueBack;         // new tail points back
                queueBack                    = arrivingCommuter;  // move tail pointer
            }

            commuterCount++;
            System.out.println("[Joined back]  " + commuterName + " waiting for taxi to " + destination);
        }

        /**
         * Marshal inserts an elderly or disabled commuter at the very front — priority boarding.
         * Demonstrates O(1) head insertion: only head and its immediate neighbour are rewired.
         *
         * @param commuterName name of the priority commuter
         * @param destination  their intended taxi destination
         */
        void priorityInsertAtFront(String commuterName, String destination) {
            // Time: O(1) — rewire a constant number of pointers regardless of queue length.
            CommuterNode priorityCommuter = new CommuterNode(commuterName, destination);

            if (isQueueEmpty()) {
                // Edge case: inserting into an empty queue — node is both front and back.
                queueFront = priorityCommuter;
                queueBack  = priorityCommuter;
            } else {
                // New node becomes head; old head becomes its next.
                priorityCommuter.nextCommuter    = queueFront;   // new front points forward
                queueFront.prevCommuter          = priorityCommuter; // old front points back
                queueFront                       = priorityCommuter; // advance head pointer
            }

            commuterCount++;
            System.out.println("[Priority front] " + commuterName + " moved to front (destination: " + destination + ")");
        }

        /**
         * The front commuter boards their taxi and is removed from the queue.
         * Demonstrates O(1) head deletion: move head pointer one step forward.
         */
        void boardTaxiFromFront() {
            // Time: O(1) — move head pointer forward; no traversal needed.
            if (isQueueEmpty()) {
                // Edge case: boarding from an empty queue.
                System.out.println("[Board] Queue is empty — no commuter to board.");
                return;
            }

            CommuterNode boardingCommuter = queueFront;

            // Advance head pointer to next commuter in line.
            queueFront = queueFront.nextCommuter;

            if (queueFront != null) {
                // Sever backward link from new head to removed node.
                queueFront.prevCommuter = null;
            } else {
                // Last commuter boarded: queue is now empty; clear tail too.
                queueBack = null;
            }

            commuterCount--;
            System.out.println("[Boarded]      " + boardingCommuter.commuterName
                    + " boarded taxi to " + boardingCommuter.destination);
        }

        /**
         * Marshal removes a specific commuter by name (e.g., found alternative transport).
         * Demonstrates mid-chain deletion by bypassing the target node.
         *
         * @param commuterName exact name of the commuter to remove
         */
        void removeCommuterByName(String commuterName) {
            // Time: O(n) — may scan every node before finding a match or confirming absence.
            if (isQueueEmpty()) {
                System.out.println("[Remove] Queue is empty; cannot remove " + commuterName + ".");
                return;
            }

            // Traversal: walk the chain using next pointers to find the target.
            CommuterNode cursor = queueFront;
            while (cursor != null && !cursor.commuterName.equalsIgnoreCase(commuterName)) {
                cursor = cursor.nextCommuter;
            }

            // Not-found edge case.
            if (cursor == null) {
                System.out.println("[Remove] " + commuterName + " not found in queue.");
                return;
            }

            // Rewire previous node's forward link to skip over the removed node.
            if (cursor.prevCommuter != null) {
                cursor.prevCommuter.nextCommuter = cursor.nextCommuter;
            } else {
                // Removed node was the head; advance head pointer.
                queueFront = cursor.nextCommuter;
            }

            // Rewire next node's backward link to skip over the removed node.
            if (cursor.nextCommuter != null) {
                cursor.nextCommuter.prevCommuter = cursor.prevCommuter;
            } else {
                // Removed node was the tail; retreat tail pointer.
                queueBack = cursor.prevCommuter;
            }

            commuterCount--;
            System.out.println("[Removed]      " + cursor.commuterName
                    + " (to " + cursor.destination + ") left the queue.");
        }

        /**
         * Marshal traverses the queue from front to back to print a status list.
         * Uses only next pointers — demonstrating singly linked traversal behaviour.
         */
        void printQueueForward() {
            // Time: O(n) — visits every commuter node exactly once.
            System.out.println("\n--- Current queue (front → back) [" + commuterCount + " waiting] ---");

            if (isQueueEmpty()) {
                System.out.println("  Queue is empty.");
                return;
            }

            int position = 1;
            CommuterNode cursor = queueFront; // start at head
            while (cursor != null) {
                System.out.println("  " + position + ". " + cursor.commuterName
                        + " → " + cursor.destination);
                cursor = cursor.nextCommuter;   // follow next pointer
                position++;
            }
        }

        /**
         * Supervisor reviews the queue in reverse — back to front — for late-arrival auditing.
         * Uses prevCommuter pointers to demonstrate doubly linked reverse traversal.
         */
        void printQueueBackward() {
            // Time: O(n) — visits every commuter node exactly once via prev pointers.
            System.out.println("\n--- Reverse audit (back → front) ---");

            if (isQueueEmpty()) {
                System.out.println("  Queue is empty.");
                return;
            }

            CommuterNode cursor = queueBack; // start at tail
            while (cursor != null) {
                System.out.println("  ← " + cursor.commuterName + " (to " + cursor.destination + ")");
                cursor = cursor.prevCommuter;   // follow prev pointer
            }
        }

        /**
         * Returns how many commuters are currently waiting.
         *
         * @return current queue length
         */
        int getCommuterCount() {
            // Time: O(1) — returns stored counter directly.
            return commuterCount;
        }
    }

    /**
     * Story-driven entry point: simulates a busy Noord Street morning rush with
     * arrivals, priority inserts, boardings, removals, and edge-case handling.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall — traversals inside the demo dominate.
        NoorStreetQueue noorStreetQueue = new NoorStreetQueue();

        System.out.println("=== Noord Street Taxi Rank — Morning Rush Simulation ===");

        // Edge case: operations on a completely empty queue.
        System.out.println("\n-- Edge case: interacting with empty queue --");
        noorStreetQueue.boardTaxiFromFront();
        noorStreetQueue.removeCommuterByName("Thabo");
        noorStreetQueue.printQueueForward();

        // Commuters arrive and join at the back normally.
        System.out.println("\n-- Morning arrivals joining at back --");
        noorStreetQueue.joinAtBack("Sipho Dlamini",   "Soweto");
        noorStreetQueue.joinAtBack("Zanele Mokoena",  "Sandton");
        noorStreetQueue.joinAtBack("Lerato Khumalo",  "Alexandra");
        noorStreetQueue.joinAtBack("Bongani Nkosi",   "Midrand");

        noorStreetQueue.printQueueForward();

        // Marshal inserts a single-element priority commuter at front (O(1)).
        System.out.println("\n-- Priority boarding: elderly commuter --");
        noorStreetQueue.priorityInsertAtFront("Mama Dube", "Soweto");

        noorStreetQueue.printQueueForward();

        // Front commuters board taxis one by one.
        System.out.println("\n-- Taxis arrive; commuters board from front --");
        noorStreetQueue.boardTaxiFromFront();
        noorStreetQueue.boardTaxiFromFront();

        noorStreetQueue.printQueueForward();

        // Marshal removes a commuter from the middle (found a lift).
        System.out.println("\n-- Lerato found a lift; marshal removes her --");
        noorStreetQueue.removeCommuterByName("Lerato Khumalo");

        // Not-found edge case.
        System.out.println("\n-- Edge case: removing commuter not in queue --");
        noorStreetQueue.removeCommuterByName("Thabo Nkosi");

        noorStreetQueue.printQueueForward();

        // Reverse audit using doubly linked prev pointers.
        noorStreetQueue.printQueueBackward();

        // Board remaining commuters down to empty queue.
        System.out.println("\n-- End of rush: remaining commuters board --");
        noorStreetQueue.boardTaxiFromFront();
        noorStreetQueue.boardTaxiFromFront();
        noorStreetQueue.boardTaxiFromFront(); // edge case: attempt on empty

        System.out.println("\nFinal commuter count: " + noorStreetQueue.getCommuterCount());
        noorStreetQueue.printQueueForward();
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Node:          Each commuter is a node that stores data plus pointers connecting it into the chain.
 - head:          queueFront is the head — the first commuter to board and the start of traversal.
 - next pointer:  nextCommuter links each node to the one behind it in the queue (forward direction).
 - singly linked: Forward traversal via nextCommuter only — enough for a basic status printout.
 - doubly linked: Adding prevCommuter supports O(1) mid-chain deletion and reverse traversal.
 - traversal:     Walking the chain node by node with a cursor to inspect or print every commuter.
 - insertion:     Rewiring pointers to place a new commuter at the front or back without shifting.
 - deletion:      Rewiring the surrounding nodes' pointers to exclude the removed commuter.

 Big-O for every operation implemented:
 - isQueueEmpty:          O(1)  — one pointer check; independent of queue length.
 - joinAtBack:            O(1)  — direct tail access; constant pointer updates.
 - priorityInsertAtFront: O(1)  — direct head access; constant pointer updates.
 - boardTaxiFromFront:    O(1)  — move head pointer one step; no scanning needed.
 - removeCommuterByName:  O(n)  — name search may reach the last node in worst case.
 - printQueueForward:     O(n)  — every node is visited once via next pointers.
 - printQueueBackward:    O(n)  — every node is visited once via prev pointers.
 - getCommuterCount:      O(1)  — returns a stored integer; no traversal needed.

 Interview questions this code prepares you for:
 - Why is insert-at-head O(1) while insert-in-middle is O(n)?
 - How do you safely delete the head node of a singly linked list?
 - What extra benefit does a doubly linked list give over singly linked?
 - How do you handle deletion when the target node is at the tail?
 - How does a linked list handle dynamic size differently from an array?

 Most common mistake and how this code avoids it:
 - Mistake: In deletion, updating only one direction of links, leaving a dangling pointer
   that corrupts traversal or causes NullPointerExceptions.
 - Avoided: Every deletion explicitly updates both the previous node's next pointer AND
   the next node's prev pointer, and handles head/tail boundary cases separately.

 When to use this vs the common alternative:
 - Use a linked list when: elements are frequently inserted or removed at arbitrary positions,
   the size is unpredictable, and you do not need fast index-based random access.
 - Use an ArrayList/array when: you need O(1) index access, the size is relatively stable,
   and cache locality matters for performance.
*/