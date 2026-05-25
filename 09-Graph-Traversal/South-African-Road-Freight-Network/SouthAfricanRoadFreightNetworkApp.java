/*
 Imperial Logistics routes heavy trucks between major South African cities, where each city is a
 graph vertex and each road route is a directed weighted edge measured in kilometres. Dispatchers
 must answer operational questions quickly: which cities are reachable within a limited number of
 road hops, and whether a destination can still be reached if some cities are closed for works.
 Lwazi models the network as an adjacency list because road graphs are sparse in practice, so this
 representation uses less memory than a full adjacency matrix. She uses BFS for hop-limited
 reachability (driver-hours compliance), and DFS for route-existence checks that avoid closed cities.
 The implementation includes explicit visited-state management, custom queue/stack structures, and a
 comparison method against Java built-in graph structures for verification.
*/

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Advanced graph traversal demo for South African road freight routing.
 */
public class SouthAfricanRoadFreightNetworkApp {

    /** Max number of cities in this demo. */
    static final int MAX_CITIES = 30;

    /**
     * Weighted directed edge node in adjacency list.
     */
    static class RoadEdgeNode {
        int destinationCityIndex;
        int distanceKm;
        RoadEdgeNode next;

        /**
         * Creates one road edge.
         *
         * @param destinationCityIndex destination city index
         * @param distanceKm edge weight in kilometres
         */
        RoadEdgeNode(int destinationCityIndex, int distanceKm) {
            // Time: O(1).
            this.destinationCityIndex = destinationCityIndex;
            this.distanceKm = distanceKm;
            this.next = null;
        }
    }

    /**
     * Queue entry for BFS including hop count.
     */
    static class HopVisitEntry {
        int cityIndex;
        int hops;

        /**
         * Creates one BFS entry.
         *
         * @param cityIndex city index
         * @param hops hops from source
         */
        HopVisitEntry(int cityIndex, int hops) {
            // Time: O(1).
            this.cityIndex = cityIndex;
            this.hops = hops;
        }
    }

    /**
     * Fixed-size queue for BFS.
     */
    static class FreightQueue {
        HopVisitEntry[] entries;
        int front;
        int rear;
        int size;

        /**
         * Creates queue.
         *
         * @param capacity queue capacity
         */
        FreightQueue(int capacity) {
            // Time: O(capacity) allocation.
            entries = new HopVisitEntry[capacity];
            front = 0;
            rear = 0;
            size = 0;
        }

        /**
         * Enqueues one BFS entry.
         *
         * @param entry entry to enqueue
         */
        void enqueue(HopVisitEntry entry) {
            // Time: O(1).
            entries[rear] = entry;
            rear = (rear + 1) % entries.length;
            size++;
        }

        /**
         * Dequeues one BFS entry.
         *
         * @return dequeued entry
         */
        HopVisitEntry dequeue() {
            // Time: O(1).
            HopVisitEntry value = entries[front];
            front = (front + 1) % entries.length;
            size--;
            return value;
        }

        /**
         * Queue empty check.
         *
         * @return true when empty
         */
        boolean isEmpty() {
            // Time: O(1).
            return size == 0;
        }
    }

    /**
     * Stack item for DFS route search.
     */
    static class DfsStackFrame {
        int cityIndex;
        int predecessor;

        /**
         * Creates one DFS frame.
         *
         * @param cityIndex city index
         * @param predecessor predecessor city index
         */
        DfsStackFrame(int cityIndex, int predecessor) {
            // Time: O(1).
            this.cityIndex = cityIndex;
            this.predecessor = predecessor;
        }
    }

    /**
     * Fixed-size stack for iterative DFS.
     */
    static class FreightStack {
        DfsStackFrame[] frames;
        int top;

        /**
         * Creates stack.
         *
         * @param capacity max frames
         */
        FreightStack(int capacity) {
            // Time: O(capacity) allocation.
            frames = new DfsStackFrame[capacity];
            top = -1;
        }

        /**
         * Pushes frame.
         *
         * @param frame frame to push
         */
        void push(DfsStackFrame frame) {
            // Time: O(1).
            top++;
            frames[top] = frame;
        }

        /**
         * Pops frame.
         *
         * @return popped frame
         */
        DfsStackFrame pop() {
            // Time: O(1).
            DfsStackFrame value = frames[top];
            top--;
            return value;
        }

        /**
         * Empty check.
         *
         * @return true when empty
         */
        boolean isEmpty() {
            // Time: O(1).
            return top == -1;
        }
    }

    /**
     * Directed weighted graph stored as adjacency list.
     */
    static class FreightRoadGraph {
        String[] cityNames;
        RoadEdgeNode[] adjacencyHeads;
        int cityCount;

        /**
         * Creates graph.
         */
        FreightRoadGraph() {
            // Time: O(V) allocation for fixed arrays.
            cityNames = new String[MAX_CITIES];
            adjacencyHeads = new RoadEdgeNode[MAX_CITIES];
            cityCount = 0;
        }

        /**
         * Adds city vertex.
         *
         * @param cityName city name
         * @return city index
         */
        int addCity(String cityName) {
            // Time: O(1).
            cityNames[cityCount] = cityName;
            cityCount++;
            return cityCount - 1;
        }

        /**
         * Adds directed weighted road edge.
         *
         * @param from source city index
         * @param to destination city index
         * @param distanceKm distance in km
         */
        void addDirectedRoad(int from, int to, int distanceKm) {
            // Time: O(1) prepend into adjacency list.
            RoadEdgeNode edge = new RoadEdgeNode(to, distanceKm);
            edge.next = adjacencyHeads[from];
            adjacencyHeads[from] = edge;
        }

        /**
         * Finds city index by name.
         *
         * @param cityName city name
         * @return index or -1
         */
        int findCityIndex(String cityName) {
            // Time: O(V).
            for (int i = 0; i < cityCount; i++) {
                if (cityNames[i].equals(cityName)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * BFS to list all cities reachable within hop limit.
         *
         * @param sourceName source city name
         * @param hopLimit max road hops
         * @return readable result string
         */
        String bfsReachableWithinHops(String sourceName, int hopLimit) {
            // Time: O(V + E) in visited subgraph.
            int source = findCityIndex(sourceName);
            if (source == -1) {
                return "SOURCE CITY NOT FOUND";
            }
            if (hopLimit < 0) {
                return "NO REACHABLE CITIES (negative hop limit)";
            }

            boolean[] visited = new boolean[cityCount];
            int[] hops = new int[cityCount];
            for (int i = 0; i < cityCount; i++) {
                hops[i] = -1;
            }

            FreightQueue queue = new FreightQueue(cityCount + 5);
            visited[source] = true;
            hops[source] = 0;
            queue.enqueue(new HopVisitEntry(source, 0));

            while (!queue.isEmpty()) {
                HopVisitEntry current = queue.dequeue();
                if (current.hops == hopLimit) {
                    continue;
                }

                RoadEdgeNode edgeCursor = adjacencyHeads[current.cityIndex];
                while (edgeCursor != null) {
                    int neighbor = edgeCursor.destinationCityIndex;
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        hops[neighbor] = current.hops + 1;
                        queue.enqueue(new HopVisitEntry(neighbor, current.hops + 1));
                    }
                    edgeCursor = edgeCursor.next;
                }
            }

            StringBuilder out = new StringBuilder();
            boolean first = true;
            int count = 0;
            for (int i = 0; i < cityCount; i++) {
                if (hops[i] != -1 && hops[i] <= hopLimit) {
                    if (!first) {
                        out.append(" | ");
                    }
                    out.append(cityNames[i]).append("(hops=").append(hops[i]).append(")");
                    first = false;
                    count++;
                }
            }
            out.append(" (reachable=").append(count).append(")");
            return out.toString();
        }

        /**
         * DFS route-existence test that avoids closed cities.
         *
         * @param sourceName source city
         * @param destinationName destination city
         * @param closedCities closed-city flags by city index
         * @return path string or failure message
         */
        String dfsRouteAvoidingClosures(String sourceName, String destinationName, boolean[] closedCities) {
            // Time: O(V + E) traversal of reachable subgraph.
            int source = findCityIndex(sourceName);
            int destination = findCityIndex(destinationName);
            if (source == -1 || destination == -1) {
                return "SOURCE OR DESTINATION NOT FOUND";
            }
            if (closedCities[source]) {
                return "NO ROUTE (source city is closed)";
            }
            if (closedCities[destination]) {
                return "NO ROUTE (destination city is closed)";
            }

            boolean[] visited = new boolean[cityCount];
            int[] predecessor = new int[cityCount];
            for (int i = 0; i < cityCount; i++) {
                predecessor[i] = -1;
            }

            FreightStack stack = new FreightStack(cityCount + 5);
            stack.push(new DfsStackFrame(source, -1));

            while (!stack.isEmpty()) {
                DfsStackFrame frame = stack.pop();
                int current = frame.cityIndex;

                if (visited[current]) {
                    continue;
                }
                if (closedCities[current]) {
                    continue;
                }

                visited[current] = true;
                predecessor[current] = frame.predecessor;

                if (current == destination) {
                    break;
                }

                RoadEdgeNode edgeCursor = adjacencyHeads[current];
                while (edgeCursor != null) {
                    int nextCity = edgeCursor.destinationCityIndex;
                    if (!visited[nextCity] && !closedCities[nextCity]) {
                        stack.push(new DfsStackFrame(nextCity, current));
                    }
                    edgeCursor = edgeCursor.next;
                }
            }

            if (!visited[destination]) {
                return "NO ROUTE (closures block all DFS branches)";
            }

            int[] reversePath = new int[cityCount];
            int length = 0;
            int cursor = destination;
            while (cursor != -1) {
                reversePath[length] = cursor;
                length++;
                cursor = predecessor[cursor];
            }

            StringBuilder path = new StringBuilder();
            for (int i = length - 1; i >= 0; i--) {
                path.append(cityNames[reversePath[i]]);
                if (i != 0) {
                    path.append(" -> ");
                }
            }
            return path.toString();
        }

        /**
         * Builds built-in equivalent and compares DFS route existence outcome.
         *
         * @param sourceName source city
         * @param destinationName destination city
         * @param closedCities closure flags
         * @return true if built-in and custom report same reachability
         */
        boolean compareWithBuiltInEquivalent(String sourceName, String destinationName, boolean[] closedCities) {
            // Time: O(V + E) to build structures plus O(V + E) traversal.
            // Build name->index map and adjacency lists using Java built-ins for verification only.
            HashMap<String, Integer> nameToIndex = new HashMap<>();
            ArrayList<ArrayList<Integer>> builtAdj = new ArrayList<>();
            for (int i = 0; i < cityCount; i++) {
                nameToIndex.put(cityNames[i], i);
                builtAdj.add(new ArrayList<>());
            }

            for (int i = 0; i < cityCount; i++) {
                RoadEdgeNode cursor = adjacencyHeads[i];
                while (cursor != null) {
                    builtAdj.get(i).add(cursor.destinationCityIndex);
                    cursor = cursor.next;
                }
            }

            Integer source = nameToIndex.get(sourceName);
            Integer dest = nameToIndex.get(destinationName);
            if (source == null || dest == null) {
                return dfsRouteAvoidingClosures(sourceName, destinationName, closedCities)
                        .equals("SOURCE OR DESTINATION NOT FOUND");
            }

            boolean[] visited = new boolean[cityCount];
            ArrayList<Integer> stack = new ArrayList<>();
            stack.add(source);

            while (!stack.isEmpty()) {
                int current = stack.remove(stack.size() - 1);
                if (visited[current] || closedCities[current]) {
                    continue;
                }
                visited[current] = true;
                if (current == dest) {
                    break;
                }
                ArrayList<Integer> neighbors = builtAdj.get(current);
                for (int i = 0; i < neighbors.size(); i++) {
                    int next = neighbors.get(i);
                    if (!visited[next] && !closedCities[next]) {
                        stack.add(next);
                    }
                }
            }

            boolean builtInReachable = visited[dest];
            boolean customReachable = !dfsRouteAvoidingClosures(sourceName, destinationName, closedCities)
                    .startsWith("NO ROUTE")
                    && !dfsRouteAvoidingClosures(sourceName, destinationName, closedCities)
                    .equals("SOURCE OR DESTINATION NOT FOUND");

            return builtInReachable == customReachable;
        }
    }

    /**
     * Builds the freight network graph.
     *
     * @return graph
     */
    static FreightRoadGraph buildFreightGraph() {
        // Time: O(V + E) fixed setup.
        FreightRoadGraph graph = new FreightRoadGraph();

        int johannesburg = graph.addCity("Johannesburg");
        int durban = graph.addCity("Durban");
        int capeTown = graph.addCity("Cape Town");
        int bloemfontein = graph.addCity("Bloemfontein");
        int gqeberha = graph.addCity("Gqeberha");
        int eastLondon = graph.addCity("East London");
        int george = graph.addCity("George");
        int kimberley = graph.addCity("Kimberley");
        int polokwane = graph.addCity("Polokwane");
        int mbombela = graph.addCity("Mbombela");
        int rustenburg = graph.addCity("Rustenburg");
        int pretoria = graph.addCity("Pretoria");
        int pietermaritzburg = graph.addCity("Pietermaritzburg");
        int upington = graph.addCity("Upington");

        graph.addDirectedRoad(johannesburg, pretoria, 60);
        graph.addDirectedRoad(pretoria, polokwane, 260);
        graph.addDirectedRoad(johannesburg, bloemfontein, 400);
        graph.addDirectedRoad(bloemfontein, capeTown, 1000);
        graph.addDirectedRoad(johannesburg, durban, 570);
        graph.addDirectedRoad(durban, pietermaritzburg, 80);
        graph.addDirectedRoad(pietermaritzburg, eastLondon, 660);
        graph.addDirectedRoad(eastLondon, gqeberha, 300);
        graph.addDirectedRoad(gqeberha, george, 330);
        graph.addDirectedRoad(george, capeTown, 430);
        graph.addDirectedRoad(johannesburg, rustenburg, 120);
        graph.addDirectedRoad(rustenburg, kimberley, 500);
        graph.addDirectedRoad(kimberley, upington, 400);
        graph.addDirectedRoad(upington, capeTown, 820);
        graph.addDirectedRoad(johannesburg, mbombela, 350);

        return graph;
    }

    /**
     * Builds closure mask from city names.
     *
     * @param graph graph instance
     * @param closedNames names to close
     * @return closure flags by index
     */
    static boolean[] closureMask(FreightRoadGraph graph, String[] closedNames) {
        // Time: O(V * C) with linear city lookup by name.
        boolean[] closed = new boolean[graph.cityCount];
        for (int i = 0; i < closedNames.length; i++) {
            int idx = graph.findCityIndex(closedNames[i]);
            if (idx != -1) {
                closed[idx] = true;
            }
        }
        return closed;
    }

    /**
     * Main demonstration.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== South African Road Freight Network (Advanced Graph Traversal) ===");

        FreightRoadGraph graph = buildFreightGraph();

        System.out.println("\n-- BFS: cities reachable from Johannesburg within 3 hops --");
        System.out.println("  " + graph.bfsReachableWithinHops("Johannesburg", 3));

        System.out.println("\n-- DFS route existence avoiding closed cities (none closed) --");
        boolean[] noneClosed = closureMask(graph, new String[]{});
        System.out.println("  " + graph.dfsRouteAvoidingClosures("Johannesburg", "Cape Town", noneClosed));

        System.out.println("\n-- DFS route avoiding closures: close Bloemfontein and George --");
        boolean[] someClosed = closureMask(graph, new String[]{"Bloemfontein", "George"});
        System.out.println("  " + graph.dfsRouteAvoidingClosures("Johannesburg", "Cape Town", someClosed));

        System.out.println("\n-- DFS route avoiding closures: close Bloemfontein, George, and Upington --");
        boolean[] heavilyClosed = closureMask(graph, new String[]{"Bloemfontein", "George", "Upington"});
        System.out.println("  " + graph.dfsRouteAvoidingClosures("Johannesburg", "Cape Town", heavilyClosed));

        System.out.println("\n-- Edge case: unknown city --");
        System.out.println("  " + graph.bfsReachableWithinHops("Atlantis", 2));

        System.out.println("\n-- Edge case: negative hop limit --");
        System.out.println("  " + graph.bfsReachableWithinHops("Johannesburg", -1));

        System.out.println("\n-- Built-in equivalent comparison check --");
        boolean equivalent = graph.compareWithBuiltInEquivalent(
                "Johannesburg", "Cape Town", someClosed);
        System.out.println("  Custom DFS reachability equals built-in model: " + equivalent);
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - vertex: city node in the freight network.
 - edge: directed road connection between cities.
 - adjacency list: per-city linked list of outgoing roads.
 - BFS: level-order expansion by hop distance.
 - DFS: deep branch exploration using an explicit stack.
 - visited set: boolean array preventing repeated city visits.
 - queue (BFS): ensures hop-layer traversal order.
 - stack (DFS): supports deep-first route exploration and backtracking behavior.

 Big-O:
 - addCity: O(1)
 - addDirectedRoad: O(1)
 - findCityIndex: O(V)
 - bfsReachableWithinHops: O(V + E)
 - dfsRouteAvoidingClosures: O(V + E)
 - compareWithBuiltInEquivalent: O(V + E)
 - closureMask: O(V * C) with linear lookups per closed city

 Interview questions:
 - Why does adjacency list save memory versus adjacency matrix for sparse road networks?
 - Why is BFS the right strategy for hop-limited compliance checks?
 - How do closures modify DFS reachability semantics?
 - Why can DFS prove existence but not shortest weighted route?
 - What changes if you need minimum-distance route instead of route existence?

 Common mistake and prevention:
 - Mistake: marking visited too late or ignoring closures, causing incorrect reachable results.
 - Avoided here: DFS/BFS skip closed nodes and mark visited consistently before expansion.

 Comparison guidance:
 - Use BFS for hop-constrained reachability.
 - Use DFS for yes/no route existence with blocked-city constraints.
 - Use Dijkstra for minimum-distance weighted routing when weights matter.
*/