/*
 The Drakensberg mountain range has hundreds of named trails that branch into a web of junctions.
 At each junction, hikers can choose one of multiple sub-trails, and some paths loop back to earlier
 points, creating cycles. The KZN Wildlife Authority wants a discovery tool that lists every complete
 route from a start camp to a target peak. This is naturally recursive: from the current junction,
 explore each connected trail, and when one branch fails, backtrack and try the next branch.
 Trail data developer Sibusiso implements recursive depth-first route discovery with cycle protection.
 He documents the base case (target reached), recursive case (visit each neighbor), and backtracking
 step (undo path/visited state) so learners can see why recursion models this tree-shaped problem
 more elegantly than deeply nested iterative loops.
*/

/**
 * Intermediate recursion project: find all hiking routes in a branched/cyclic trail graph.
 * Uses recursive DFS with explicit backtracking and cycle prevention.
 */
public class DrakensbergHikingTrailMapperApp {

    /** Maximum number of named junctions in this demo graph. */
    static final int MAX_JUNCTIONS = 20;

    /** Maximum route length we allow in one recursive path to guard stack growth. */
    static final int MAX_ROUTE_DEPTH = 30;

    /**
     * Represents the trail network using a fixed-size adjacency matrix and name table.
     * This avoids Java collection built-ins for core graph logic.
     */
    static class TrailNetwork {
        String[] junctionNames = new String[MAX_JUNCTIONS];
        int junctionCount = 0;

        // adjacency[from][to] == true means there is a directed trail from "from" to "to".
        boolean[][] adjacency = new boolean[MAX_JUNCTIONS][MAX_JUNCTIONS];

        /**
         * Adds a named junction and returns its index.
         *
         * @param junctionName name of the trail junction
         * @return index assigned to this junction
         */
        int addJunction(String junctionName) {
            // Time: O(1) - append into fixed-size arrays.
            if (junctionCount >= MAX_JUNCTIONS) {
                throw new IllegalStateException("Trail network capacity reached.");
            }
            junctionNames[junctionCount] = junctionName;
            junctionCount++;
            return junctionCount - 1;
        }

        /**
         * Adds a directed trail segment from one junction to another.
         *
         * @param fromIndex source junction index
         * @param toIndex destination junction index
         */
        void addTrail(int fromIndex, int toIndex) {
            // Time: O(1) - one matrix cell assignment.
            adjacency[fromIndex][toIndex] = true;
        }

        /**
         * Finds a junction index by name.
         *
         * @param targetName name to search
         * @return index if found, otherwise -1
         */
        int findJunctionIndex(String targetName) {
            // Time: O(n) - scans junction name array.
            for (int index = 0; index < junctionCount; index++) {
                if (junctionNames[index].equals(targetName)) {
                    return index;
                }
            }
            return -1;
        }
    }

    /**
     * Holds route-discovery output without using collection classes.
     */
    static class RouteCatalogue {
        String[] routeDescriptions;
        int routeCount;

        /**
         * Creates a route catalogue with fixed capacity.
         *
         * @param capacity maximum routes to store
         */
        RouteCatalogue(int capacity) {
            // Time: O(capacity) for array allocation.
            routeDescriptions = new String[capacity];
            routeCount = 0;
        }

        /**
         * Adds a route description if capacity remains.
         *
         * @param routeText route string like "Monks Cowl -> Blindman's Corner -> Cathkin Peak"
         */
        void addRoute(String routeText) {
            // Time: O(1) append operation.
            if (routeCount < routeDescriptions.length) {
                routeDescriptions[routeCount] = routeText;
                routeCount++;
            }
        }
    }

    /**
     * Recursively discovers all routes from start junction to destination.
     *
     * @param network trail network graph
     * @param currentIndex current junction index
     * @param destinationIndex target destination junction index
     * @param visited marker array for cycle prevention
     * @param routePath current route path as sequence of junction indices
     * @param routeDepth current depth in routePath
     * @param routeCatalogue where discovered complete routes are stored
     */
    static void discoverRoutesRecursive(
            TrailNetwork network,
            int currentIndex,
            int destinationIndex,
            boolean[] visited,
            int[] routePath,
            int routeDepth,
            RouteCatalogue routeCatalogue) {

        // Time: O(V + E) for acyclic traversal in this DFS branch,
        //       but route enumeration can grow exponentially in branching graphs.

        // Guard base case for stack safety in malformed cyclic maps.
        if (routeDepth >= MAX_ROUTE_DEPTH) {
            return;
        }

        // Include current junction in the active route path.
        routePath[routeDepth] = currentIndex;

        // Base case: reached destination peak, store route and stop descending this branch.
        if (currentIndex == destinationIndex) {
            routeCatalogue.addRoute(buildRouteText(network, routePath, routeDepth + 1));
            return;
        }

        // Mark as visited before exploring outgoing trails.
        visited[currentIndex] = true;

        // Recursive case: explore every connected sub-trail.
        for (int neighborIndex = 0; neighborIndex < network.junctionCount; neighborIndex++) {
            if (network.adjacency[currentIndex][neighborIndex] && !visited[neighborIndex]) {
                discoverRoutesRecursive(
                        network,
                        neighborIndex,
                        destinationIndex,
                        visited,
                        routePath,
                        routeDepth + 1,
                        routeCatalogue);
            }
        }

        // Backtracking step: unmark current junction so alternative branches can reuse it.
        visited[currentIndex] = false;
    }

    /**
     * Builds human-readable route text from the path index array.
     *
     * @param network trail network
     * @param routePath path indices
     * @param length number of used positions in routePath
     * @return route description string
     */
    static String buildRouteText(TrailNetwork network, int[] routePath, int length) {
        // Time: O(length) - iterate once over route nodes.
        StringBuilder routeBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                routeBuilder.append(" -> ");
            }
            routeBuilder.append(network.junctionNames[routePath[i]]);
        }
        return routeBuilder.toString();
    }

    /**
     * Public route-finding API.
     *
     * @param network trail network
     * @param startName start junction name
     * @param destinationName destination peak name
     * @return catalogue of all complete routes found
     */
    static RouteCatalogue findAllRoutes(TrailNetwork network, String startName, String destinationName) {
        // Time: varies with graph branching; worst-case route enumeration can be exponential.
        int startIndex = network.findJunctionIndex(startName);
        int destinationIndex = network.findJunctionIndex(destinationName);

        // Edge case: unknown start or destination.
        if (startIndex == -1 || destinationIndex == -1) {
            return new RouteCatalogue(1);
        }

        RouteCatalogue routes = new RouteCatalogue(200);
        boolean[] visited = new boolean[network.junctionCount];
        int[] routePath = new int[MAX_ROUTE_DEPTH];

        discoverRoutesRecursive(network, startIndex, destinationIndex, visited, routePath, 0, routes);
        return routes;
    }

    /**
     * Recursively computes factorial for recursion concept practice.
     *
     * @param n non-negative number
     * @return n!
     */
    static long factorialTrails(int n) {
        // Time: O(n) - one recursive call per decrement.
        if (n < 0) {
            throw new IllegalArgumentException("Factorial requires n >= 0");
        }
        if (n <= 1) {
            return 1;
        }
        return n * factorialTrails(n - 1);
    }

    /**
     * Naive recursive Fibonacci to demonstrate recursion tree growth.
     *
     * @param n position in sequence
     * @return Fibonacci(n)
     */
    static long fibonacciNaiveTrail(int n) {
        // Time: O(2^n) - branches into two recursive calls repeatedly.
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci requires n >= 0");
        }
        if (n <= 1) {
            return n;
        }
        return fibonacciNaiveTrail(n - 1) + fibonacciNaiveTrail(n - 2);
    }

    /**
     * Memoized recursive Fibonacci with array cache.
     *
     * @param n position in sequence
     * @param memo cache array initialized to -1
     * @return Fibonacci(n)
     */
    static long fibonacciMemoizedTrail(int n, long[] memo) {
        // Time: O(n) - each value computed once, then cached.
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci requires n >= 0");
        }
        if (n <= 1) {
            return n;
        }
        if (memo[n] != -1) {
            return memo[n];
        }
        memo[n] = fibonacciMemoizedTrail(n - 1, memo) + fibonacciMemoizedTrail(n - 2, memo);
        return memo[n];
    }

    /**
     * Demonstrates safe recursion depth counter.
     *
     * @param depth countdown depth
     * @return total frames traversed
     */
    static int safeDepthCounter(int depth) {
        // Time: O(depth), Space: O(depth) call stack.
        if (depth == 0) {
            return 0;
        }
        return 1 + safeDepthCounter(depth - 1);
    }

    /**
     * Builds the Drakensberg demo network with branches and cycles.
     *
     * @return populated trail network
     */
    static TrailNetwork buildDrakensbergNetwork() {
        // Time: O(V + E) for fixed V/E setup in this demo.
        TrailNetwork network = new TrailNetwork();

        int monksCowl = network.addJunction("Monks Cowl Camp");
        int blindmansCorner = network.addJunction("Blindman's Corner");
        int sphinxJunction = network.addJunction("Sphinx Junction");
        int mlambonjaPass = network.addJunction("Mlambonja Pass");
        int crystalFalls = network.addJunction("Crystal Falls");
        int cathkinPeak = network.addJunction("Cathkin Peak");
        int champagneCastle = network.addJunction("Champagne Castle");
        int nkosazanaStream = network.addJunction("Nkosazana Stream");

        // Directed connections; include loops to model real trail web complexity.
        network.addTrail(monksCowl, blindmansCorner);
        network.addTrail(monksCowl, sphinxJunction);
        network.addTrail(blindmansCorner, mlambonjaPass);
        network.addTrail(blindmansCorner, crystalFalls);
        network.addTrail(sphinxJunction, crystalFalls);
        network.addTrail(crystalFalls, mlambonjaPass);
        network.addTrail(mlambonjaPass, cathkinPeak);
        network.addTrail(mlambonjaPass, champagneCastle);
        network.addTrail(champagneCastle, nkosazanaStream);
        network.addTrail(nkosazanaStream, blindmansCorner); // cycle back

        return network;
    }

    /**
     * Demonstrates route discovery and recursion fundamentals.
     *
     * @param args unused command-line args
     */
    public static void main(String[] args) {
        System.out.println("=== Drakensberg Hiking Trail Mapper (Recursive DFS + Backtracking) ===");

        TrailNetwork network = buildDrakensbergNetwork();

        System.out.println("\n-- Main Scenario: Monks Cowl Camp to Cathkin Peak --");
        RouteCatalogue routesToCathkin = findAllRoutes(network, "Monks Cowl Camp", "Cathkin Peak");

        if (routesToCathkin.routeCount == 0) {
            System.out.println("  No routes found.");
        } else {
            for (int i = 0; i < routesToCathkin.routeCount; i++) {
                System.out.println("  Route " + (i + 1) + ": " + routesToCathkin.routeDescriptions[i]);
            }
            System.out.println("  Total complete routes found: " + routesToCathkin.routeCount);
        }

        System.out.println("\n-- Edge Case: Unknown Destination Peak --");
        RouteCatalogue missingPeakRoutes = findAllRoutes(network, "Monks Cowl Camp", "Injasuthi Summit");
        System.out.println("  Routes found: " + missingPeakRoutes.routeCount + " (expected 0)");

        System.out.println("\n-- Edge Case: Single-node start equals destination --");
        RouteCatalogue sameStartEnd = findAllRoutes(network, "Monks Cowl Camp", "Monks Cowl Camp");
        System.out.println("  Routes found: " + sameStartEnd.routeCount);
        if (sameStartEnd.routeCount > 0) {
            System.out.println("  Route: " + sameStartEnd.routeDescriptions[0]);
        }

        System.out.println("\n-- Recursion Fundamentals --");
        System.out.println("  factorial(6) = " + factorialTrails(6));

        int fibN = 32;
        long naiveStart = System.nanoTime();
        long naiveFib = fibonacciNaiveTrail(fibN);
        long naiveMs = (System.nanoTime() - naiveStart) / 1_000_000;

        long[] memo = new long[fibN + 1];
        for (int i = 0; i < memo.length; i++) {
            memo[i] = -1;
        }

        long memoStart = System.nanoTime();
        long memoFib = fibonacciMemoizedTrail(fibN, memo);
        long memoMs = (System.nanoTime() - memoStart) / 1_000_000;

        System.out.println("  fibonacciNaive(" + fibN + ") = " + naiveFib + " in " + naiveMs + " ms");
        System.out.println("  fibonacciMemoized(" + fibN + ") = " + memoFib + " in " + memoMs + " ms");

        int safeDepth = 120;
        System.out.println("  Safe recursion depth reached: " + safeDepthCounter(safeDepth));
        System.out.println("  Note: without base cases and cycle checks, recursive DFS can stack overflow.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - base case: stop recursion when destination is reached or search window is exhausted.
 - recursive call: DFS explores each neighboring trail by calling itself.
 - call stack: each branch exploration is represented by stacked function frames.
 - backtracking: unmark visited node after exploring a branch so other branches can reuse it.
 - factorial: linear recursion example with one subproblem per call.
 - Fibonacci: overlapping subproblems in naive recursion.
 - memoization: cache Fibonacci results to reduce recomputation.
 - stack overflow: risk when recursion depth grows too large without controls.

 Big-O of implemented operations:
 - findAllRoutes / discoverRoutesRecursive: varies by graph; route enumeration can be exponential.
 - buildRouteText: O(L) where L is current route length.
 - findJunctionIndex: O(V) scan over junction names.
 - factorialTrails: O(n) time, O(n) stack space.
 - fibonacciNaiveTrail: O(2^n) time, O(n) stack space.
 - fibonacciMemoizedTrail: O(n) time, O(n) stack + O(n) memo space.
 - safeDepthCounter: O(d) time and O(d) stack space.

 Interview questions this prepares you for:
 - Why do DFS/backtracking solutions need explicit visited-state management in cyclic graphs?
 - What is the role of backtracking unmark operations in route enumeration?
 - Why can route enumeration become exponential even with DFS?
 - How does memoization change Fibonacci complexity from exponential to linear?
 - What practical safeguards prevent stack overflow in recursive graph traversals?

 Common mistake and prevention:
 - Mistake: forgetting to unmark visited nodes during backtracking, which hides valid alternative routes.
 - Avoided here: visited[currentIndex] is set before recursion and reset after branch exploration.

 When to use recursion vs the common alternative:
 - Use recursion + backtracking for tree/graph path enumeration where branch state is naturally nested.
 - Use iterative stacks/queues when recursion depth may exceed safe stack limits in very large graphs.
*/