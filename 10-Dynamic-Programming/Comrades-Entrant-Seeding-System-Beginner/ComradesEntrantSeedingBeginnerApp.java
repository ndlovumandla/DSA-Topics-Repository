/*
 The Comrades Marathon seeding committee must place thousands of entrants into starting batches so
 that each batch stays within capacity and congestion is minimized near the first 5 km. This is an
 optimization problem because each assignment decision affects future assignment options. Yusuf models
 a simplified version as a dynamic programming optimization where entrants are allocated across batch
 capacities to maximize smooth seeding score. The same partial states appear repeatedly, so a brute-
 force recursive solution recalculates identical subproblems many times. Dynamic programming fixes
 this by caching intermediate answers (top-down memoization) or filling a table once (bottom-up
 tabulation). The result is a clear reduction from exponential-style recomputation toward O(n*m),
 where n is entrants considered and m is capacity budget.
*/

/**
 * Beginner dynamic-programming demo for Comrades seeding allocation.
 */
public class ComradesEntrantSeedingBeginnerApp {

    /**
     * Represents one entrant's congestion impact score if allocated early.
     */
    static class EntrantSeedingScore {
        int entrantId;
        int seedingBenefit;
        int capacityCost;

        /**
         * Creates one entrant scoring record.
         *
         * @param entrantId entrant identifier
         * @param seedingBenefit congestion-reduction benefit points
         * @param capacityCost batch-capacity units consumed
         */
        EntrantSeedingScore(int entrantId, int seedingBenefit, int capacityCost) {
            // Time: O(1) constant field assignments.
            this.entrantId = entrantId;
            this.seedingBenefit = seedingBenefit;
            this.capacityCost = capacityCost;
        }
    }

    /**
     * Solves allocation using top-down memoization.
     *
     * @param entrants entrant score array
     * @param index current entrant index
     * @param remainingCapacity capacity units left
     * @param memo cache table (value -1 means unknown)
     * @return maximum seeding benefit achievable
     */
    static int maximizeBenefitTopDown(
            EntrantSeedingScore[] entrants,
            int index,
            int remainingCapacity,
            int[][] memo) {
        // Time: O(n*m) because each (index, remainingCapacity) state is solved once and cached.

        // Base case: no entrants left or no capacity left.
        if (index == entrants.length || remainingCapacity == 0) {
            return 0;
        }

        // Cache hit: reuse previously solved overlapping subproblem.
        if (memo[index][remainingCapacity] != -1) {
            return memo[index][remainingCapacity];
        }

        // Option 1: skip this entrant.
        int skipBenefit = maximizeBenefitTopDown(entrants, index + 1, remainingCapacity, memo);

        // Option 2: include this entrant if capacity allows.
        int includeBenefit = 0;
        if (entrants[index].capacityCost <= remainingCapacity) {
            includeBenefit = entrants[index].seedingBenefit
                    + maximizeBenefitTopDown(
                    entrants,
                    index + 1,
                    remainingCapacity - entrants[index].capacityCost,
                    memo);
        }

        // Optimal substructure: choose better of include vs skip.
        int best = (includeBenefit > skipBenefit) ? includeBenefit : skipBenefit;
        memo[index][remainingCapacity] = best;
        return best;
    }

    /**
     * Solves allocation using bottom-up tabulation.
     *
     * @param entrants entrant score array
     * @param totalCapacity capacity budget
     * @return maximum seeding benefit
     */
    static int maximizeBenefitBottomUp(EntrantSeedingScore[] entrants, int totalCapacity) {
        // Time: O(n*m), Space: O(n*m) for DP table.
        int n = entrants.length;
        int[][] dp = new int[n + 1][totalCapacity + 1];

        // Bottom-up fill: dp[i][c] means best using first i entrants and capacity c.
        for (int i = 1; i <= n; i++) {
            int benefit = entrants[i - 1].seedingBenefit;
            int cost = entrants[i - 1].capacityCost;

            for (int c = 0; c <= totalCapacity; c++) {
                int skip = dp[i - 1][c];
                int include = 0;
                if (cost <= c) {
                    include = benefit + dp[i - 1][c - cost];
                }
                dp[i][c] = (include > skip) ? include : skip;
            }
        }

        return dp[n][totalCapacity];
    }

    /**
     * Prints a compact DP table for teaching tabulation transitions.
     *
     * @param entrants entrant array
     * @param totalCapacity capacity
     */
    static void printSmallBottomUpTable(EntrantSeedingScore[] entrants, int totalCapacity) {
        // Time: O(n*m) for table fill and print.
        int n = entrants.length;
        int[][] dp = new int[n + 1][totalCapacity + 1];

        for (int i = 1; i <= n; i++) {
            int benefit = entrants[i - 1].seedingBenefit;
            int cost = entrants[i - 1].capacityCost;
            for (int c = 0; c <= totalCapacity; c++) {
                int skip = dp[i - 1][c];
                int include = (cost <= c) ? (benefit + dp[i - 1][c - cost]) : 0;
                dp[i][c] = (include > skip) ? include : skip;
            }
        }

        System.out.println("  DP table rows=entrants considered, cols=capacity");
        for (int i = 0; i <= n; i++) {
            System.out.print("  row " + i + ": ");
            for (int c = 0; c <= totalCapacity; c++) {
                System.out.print(dp[i][c]);
                if (c != totalCapacity) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Builds demo entrants.
     *
     * @return entrant array
     */
    static EntrantSeedingScore[] buildDemoEntrants() {
        // Time: O(1) fixed-size creation.
        return new EntrantSeedingScore[]{
                new EntrantSeedingScore(101, 9, 3),
                new EntrantSeedingScore(102, 6, 2),
                new EntrantSeedingScore(103, 12, 4),
                new EntrantSeedingScore(104, 7, 3),
                new EntrantSeedingScore(105, 10, 5)
        };
    }

    /**
     * Main demonstration.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== Comrades Entrant Seeding System (Beginner Dynamic Programming) ===");

        EntrantSeedingScore[] entrants = buildDemoEntrants();
        int capacity = 10;

        System.out.println("\n-- Main scenario: optimize seeding under capacity 10 --");
        int[][] memo = new int[entrants.length][capacity + 1];
        for (int i = 0; i < entrants.length; i++) {
            for (int c = 0; c <= capacity; c++) {
                memo[i][c] = -1;
            }
        }
        int topDownBest = maximizeBenefitTopDown(entrants, 0, capacity, memo);
        int bottomUpBest = maximizeBenefitBottomUp(entrants, capacity);
        System.out.println("  Top-down memoized result: " + topDownBest);
        System.out.println("  Bottom-up tabulated result: " + bottomUpBest);
        System.out.println("  Match: " + (topDownBest == bottomUpBest));

        System.out.println("\n-- Small DP table visualization --");
        printSmallBottomUpTable(entrants, 10);

        System.out.println("\n-- Edge case: empty entrant list --");
        EntrantSeedingScore[] empty = new EntrantSeedingScore[0];
        System.out.println("  Result: " + maximizeBenefitBottomUp(empty, 10));

        System.out.println("\n-- Edge case: zero capacity --");
        System.out.println("  Result: " + maximizeBenefitBottomUp(entrants, 0));

        System.out.println("\n-- Edge case: single entrant --");
        EntrantSeedingScore[] one = new EntrantSeedingScore[]{new EntrantSeedingScore(201, 5, 2)};
        System.out.println("  Capacity 1 result: " + maximizeBenefitBottomUp(one, 1));
        System.out.println("  Capacity 2 result: " + maximizeBenefitBottomUp(one, 2));
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - memoization: top-down recursion stores solved states in cache.
 - tabulation: bottom-up DP table filled iteratively.
 - overlapping subproblems: same (index, capacity) state appears repeatedly.
 - optimal substructure: global optimum built from best include/skip choice per state.
 - bottom-up: solve smallest states first, build upward.
 - top-down: recurse from full problem, cache sub-results.
 - cache: memo[][] avoids repeated recursion work.

 Big-O:
 - maximizeBenefitTopDown: O(n*m) time, O(n*m) cache, O(n) recursion depth.
 - maximizeBenefitBottomUp: O(n*m) time, O(n*m) table space.
 - printSmallBottomUpTable: O(n*m).

 Interview questions:
 - Why does memoization reduce repeated recursion in optimization problems?
 - How do top-down and bottom-up DP differ in execution flow?
 - What is overlapping subproblems in this entrant assignment context?
 - What is optimal substructure and where is it used here?
 - How can table space be optimized from O(n*m) to O(m) in knapsack-like DP?

 Common mistake and prevention:
 - Mistake: forgetting to initialize memo cache with sentinel values.
 - Avoided here: memo[][] initialized to -1 so unknown states are distinguishable.

 Comparison guidance:
 - Use DP when brute-force recursion revisits subproblems.
 - Use greedy only when a formal greedy-choice proof exists; this allocation case needs DP.
*/