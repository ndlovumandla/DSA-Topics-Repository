/*
 Cape Town's Metrorail system connects stations across multiple lines, and passengers often need
 interchanges to reach final destinations. A passenger travelling from Bellville to Simon's Town
 needs the planner to answer two questions: does a path exist, and which stations will be visited.
 This is a graph problem where stations are vertices and rail links are undirected edges. Ravi
 models the rail map using an adjacency list because each station only connects to a few neighbors,
 making sparse storage efficient. He uses BFS to find the shortest route by number of station stops,
 and DFS to explore one branch deeply before backtracking. The two traversals solve different needs:
 BFS for shortest-stop planning, DFS for full-line exploration and reachability checks.
*/

/**
 * Beginner graph traversal demo for Cape Town Metrorail journey planning.
 */
public class MetrorailCapeTownNetworkApp {

    /** Maximum number of stations in this educational demo. */
    static final int MAX_STATIONS = 30;

    /**
     * Neighbor node for adjacency-list chains.
     */
    static class StationNeighborNode {
        int neighborStationIndex;
        StationNeighborNode next;

        /**
         * Creates a neighbor node.
         *
         * @param neighborStationIndex linked neighbor station index
         */
        StationNeighborNode(int neighborStationIndex) {
            // Time: O(1) - constant initialization.
            this.neighborStationIndex = neighborStationIndex;
            this.next = null;
        }
    }

    /**
     * Fixed-size queue used by BFS.
     */
    static class StationQueue {
        int[] data;
        int front;
        int rear;
        int size;

        /**
         * Creates queue with fixed capacity.
         *
         * @param capacity queue capacity
         */
        StationQueue(int capacity) {
            // Time: O(capacity) for array allocation.
            data = new int[capacity];
            front = 0;
            rear = 0;
            size = 0;
        }

        /**
         * Enqueues one station index.
         *
         * @param value station index
         */
        void enqueue(int value) {
            // Time: O(1) with circular indexing.
            data[rear] = value;
            rear = (rear + 1) % data.length;
            size++;
        }

        /**
         * Dequeues one station index.
         *
         * @return dequeued station index
         */
        int dequeue() {
            // Time: O(1).
            int value = data[front];
            front = (front + 1) % data.length;
            size--;
            return value;
        }

        /**
         * Checks if queue is empty.
         *
         * @return true when empty
         */
        boolean isEmpty() {
            // Time: O(1).
            return size == 0;
        }
    }

    /**
     * Fixed-size stack used by iterative DFS.
     */
    static class StationStack {
        int[] data;
        int top;

        /**
         * Creates stack.
         *
         * @param capacity stack capacity
         */
        StationStack(int capacity) {
            // Time: O(capacity) allocation.
            data = new int[capacity];
            top = -1;
        }

        /**
         * Pushes one index.
         *
         * @param value station index
         */
        void push(int value) {
            // Time: O(1).
            top++;
            data[top] = value;
        }

        /**
         * Pops one index.
         *
         * @return popped station index
         */
        int pop() {
            // Time: O(1).
            int value = data[top];
            top--;
            return value;
        }

        /**
         * Tests if stack is empty.
         *
         * @return true if empty
         */
        boolean isEmpty() {
            // Time: O(1).
            return top == -1;
        }
    }

    /**
     * Metrorail graph using adjacency-list internals.
     */
    static class MetrorailGraph {
        String[] stationNames;
        StationNeighborNode[] adjacencyHeads;
        int stationCount;

        /**
         * Creates graph.
         */
        MetrorailGraph() {
            // Time: O(V) for array allocation where V=MAX_STATIONS.
            stationNames = new String[MAX_STATIONS];
            adjacencyHeads = new StationNeighborNode[MAX_STATIONS];
            stationCount = 0;
        }

        /**
         * Adds a station vertex.
         *
         * @param stationName station name
         * @return index of inserted station
         */
        int addStation(String stationName) {
            // Time: O(1) append by index.
            stationNames[stationCount] = stationName;
            stationCount++;
            return stationCount - 1;
        }

        /**
         * Adds undirected rail edge between two stations.
         *
         * @param stationA first station index
         * @param stationB second station index
         */
        void addUndirectedConnection(int stationA, int stationB) {
            // Time: O(1) inserts at adjacency-list heads.
            StationNeighborNode nodeAB = new StationNeighborNode(stationB);
            nodeAB.next = adjacencyHeads[stationA];
            adjacencyHeads[stationA] = nodeAB;

            StationNeighborNode nodeBA = new StationNeighborNode(stationA);
            nodeBA.next = adjacencyHeads[stationB];
            adjacencyHeads[stationB] = nodeBA;
        }

        /**
         * Finds station index by name.
         *
         * @param stationName station name
         * @return station index or -1 if missing
         */
        int findStationIndex(String stationName) {
            // Time: O(V) linear scan over station array.
            for (int i = 0; i < stationCount; i++) {
                if (stationNames[i].equals(stationName)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * BFS shortest path by station stops.
         *
         * @param fromName origin station name
         * @param toName destination station name
         * @return path string or "NO PATH"
         */
        String bfsShortestStopPath(String fromName, String toName) {
            // Time: O(V + E) - each vertex/edge processed at most once in BFS.
            int from = findStationIndex(fromName);
            int to = findStationIndex(toName);

            if (from == -1 || to == -1) {
                return "NO PATH (unknown station)";
            }

            boolean[] visited = new boolean[stationCount];
            int[] predecessor = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                predecessor[i] = -1;
            }

            StationQueue queue = new StationQueue(stationCount + 2);
            visited[from] = true;
            queue.enqueue(from);

            while (!queue.isEmpty()) {
                int current = queue.dequeue();

                if (current == to) {
                    break;
                }

                StationNeighborNode cursor = adjacencyHeads[current];
                while (cursor != null) {
                    int neighbor = cursor.neighborStationIndex;
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        predecessor[neighbor] = current;
                        queue.enqueue(neighbor);
                    }
                    cursor = cursor.next;
                }
            }

            if (!visited[to]) {
                return "NO PATH";
            }

            // Reconstruct shortest path by walking predecessor chain backward.
            int[] reversed = new int[stationCount];
            int length = 0;
            int cursor = to;
            while (cursor != -1) {
                reversed[length] = cursor;
                length++;
                cursor = predecessor[cursor];
            }

            StringBuilder path = new StringBuilder();
            for (int i = length - 1; i >= 0; i--) {
                path.append(stationNames[reversed[i]]);
                if (i != 0) {
                    path.append(" -> ");
                }
            }
            path.append(" (stops=").append(length - 1).append(")");
            return path.toString();
        }

        /**
         * DFS traversal order from a start station.
         *
         * @param fromName start station
         * @return traversal order string
         */
        String dfsExplorationOrder(String fromName) {
            // Time: O(V + E) - each vertex/edge visited once in DFS.
            int from = findStationIndex(fromName);
            if (from == -1) {
                return "NO TRAVERSAL (unknown station)";
            }

            boolean[] visited = new boolean[stationCount];
            StationStack stack = new StationStack(stationCount + 2);
            stack.push(from);

            StringBuilder order = new StringBuilder();
            boolean first = true;

            while (!stack.isEmpty()) {
                int current = stack.pop();
                if (visited[current]) {
                    continue;
                }

                visited[current] = true;
                if (!first) {
                    order.append(" -> ");
                }
                order.append(stationNames[current]);
                first = false;

                // Push neighbors so DFS goes deep; order depends on adjacency insertion sequence.
                StationNeighborNode cursor = adjacencyHeads[current];
                while (cursor != null) {
                    if (!visited[cursor.neighborStationIndex]) {
                        stack.push(cursor.neighborStationIndex);
                    }
                    cursor = cursor.next;
                }
            }

            return order.toString();
        }
    }

    /**
     * Builds a compact demo section of the Cape Town rail network.
     *
     * @return graph instance
     */
    static MetrorailGraph buildDemoNetwork() {
        // Time: O(V + E) setup for fixed demo size.
        MetrorailGraph graph = new MetrorailGraph();

        int bellville = graph.addStation("Bellville");
        int parow = graph.addStation("Parow");
        int capeTown = graph.addStation("Cape Town");
        int wynberg = graph.addStation("Wynberg");
        int retreat = graph.addStation("Retreat");
        int simonsTown = graph.addStation("Simon's Town");
        int rondebosch = graph.addStation("Rondebosch");
        int pinelands = graph.addStation("Pinelands");
        int malmesburyShuttle = graph.addStation("Malmesbury Shuttle");

        graph.addUndirectedConnection(bellville, parow);
        graph.addUndirectedConnection(parow, capeTown);
        graph.addUndirectedConnection(capeTown, wynberg);
        graph.addUndirectedConnection(wynberg, retreat);
        graph.addUndirectedConnection(retreat, simonsTown);
        graph.addUndirectedConnection(capeTown, rondebosch);
        graph.addUndirectedConnection(rondebosch, wynberg);
        graph.addUndirectedConnection(parow, pinelands);
        graph.addUndirectedConnection(pinelands, capeTown);

        // Malmesbury shuttle remains disconnected to show not-found path case.
        // no edges for malmesburyShuttle
        int ignored = malmesburyShuttle;
        if (ignored == -1) {
            System.out.println();
        }

        return graph;
    }

    /**
     * Main story-driven demo.
     *
     * @param args unused CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== Metrorail Cape Town Network (Beginner Graph Traversal) ===");

        MetrorailGraph network = buildDemoNetwork();

        System.out.println("\n-- BFS shortest-stop route: Bellville to Simon's Town --");
        System.out.println("  " + network.bfsShortestStopPath("Bellville", "Simon's Town"));

        System.out.println("\n-- DFS exploration from Bellville --");
        System.out.println("  " + network.dfsExplorationOrder("Bellville"));

        System.out.println("\n-- Edge case: unknown station --");
        System.out.println("  " + network.bfsShortestStopPath("Atlantis", "Cape Town"));

        System.out.println("\n-- Edge case: disconnected station (no path) --");
        System.out.println("  " + network.bfsShortestStopPath("Bellville", "Malmesbury Shuttle"));

        System.out.println("\n-- Edge case: same source and destination --");
        System.out.println("  " + network.bfsShortestStopPath("Cape Town", "Cape Town"));
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - vertex: a station node in the rail network.
 - edge: a rail connection between two stations.
 - adjacency list: per-vertex linked list of neighbors.
 - BFS: level-order traversal using a queue.
 - DFS: depth-first traversal using a stack.
 - visited set: boolean array preventing repeated revisits.
 - queue (BFS): first-in-first-out structure for shortest-stop expansion.
 - stack (DFS): last-in-first-out structure for deep branch exploration.

 Big-O of operations:
 - addStation: O(1)
 - addUndirectedConnection: O(1)
 - findStationIndex: O(V)
 - bfsShortestStopPath: O(V + E)
 - dfsExplorationOrder: O(V + E)

 Interview questions:
 - Why does BFS find shortest path in unweighted graphs?
 - Why can DFS reach destination without guaranteeing shortest path?
 - Why is adjacency list usually better than adjacency matrix for sparse networks?
 - What breaks if you forget a visited set in cyclic graphs?
 - How do BFS queue semantics differ from DFS stack semantics?

 Common mistake and prevention:
 - Mistake: skipping predecessor tracking in BFS, making path reconstruction impossible.
 - Avoided here: predecessor[] stores parent pointers so shortest route can be rebuilt.

 Comparison guidance:
 - Use BFS for shortest-stop journey planner queries.
 - Use DFS for topology exploration, connected-component tracing, or deep-line audits.
*/