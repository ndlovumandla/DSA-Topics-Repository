/*
 The Comrades seeding committee must assign entrants into batches while respecting per-batch
 capacity limits and maximizing a congestion-reduction objective. Each assignment decision changes
 available capacity for future entrants, so local greedy choices can easily block better global
 outcomes. Yusuf models this as dynamic programming: a state describes entrant prefix and remaining
 capacity budget, and transitions decide include/skip of each entrant in early-seeding slots.
 Overlapping subproblems appear heavily in recursion, making brute force impractical at scale.
 Top-down memoization and bottom-up tabulation both solve each state once, reducing runtime to O(n*m).
 This advanced version adds reconstruction, diagnostics, and a built-in equivalent solver for
 verification against independent implementation logic.
*/

/**
 * Advanced dynamic-programming demo for Comrades entrant seeding optimization.
 */
public class ComradesEntrantSeedingAdvancedApp {

    /**
     * One entrant candidate with benefit and capacity consumption.
     */
    static class EntrantProfile {
        int entrantId;
        int qualifyingTimeMinutes;
        int congestionBenefit;
        int capacityUnits;

        /**
         * Creates entrant profile.
         *
         * @param entrantId entrant id
         * @param qualifyingTimeMinutes qualification time in minutes
         * @param congestionBenefit optimization benefit score
         * @param capacityUnits batch capacity units consumed
         */
        EntrantProfile(int entrantId, int qualifyingTimeMinutes, int congestionBenefit, int capacityUnits) {
            // Time: O(1).
            this.entrantId = entrantId;
            this.qualifyingTimeMinutes = qualifyingTimeMinutes;
            this.congestionBenefit = congestionBenefit;
            this.capacityUnits = capacityUnits;
        }
    }

    /**
     * Solution result wrapper.
     */
    static class SeedingSolution {
        int maxBenefit;
        String allocation;
        long elapsedNanos;

        /**
         * Creates a solution object.
         *
         * @param maxBenefit maximum benefit
         * @param allocation chosen entrants summary
         * @param elapsedNanos runtime in ns
         */
        SeedingSolution(int maxBenefit, String allocation, long elapsedNanos) {
            // Time: O(1).
            this.maxBenefit = maxBenefit;
            this.allocation = allocation;
            this.elapsedNanos = elapsedNanos;
        }
    }

    /**
     * Top-down memoized solve entry.
     *
     * @param entrants entrants
     * @param totalCapacity capacity budget
     * @return solution details
     */
    static SeedingSolution solveTopDownMemoized(EntrantProfile[] entrants, int totalCapacity) {
        // Time: O(n*m), Space: O(n*m) cache + O(n) recursion stack.
        long start = System.nanoTime();

        int[][] memo = new int[entrants.length][totalCapacity + 1];
        for (int i = 0; i < entrants.length; i++) {
            for (int c = 0; c <= totalCapacity; c++) {
                memo[i][c] = -1;
            }
        }

        int best = topDownRecurrence(entrants, 0, totalCapacity, memo);
        String allocation = reconstructFromTopDownMemo(entrants, totalCapacity, memo);
        long elapsed = System.nanoTime() - start;

        return new SeedingSolution(best, allocation, elapsed);
    }

    /**
     * Top-down recurrence.
     *
     * @param entrants entrant list
     * @param idx current index
     * @param remainingCapacity remaining capacity
     * @param memo cache table
     * @return best score
     */
    static int topDownRecurrence(EntrantProfile[] entrants, int idx, int remainingCapacity, int[][] memo) {
        // Time: O(n*m) across all states.
        if (idx == entrants.length || remainingCapacity == 0) {
            return 0;
        }
        if (memo[idx][remainingCapacity] != -1) {
            return memo[idx][remainingCapacity];
        }

        int skip = topDownRecurrence(entrants, idx + 1, remainingCapacity, memo);
        int include = 0;
        if (entrants[idx].capacityUnits <= remainingCapacity) {
            include = entrants[idx].congestionBenefit
                    + topDownRecurrence(
                    entrants,
                    idx + 1,
                    remainingCapacity - entrants[idx].capacityUnits,
                    memo);
        }

        memo[idx][remainingCapacity] = (include > skip) ? include : skip;
        return memo[idx][remainingCapacity];
    }

    /**
     * Reconstructs one optimal selection from memoized values.
     *
     * @param entrants entrant list
     * @param totalCapacity capacity
     * @param memo memo table
     * @return chosen entrants string
     */
    static String reconstructFromTopDownMemo(EntrantProfile[] entrants, int totalCapacity, int[][] memo) {
        // Time: O(n) path walk through decision boundary.
        StringBuilder builder = new StringBuilder();
        int idx = 0;
        int cap = totalCapacity;
        boolean first = true;

        while (idx < entrants.length && cap >= 0) {
            int skip = (idx + 1 < entrants.length) ? memo[idx + 1][cap] : 0;
            int include = -1;

            if (entrants[idx].capacityUnits <= cap) {
                int remainder = cap - entrants[idx].capacityUnits;
                int next = (idx + 1 < entrants.length) ? memo[idx + 1][remainder] : 0;
                include = entrants[idx].congestionBenefit + next;
            }

            int current = memo[idx][cap];
            if (include == current && include >= skip) {
                if (!first) {
                    builder.append(" | ");
                }
                builder.append("E").append(entrants[idx].entrantId)
                        .append("(benefit=").append(entrants[idx].congestionBenefit)
                        .append(",cap=").append(entrants[idx].capacityUnits).append(")");
                first = false;
                cap -= entrants[idx].capacityUnits;
            }
            idx++;
        }

        if (builder.length() == 0) {
            return "No entrants selected";
        }
        return builder.toString();
    }

    /**
     * Bottom-up tabulated solve.
     *
     * @param entrants entrant list
     * @param totalCapacity capacity
     * @return solution
     */
    static SeedingSolution solveBottomUpTabulated(EntrantProfile[] entrants, int totalCapacity) {
        // Time: O(n*m), Space: O(n*m).
        long start = System.nanoTime();

        int n = entrants.length;
        int[][] dp = new int[n + 1][totalCapacity + 1];

        for (int i = 1; i <= n; i++) {
            int benefit = entrants[i - 1].congestionBenefit;
            int cost = entrants[i - 1].capacityUnits;

            for (int c = 0; c <= totalCapacity; c++) {
                int skip = dp[i - 1][c];
                int include = (cost <= c) ? (benefit + dp[i - 1][c - cost]) : 0;
                dp[i][c] = (include > skip) ? include : skip;
            }
        }

        String allocation = reconstructFromBottomUpTable(entrants, totalCapacity, dp);
        long elapsed = System.nanoTime() - start;
        return new SeedingSolution(dp[n][totalCapacity], allocation, elapsed);
    }

    /**
     * Reconstructs chosen entrants from bottom-up DP table.
     *
     * @param entrants entrant list
     * @param totalCapacity capacity
     * @param dp table
     * @return allocation string
     */
    static String reconstructFromBottomUpTable(EntrantProfile[] entrants, int totalCapacity, int[][] dp) {
        // Time: O(n) backtracking through table rows.
        int i = entrants.length;
        int c = totalCapacity;

        int[] selected = new int[entrants.length];
        int selectedCount = 0;

        while (i > 0 && c >= 0) {
            if (dp[i][c] == dp[i - 1][c]) {
                i--;
            } else {
                selected[selectedCount] = i - 1;
                selectedCount++;
                c -= entrants[i - 1].capacityUnits;
                i--;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int idx = selectedCount - 1; idx >= 0; idx--) {
            int entrantIdx = selected[idx];
            if (idx != selectedCount - 1) {
                builder.append(" | ");
            }
            builder.append("E").append(entrants[entrantIdx].entrantId)
                    .append("(benefit=").append(entrants[entrantIdx].congestionBenefit)
                    .append(",cap=").append(entrants[entrantIdx].capacityUnits).append(")");
        }
        if (selectedCount == 0) {
            return "No entrants selected";
        }
        return builder.toString();
    }

    /**
     * Built-in equivalent using Java arrays for independent verification.
     *
     * @param entrants entrant list
     * @param totalCapacity capacity
     * @return maximum benefit from equivalent implementation
     */
    static int builtInEquivalentCheck(EntrantProfile[] entrants, int totalCapacity) {
        // Time: O(n*m) equivalent table filling.
        int n = entrants.length;
        int[][] dp = new int[n + 1][totalCapacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= totalCapacity; c++) {
                dp[i][c] = dp[i - 1][c];
                if (entrants[i - 1].capacityUnits <= c) {
                    dp[i][c] = Math.max(dp[i][c],
                            entrants[i - 1].congestionBenefit + dp[i - 1][c - entrants[i - 1].capacityUnits]);
                }
            }
        }
        return dp[n][totalCapacity];
    }

    /**
     * Builds deterministic entrant sample.
     *
     * @return entrant profiles
     */
    static EntrantProfile[] buildEntrants() {
        // Time: O(1) fixed setup.
        return new EntrantProfile[]{
                new EntrantProfile(1001, 190, 14, 4),
                new EntrantProfile(1002, 205, 9, 3),
                new EntrantProfile(1003, 198, 13, 5),
                new EntrantProfile(1004, 212, 8, 2),
                new EntrantProfile(1005, 201, 12, 4),
                new EntrantProfile(1006, 220, 7, 2),
                new EntrantProfile(1007, 195, 15, 6),
                new EntrantProfile(1008, 208, 10, 3)
        };
    }

    /**
     * Prints compact entrant list.
     *
     * @param entrants entrant array
     */
    static void printEntrants(EntrantProfile[] entrants) {
        // Time: O(n).
        for (int i = 0; i < entrants.length; i++) {
            System.out.println("  E" + entrants[i].entrantId
                    + " time=" + entrants[i].qualifyingTimeMinutes
                    + " benefit=" + entrants[i].congestionBenefit
                    + " cap=" + entrants[i].capacityUnits);
        }
    }

    /**
     * Main demonstration.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== Comrades Entrant Seeding System (Advanced Dynamic Programming) ===");

        EntrantProfile[] entrants = buildEntrants();
        int capacity = 15;

        System.out.println("\n-- Input entrant profiles --");
        printEntrants(entrants);

        System.out.println("\n-- Main scenario: optimize under capacity 15 --");
        SeedingSolution topDown = solveTopDownMemoized(entrants, capacity);
        SeedingSolution bottomUp = solveBottomUpTabulated(entrants, capacity);
        int builtIn = builtInEquivalentCheck(entrants, capacity);

        System.out.println("  Top-down max benefit: " + topDown.maxBenefit + " (" + topDown.elapsedNanos + " ns)");
        System.out.println("  Top-down allocation: " + topDown.allocation);
        System.out.println("  Bottom-up max benefit: " + bottomUp.maxBenefit + " (" + bottomUp.elapsedNanos + " ns)");
        System.out.println("  Bottom-up allocation: " + bottomUp.allocation);
        System.out.println("  Built-in equivalent max benefit: " + builtIn);
        System.out.println("  All methods agree: " + (topDown.maxBenefit == bottomUp.maxBenefit
                && topDown.maxBenefit == builtIn));

        System.out.println("\n-- Edge case: empty entrant list --");
        EntrantProfile[] empty = new EntrantProfile[0];
        SeedingSolution emptySolve = solveBottomUpTabulated(empty, 10);
        System.out.println("  Max benefit: " + emptySolve.maxBenefit);
        System.out.println("  Allocation: " + emptySolve.allocation);

        System.out.println("\n-- Edge case: zero capacity --");
        SeedingSolution zeroCap = solveBottomUpTabulated(entrants, 0);
        System.out.println("  Max benefit: " + zeroCap.maxBenefit);
        System.out.println("  Allocation: " + zeroCap.allocation);

        System.out.println("\n-- Edge case: duplicate entrant benefits --");
        EntrantProfile[] duplicateBenefits = new EntrantProfile[]{
                new EntrantProfile(2001, 210, 5, 2),
                new EntrantProfile(2002, 211, 5, 2),
                new EntrantProfile(2003, 212, 5, 2),
                new EntrantProfile(2004, 213, 5, 2)
        };
        SeedingSolution dupSolve = solveBottomUpTabulated(duplicateBenefits, 4);
        System.out.println("  Max benefit: " + dupSolve.maxBenefit);
        System.out.println("  Allocation: " + dupSolve.allocation);
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - memoization: recursive DP state results cached in memo table.
 - tabulation: bottom-up DP table computes all states iteratively.
 - overlapping subproblems: repeated (entrantIndex, remainingCapacity) states.
 - optimal substructure: best state derives from best of include vs skip transitions.
 - bottom-up: iterate entrants and capacities from small to large states.
 - top-down: recurse from full problem and cache sub-results.
 - cache: memo[][] eliminates repeated state recomputation.

 Big-O:
 - solveTopDownMemoized / topDownRecurrence: O(n*m) time, O(n*m) space.
 - solveBottomUpTabulated: O(n*m) time, O(n*m) space.
 - reconstruction methods: O(n).
 - builtInEquivalentCheck: O(n*m).
 - printEntrants: O(n).

 Interview questions:
 - Why does this seeding optimization map to knapsack-like DP?
 - What state definition makes transitions simple and correct?
 - Why do top-down and bottom-up produce identical optima?
 - How do you reconstruct choices, not just objective value?
 - When would you compress DP space to O(m) and what trade-off appears?

 Common mistake and prevention:
 - Mistake: using a transition that reuses current row in 0/1 allocation, accidentally allowing duplicates.
 - Avoided here: bottom-up transition always reads previous row (i-1), preserving 0/1 semantics.

 Comparison guidance:
 - Use DP for constrained optimization with capacity-like limits and overlapping states.
 - Use greedy only when problem has proven greedy-choice property; this one does not generally.
*/