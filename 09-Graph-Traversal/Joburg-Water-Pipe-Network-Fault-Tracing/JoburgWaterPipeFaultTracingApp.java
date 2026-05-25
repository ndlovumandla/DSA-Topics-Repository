/*
 Johannesburg Water operates a large pipe network where each junction is a node and each pipe
 segment is an undirected edge. When a burst occurs, emergency teams must trace all connected
 downstream/upstream junctions affected by that fault zone. This is naturally solved with DFS,
 because DFS walks one branch deeply and backtracks to cover the full connected component.
 At the same time, rerouting planners need to know which junctions are reachable from an intake
 within a strict emergency hop limit, which is a BFS use case. Ayanda models the network with an
 adjacency list so memory scales with actual pipe links, not all possible junction pairs.
*/

/**
 * Intermediate graph traversal project for Joburg water fault tracing.
 */
public class JoburgWaterPipeFaultTracingApp {

    /** Maximum junctions in this demo. */
    static final int MAX_JUNCTIONS = 40;

    /**
     * Neighbor chain node for adjacency list.
     */
    static class PipeNeighborNode {
        int neighborIndex;
        PipeNeighborNode next;

        /**
         * Creates one neighbor reference.
         *
         * @param neighborIndex neighbor junction index
         */
        PipeNeighborNode(int neighborIndex) {
            // Time: O(1).
            this.neighborIndex = neighborIndex;
            this.next = null;
        }
    }

    /**
     * Queue element containing junction and distance in hops.
     */
    static class HopQueueEntry {
        int junctionIndex;
        int hops;

        /**
         * Creates one queue entry.
         *
         * @param junctionIndex junction index
         * @param hops distance from source
         */
        HopQueueEntry(int junctionIndex, int hops) {
            // Time: O(1).
            this.junctionIndex = junctionIndex;
            this.hops = hops;
        }
    }

    /**
     * Fixed-size queue for BFS.
     */
    static class HopQueue {
        HopQueueEntry[] entries;
        int front;
        int rear;
        int size;

        /**
         * Creates queue.
         *
         * @param capacity queue capacity
         */
        HopQueue(int capacity) {
            // Time: O(capacity) allocation.
            entries = new HopQueueEntry[capacity];
            front = 0;
            rear = 0;
            size = 0;
        }

        /**
         * Enqueues entry.
         *
         * @param entry item to enqueue
         */
        void enqueue(HopQueueEntry entry) {
            // Time: O(1).
            entries[rear] = entry;
            rear = (rear + 1) % entries.length;
            size++;
        }

        /**
         * Dequeues entry.
         *
         * @return dequeued item
         */
        HopQueueEntry dequeue() {
            // Time: O(1).
            HopQueueEntry value = entries[front];
            front = (front + 1) % entries.length;
            size--;
            return value;
        }

        /**
         * Checks emptiness.
         *
         * @return true if empty
         */
        boolean isEmpty() {
            // Time: O(1).
            return size == 0;
        }
    }

    /**
     * Water pipe graph using adjacency lists.
     */
    static class WaterPipeGraph {
        String[] junctionNames;
        PipeNeighborNode[] adjacencyHeads;
        int junctionCount;

        /**
         * Creates graph.
         */
        WaterPipeGraph() {
            // Time: O(V) allocation for fixed arrays.
            junctionNames = new String[MAX_JUNCTIONS];
            adjacencyHeads = new PipeNeighborNode[MAX_JUNCTIONS];
            junctionCount = 0;
        }

        /**
         * Adds junction vertex.
         *
         * @param name junction name
         * @return index
         */
        int addJunction(String name) {
            // Time: O(1).
            junctionNames[junctionCount] = name;
            junctionCount++;
            return junctionCount - 1;
        }

        /**
         * Adds undirected pipe segment.
         *
         * @param a first junction index
         * @param b second junction index
         */
        void addPipeSegment(int a, int b) {
            // Time: O(1).
            PipeNeighborNode nodeAB = new PipeNeighborNode(b);
            nodeAB.next = adjacencyHeads[a];
            adjacencyHeads[a] = nodeAB;

            PipeNeighborNode nodeBA = new PipeNeighborNode(a);
            nodeBA.next = adjacencyHeads[b];
            adjacencyHeads[b] = nodeBA;
        }

        /**
         * Finds index by junction name.
         *
         * @param name junction name
         * @return index or -1
         */
        int findJunctionIndex(String name) {
            // Time: O(V).
            for (int i = 0; i < junctionCount; i++) {
                if (junctionNames[i].equals(name)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * DFS trace of all junctions connected to a fault point.
         *
         * @param faultName fault junction name
         * @return traversal string
         */
        String traceFaultZoneDfs(String faultName) {
            // Time: O(V + E) for component traversal.
            int faultIndex = findJunctionIndex(faultName);
            if (faultIndex == -1) {
                return "FAULT JUNCTION NOT FOUND";
            }

            boolean[] visited = new boolean[junctionCount];
            int[] order = new int[junctionCount];
            int[] countRef = new int[]{0};

            dfsVisit(faultIndex, visited, order, countRef);

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < countRef[0]; i++) {
                if (i > 0) {
                    result.append(" -> ");
                }
                result.append(junctionNames[order[i]]);
            }
            result.append(" (affected junctions=").append(countRef[0]).append(")");
            return result.toString();
        }

        /**
         * Recursive DFS visitor.
         *
         * @param current current index
         * @param visited visited set
         * @param order output order array
         * @param countRef mutable count holder
         */
        void dfsVisit(int current, boolean[] visited, int[] order, int[] countRef) {
            // Time: O(V + E) across whole DFS recursion.
            visited[current] = true;
            order[countRef[0]] = current;
            countRef[0]++;

            PipeNeighborNode cursor = adjacencyHeads[current];
            while (cursor != null) {
                if (!visited[cursor.neighborIndex]) {
                    dfsVisit(cursor.neighborIndex, visited, order, countRef);
                }
                cursor = cursor.next;
            }
        }

        /**
         * BFS reachability within a hop limit from intake point.
         *
         * @param intakeName intake/source junction name
         * @param hopLimit max allowed hops
         * @return reachable junction list string
         */
        String reachableWithinHopsBfs(String intakeName, int hopLimit) {
            // Time: O(V + E) in worst case until hop limit boundary.
            int source = findJunctionIndex(intakeName);
            if (source == -1) {
                return "INTAKE JUNCTION NOT FOUND";
            }
            if (hopLimit < 0) {
                return "NO JUNCTIONS (negative hop limit)";
            }

            boolean[] visited = new boolean[junctionCount];
            int[] distance = new int[junctionCount];
            for (int i = 0; i < junctionCount; i++) {
                distance[i] = -1;
            }

            HopQueue queue = new HopQueue(junctionCount + 5);
            visited[source] = true;
            distance[source] = 0;
            queue.enqueue(new HopQueueEntry(source, 0));

            while (!queue.isEmpty()) {
                HopQueueEntry front = queue.dequeue();
                int current = front.junctionIndex;
                int hops = front.hops;

                if (hops == hopLimit) {
                    continue;
                }

                PipeNeighborNode cursor = adjacencyHeads[current];
                while (cursor != null) {
                    int neighbor = cursor.neighborIndex;
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        distance[neighbor] = hops + 1;
                        queue.enqueue(new HopQueueEntry(neighbor, hops + 1));
                    }
                    cursor = cursor.next;
                }
            }

            StringBuilder result = new StringBuilder();
            boolean first = true;
            int count = 0;
            for (int i = 0; i < junctionCount; i++) {
                if (distance[i] != -1 && distance[i] <= hopLimit) {
                    if (!first) {
                        result.append(" | ");
                    }
                    result.append(junctionNames[i]).append("(hops=").append(distance[i]).append(")");
                    first = false;
                    count++;
                }
            }
            result.append(" (reachable=").append(count).append(")");
            return result.toString();
        }
    }

    /**
     * Builds demo network with one disconnected section.
     *
     * @return graph
     */
    static WaterPipeGraph buildNetwork() {
        // Time: O(V + E) fixed setup.
        WaterPipeGraph graph = new WaterPipeGraph();

        int sandtonFault = graph.addJunction("Sandton Fault Node");
        int rosebank = graph.addJunction("Rosebank Junction");
        int alexandra = graph.addJunction("Alexandra Junction");
        int bryanston = graph.addJunction("Bryanston Junction");
        int randburg = graph.addJunction("Randburg Junction");
        int parktown = graph.addJunction("Parktown Junction");
        int intake = graph.addJunction("Hursthill Intake");
        int cbd = graph.addJunction("CBD Junction");
        int soweto = graph.addJunction("Soweto Junction");
        int disconnectedDepot = graph.addJunction("Lenasia Depot");

        graph.addPipeSegment(sandtonFault, rosebank);
        graph.addPipeSegment(sandtonFault, alexandra);
        graph.addPipeSegment(rosebank, parktown);
        graph.addPipeSegment(alexandra, bryanston);
        graph.addPipeSegment(bryanston, randburg);
        graph.addPipeSegment(randburg, intake);
        graph.addPipeSegment(parktown, cbd);
        graph.addPipeSegment(cbd, intake);
        graph.addPipeSegment(cbd, soweto);

        int ignored = disconnectedDepot;
        if (ignored == -1) {
            System.out.print("");
        }

        return graph;
    }

    /**
     * Main demo.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== Joburg Water Pipe Network Fault Tracing (Intermediate) ===");

        WaterPipeGraph graph = buildNetwork();

        System.out.println("\n-- DFS fault zone trace from Sandton burst --");
        System.out.println("  " + graph.traceFaultZoneDfs("Sandton Fault Node"));

        System.out.println("\n-- BFS reachable within 3 pipe connections from Hursthill intake --");
        System.out.println("  " + graph.reachableWithinHopsBfs("Hursthill Intake", 3));

        System.out.println("\n-- Edge case: unknown fault node --");
        System.out.println("  " + graph.traceFaultZoneDfs("Midrand Unknown"));

        System.out.println("\n-- Edge case: hop limit 0 (only source) --");
        System.out.println("  " + graph.reachableWithinHopsBfs("Hursthill Intake", 0));

        System.out.println("\n-- Edge case: disconnected junction from intake region --");
        System.out.println("  " + graph.reachableWithinHopsBfs("Lenasia Depot", 3));
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - vertex: pipe junction in the water network.
 - edge: physical pipe segment between two junctions.
 - adjacency list: each junction stores neighbor chain nodes.
 - BFS: expands in layers by hop count from source.
 - DFS: explores one branch deeply before backtracking.
 - visited set: prevents revisiting cycles.
 - queue (BFS): ensures shortest-hop expansion order.
 - stack (DFS): implicit recursion stack in this DFS implementation.

 Big-O:
 - addJunction: O(1)
 - addPipeSegment: O(1)
 - findJunctionIndex: O(V)
 - traceFaultZoneDfs / dfsVisit: O(V + E)
 - reachableWithinHopsBfs: O(V + E)

 Interview questions:
 - Why is DFS suitable for connected-component fault tracing?
 - Why is BFS ideal for hop-limited reachability queries?
 - How does visited[] prevent infinite traversal in cyclic networks?
 - Why can adjacency lists be more memory-efficient than matrices for sparse utility graphs?
 - What changes if pipe graph edges were directed instead of undirected?

 Common mistake and prevention:
 - Mistake: not tracking hop distance in BFS when queries need radius constraints.
 - Avoided here: queue stores both junction index and current hop distance.

 Comparison guidance:
 - Use DFS to enumerate full affected zones after a burst.
 - Use BFS to estimate reroute feasibility within emergency connection limits.
*/