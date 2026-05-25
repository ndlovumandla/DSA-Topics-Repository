/*
 Cape Town's MyCiTi buses arrive every few minutes, and passengers at busy stops such as Civic Centre
 must board in the exact order they arrived. When a bus reaches capacity, the remaining passengers must
 stay in line for the next service without the whole line being shifted around in memory. Developer
 Amara models the stop as a queue because the boarding rule is FIFO: first passenger in is the first
 passenger out. A circular queue is the right implementation because it reuses a fixed-size buffer
 efficiently, which matters when the stop may hold around 200 people during peak hour. The same queue
 idea also powers breadth-first search, so the program includes a small route explorer to show how
 queue logic can visit stops level by level in a network.
*/

/**
 * Simulates a Cape Town MyCiTi stop using a custom circular queue built from scratch.
 * Passengers enqueue at the back, board from the front, and the remaining queue is reused efficiently.
 */
public class CapeTownMyCiTiApp {

    /**
     * Represents one passenger waiting at the stop.
     * Each record stores the commuter's identity and destination for the printed bus manifest.
     */
    static class PassengerRecord {
        String passengerName;
        String destinationRoute;

        /**
         * Creates a passenger record for the queue buffer.
         *
         * @param passengerName commuter name
         * @param destinationRoute route or destination the commuter is headed to
         */
        PassengerRecord(String passengerName, String destinationRoute) {
            // Time: O(1) — just assign two fields.
            this.passengerName = passengerName;
            this.destinationRoute = destinationRoute;
        }
    }

    /**
     * Circular queue for passengers waiting at a bus stop.
     * It stores fixed-size memory and wraps around instead of shifting items after dequeues.
     */
    static class PassengerCircularQueue {
        private final PassengerRecord[] busStopBuffer;
        private int frontSeatIndex;
        private int rearSeatIndex;
        private int passengerCount;

        /**
         * Creates a stop queue with fixed capacity for the peak-hour crowd.
         *
         * @param capacity maximum number of passengers the stop can hold
         */
        PassengerCircularQueue(int capacity) {
            // Time: O(1) — one array allocation and a few assignments.
            this.busStopBuffer = new PassengerRecord[capacity];
            this.frontSeatIndex = 0;
            this.rearSeatIndex = -1;
            this.passengerCount = 0;
        }

        /**
         * Checks whether the stop has no passengers waiting.
         *
         * @return true when the queue is empty
         */
        boolean isEmpty() {
            // Time: O(1) — one counter comparison.
            return passengerCount == 0;
        }

        /**
         * Checks whether the stop queue has reached capacity.
         *
         * @return true when every slot in the circular buffer is occupied
         */
        boolean isFull() {
            // Time: O(1) — one counter comparison.
            return passengerCount == busStopBuffer.length;
        }

        /**
         * Adds a passenger to the back of the queue.
         * FIFO means every new arrival waits behind people already in line.
         *
         * @param passengerName passenger joining the queue
         * @param destinationRoute bus route the passenger wants to board
         */
        void enqueuePassenger(String passengerName, String destinationRoute) {
            // Time: O(1) — circular arithmetic and one assignment.
            if (isFull()) {
                System.out.println("[ENQUEUE FAILED] Stop is full — " + passengerName + " must wait for the next service.");
                return;
            }

            // Circular queue concept: move the rear forward with wrap-around arithmetic.
            rearSeatIndex = (rearSeatIndex + 1) % busStopBuffer.length;
            busStopBuffer[rearSeatIndex] = new PassengerRecord(passengerName, destinationRoute);
            passengerCount++;

            System.out.println("[JOINED QUEUE] " + passengerName + " waiting for " + destinationRoute);
        }

        /**
         * Removes the passenger at the front so they can board the bus.
         * This is the FIFO rule: the person who waited longest is served first.
         *
         * @return removed passenger record, or null if the queue is empty
         */
        PassengerRecord dequeuePassengerForBus() {
            // Time: O(1) — one read, one pointer move, and a counter update.
            if (isEmpty()) {
                System.out.println("[DEQUEUE FAILED] No passengers waiting at the stop.");
                return null;
            }

            PassengerRecord boardingPassenger = busStopBuffer[frontSeatIndex];
            busStopBuffer[frontSeatIndex] = null; // clear slot for memory reuse

            // Circular queue concept: advance front and wrap naturally through the buffer.
            frontSeatIndex = (frontSeatIndex + 1) % busStopBuffer.length;
            passengerCount--;

            if (passengerCount == 0) {
                // Reset to canonical empty-state pointers.
                frontSeatIndex = 0;
                rearSeatIndex = -1;
            }

            System.out.println("[BOARDED] " + boardingPassenger.passengerName
                    + " boarded a bus to " + boardingPassenger.destinationRoute);
            return boardingPassenger;
        }

        /**
         * Shows the next passenger without removing them from the queue.
         *
         * @return the front passenger, or null if empty
         */
        PassengerRecord peekFrontPassenger() {
            // Time: O(1) — direct access to the front slot.
            if (isEmpty()) {
                return null;
            }
            return busStopBuffer[frontSeatIndex];
        }

        /**
         * Prints the current queue in arrival order from front to back.
         *
         * @param stopName human-readable stop name for the printed dashboard
         */
        void printQueueState(String stopName) {
            // Time: O(n) — each waiting passenger is printed once.
            System.out.println("\n--- MyCiTi queue at " + stopName + " [" + passengerCount + " waiting] ---");

            if (isEmpty()) {
                System.out.println("  Queue is empty.");
                return;
            }

            for (int offset = 0; offset < passengerCount; offset++) {
                int bufferIndex = (frontSeatIndex + offset) % busStopBuffer.length;
                PassengerRecord waitingPassenger = busStopBuffer[bufferIndex];
                System.out.println("  " + (offset + 1) + ". " + waitingPassenger.passengerName
                        + " -> " + waitingPassenger.destinationRoute);
            }
        }

        /**
         * Returns the number of passengers currently in the queue.
         *
         * @return queue size
         */
        int getPassengerCount() {
            // Time: O(1) — returns a stored counter.
            return passengerCount;
        }

        /**
         * Returns the maximum capacity of the stop.
         *
         * @return queue capacity
         */
        int getCapacity() {
            // Time: O(1) — fixed array length access.
            return busStopBuffer.length;
        }
    }

    /**
     * Small queue used to demonstrate breadth-first search over a route network.
     * BFS uses the same FIFO idea as a passenger queue.
     */
    static class RouteExplorerCircularQueue {
        private final int[] stopIndexBuffer;
        private int frontIndex;
        private int rearIndex;
        private int itemCount;

        /**
         * Creates a fixed-size integer queue for BFS stop exploration.
         *
         * @param capacity number of stops that may be queued
         */
        RouteExplorerCircularQueue(int capacity) {
            // Time: O(1) — one array allocation and pointer setup.
            this.stopIndexBuffer = new int[capacity];
            this.frontIndex = 0;
            this.rearIndex = -1;
            this.itemCount = 0;
        }

        /**
         * Checks whether the BFS queue is empty.
         *
         * @return true if no stops are waiting to be explored
         */
        boolean isEmpty() {
            // Time: O(1) — counter check only.
            return itemCount == 0;
        }

        /**
         * Enqueues a stop index for BFS exploration.
         *
         * @param stopIndex the graph vertex to visit later
         */
        void enqueue(int stopIndex) {
            // Time: O(1) — wrap-around insertion.
            rearIndex = (rearIndex + 1) % stopIndexBuffer.length;
            stopIndexBuffer[rearIndex] = stopIndex;
            itemCount++;
        }

        /**
         * Dequeues the next stop index in BFS order.
         *
         * @return front stop index
         */
        int dequeue() {
            // Time: O(1) — move the front pointer once.
            int stopIndex = stopIndexBuffer[frontIndex];
            frontIndex = (frontIndex + 1) % stopIndexBuffer.length;
            itemCount--;
            return stopIndex;
        }
    }

    /**
     * Demonstrates breadth-first search over a small MyCiTi stop network.
     * The queue visits stops level by level, which is exactly what BFS means.
     */
    static void demonstrateBreadthFirstSearch() {
        // Time: O(V + E) conceptually for the graph traversal; here the graph is fixed and small.
        String[] myCitiStops = {
            "Civic Centre", "Foreshore", "Woodstock", "Salt River", "V&A Waterfront", "Table View"
        };

        // Adjacency matrix for a tiny route network; 1 means direct service connection.
        int[][] routeLinks = {
            {0, 1, 1, 0, 0, 0},
            {1, 0, 0, 1, 1, 0},
            {1, 0, 0, 1, 0, 0},
            {0, 1, 1, 0, 0, 1},
            {0, 1, 0, 0, 0, 1},
            {0, 0, 0, 1, 1, 0}
        };

        boolean[] visitedStops = new boolean[myCitiStops.length];
        RouteExplorerCircularQueue bfsQueue = new RouteExplorerCircularQueue(myCitiStops.length);

        System.out.println("\n--- BFS route explorer from Civic Centre ---");
        bfsQueue.enqueue(0);
        visitedStops[0] = true;

        while (!bfsQueue.isEmpty()) {
            int currentStopIndex = bfsQueue.dequeue();
            System.out.println("  Visit stop: " + myCitiStops[currentStopIndex]);

            for (int neighborIndex = 0; neighborIndex < myCitiStops.length; neighborIndex++) {
                // BFS concept: discover unseen neighboring stops and queue them for later.
                if (routeLinks[currentStopIndex][neighborIndex] == 1 && !visitedStops[neighborIndex]) {
                    visitedStops[neighborIndex] = true;
                    bfsQueue.enqueue(neighborIndex);
                }
            }
        }
    }

    /**
     * Runs a story-driven demonstration of the queue system at a busy Cape Town stop.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall — printing and BFS traversal dominate the demo.
        PassengerCircularQueue civicCentreQueue = new PassengerCircularQueue(6);

        System.out.println("=== Cape Town MyCiTi Bus System ===");

        // Edge case: empty queue.
        civicCentreQueue.printQueueState("Civic Centre");
        civicCentreQueue.dequeuePassengerForBus();

        // Normal arrivals.
        System.out.println("\n-- Morning arrivals --");
        civicCentreQueue.enqueuePassenger("Ayesha", "Table View");
        civicCentreQueue.enqueuePassenger("Sibusiso", "Woodstock");
        civicCentreQueue.enqueuePassenger("Naledi", "V&A Waterfront");

        // Duplicate values are allowed and preserve FIFO arrival order.
        civicCentreQueue.enqueuePassenger("Ayesha", "Table View");

        civicCentreQueue.printQueueState("Civic Centre");

        // Single-element edge case after repeated boarding.
        System.out.println("\n-- Bus arrives and boards in FIFO order --");
        civicCentreQueue.dequeuePassengerForBus();
        civicCentreQueue.dequeuePassengerForBus();

        PassengerRecord nextPassenger = civicCentreQueue.peekFrontPassenger();
        if (nextPassenger != null) {
            System.out.println("[PEEK] Next passenger to board: " + nextPassenger.passengerName
                    + " -> " + nextPassenger.destinationRoute);
        }

        civicCentreQueue.printQueueState("Civic Centre");

        // Finish the stop and demonstrate wrap-around reuse.
        civicCentreQueue.enqueuePassenger("Farah", "Salt River");
        civicCentreQueue.enqueuePassenger("Thando", "Foreshore");
        civicCentreQueue.printQueueState("Civic Centre");

        System.out.println("\n-- Remaining passengers board --");
        while (!civicCentreQueue.isEmpty()) {
            civicCentreQueue.dequeuePassengerForBus();
        }

        // Edge case: empty after final boarding.
        civicCentreQueue.dequeuePassengerForBus();
        System.out.println("Final queue size: " + civicCentreQueue.getPassengerCount());

        // BFS concept demonstration.
        demonstrateBreadthFirstSearch();
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - FIFO: First In, First Out — the first passenger to arrive is the first one boarded.
 - enqueue: Add a passenger to the back of the queue.
 - dequeue: Remove a passenger from the front of the queue.
 - circular queue: A fixed array that wraps around to reuse freed slots.
 - BFS: Breadth-first search — visit one layer of a graph before moving deeper.
 - traversal: Walk the queue from front to back, or visit graph stops level by level.
 - head/front: The front index is where boarding happens.
 - tail/rear: The rear index is where new arrivals are inserted.

 Big-O for operations implemented:
 - isEmpty: O(1) because it checks a counter.
 - isFull: O(1) because it checks a counter.
 - enqueuePassenger: O(1) because it computes a slot with modular arithmetic and writes once.
 - dequeuePassengerForBus: O(1) because it reads one item and advances the front index.
 - peekFrontPassenger: O(1) because it reads the front slot only.
 - printQueueState: O(n) because it prints each waiting passenger once.
 - getPassengerCount: O(1) because it returns a stored value.
 - BFS demo: O(V + E) in graph terms because every stop and every link is examined once.
 Space complexity: O(n) because the circular queue stores one record per waiting passenger.

 Interview questions this code prepares you for:
 - Why does a queue naturally model bus boarding or customer service lines?
 - How does a circular queue avoid shifting elements after each dequeue?
 - What is the difference between FIFO and LIFO?
 - How does BFS use a queue to traverse a graph level by level?
 - What happens when a circular queue becomes full?

 Most common mistake and how this code avoids it:
 - Mistake: Forgetting wrap-around arithmetic or confusing full and empty states.
 - Avoided: The implementation tracks passengerCount explicitly and uses modular indexing,
   which makes full/empty checks unambiguous.

 When to use this vs the common alternative:
 - Use a queue when you need arrival order preserved: buses, service desks, BFS, print spooling.
 - Use a stack when the newest item must be processed first: undo, recursion, expression parsing.
 - Use a circular queue when memory is fixed and you want O(1) enqueue/dequeue without shifting.
*/
