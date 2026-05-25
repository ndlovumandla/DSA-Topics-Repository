/*
 Sasol's Secunda-to-Cape Town pipeline includes many pump stations that need inspection during a
 limited maintenance window. A safety rule prevents adjacent stations from being inspected on the
 same day because shutdown surges propagate to neighbors. The scheduling objective is to maximize
 completed inspections under this adjacency constraint. Brute-force recursion tries include/skip at
 every station and revisits the same suffixes repeatedly, leading to exponential growth. Sizwe models
 this as dynamic programming (house-robber pattern): best up to station k is max(best up to k-1,
 best up to k-2 plus current station). Tabulation computes this bottom-up in O(n), and memoization
 solves the same recurrence top-down with caching.
*/

/**
 * Intermediate dynamic-programming project for Sasol pipeline maintenance scheduling.
 */
public class SasolPipelineMaintenanceScheduleApp {

    /**
     * Represents one pump station and whether inspection is required.
     */
    static class PumpStationInspection {
        int stationNumber;
        int inspectionValue;

        /**
         * Creates one station inspection record.
         *
         * @param stationNumber station identifier
         * @param inspectionValue value/priority of completing this station inspection
         */
        PumpStationInspection(int stationNumber, int inspectionValue) {
            // Time: O(1).
            this.stationNumber = stationNumber;
            this.inspectionValue = inspectionValue;
        }
    }

    /**
     * Top-down memoized recursion for maximum inspections value.
     *
     * @param stations station array
     * @param index current index
     * @param memo cache array initialized to -1
     * @return max value from index onward
     */
    static int maxInspectionTopDown(PumpStationInspection[] stations, int index, int[] memo) {
        // Time: O(n) because each index state is solved once.

        // Base case: beyond last station.
        if (index >= stations.length) {
            return 0;
        }

        // Cache hit.
        if (memo[index] != -1) {
            return memo[index];
        }

        // Option A: inspect current station, skip adjacent.
        int include = stations[index].inspectionValue + maxInspectionTopDown(stations, index + 2, memo);

        // Option B: skip current station.
        int skip = maxInspectionTopDown(stations, index + 1, memo);

        memo[index] = (include > skip) ? include : skip;
        return memo[index];
    }

    /**
     * Bottom-up tabulation for maximum inspections value.
     *
     * @param stations station array
     * @return max obtainable inspection value
     */
    static int maxInspectionBottomUp(PumpStationInspection[] stations) {
        // Time: O(n), Space: O(n) DP array.
        if (stations.length == 0) {
            return 0;
        }
        if (stations.length == 1) {
            return stations[0].inspectionValue;
        }

        int[] bestUpTo = new int[stations.length];

        // Base rows for DP recurrence.
        bestUpTo[0] = stations[0].inspectionValue;
        bestUpTo[1] = (stations[0].inspectionValue > stations[1].inspectionValue)
                ? stations[0].inspectionValue : stations[1].inspectionValue;

        for (int i = 2; i < stations.length; i++) {
            int include = bestUpTo[i - 2] + stations[i].inspectionValue;
            int skip = bestUpTo[i - 1];
            bestUpTo[i] = (include > skip) ? include : skip;
        }

        return bestUpTo[stations.length - 1];
    }

    /**
     * Reconstructs one optimal non-adjacent inspection plan from bottom-up table.
     *
     * @param stations station array
     * @return plan string
     */
    static String reconstructInspectionPlan(PumpStationInspection[] stations) {
        // Time: O(n) build + O(n) backtrack.
        int n = stations.length;
        if (n == 0) {
            return "No inspections";
        }
        if (n == 1) {
            return "Station " + stations[0].stationNumber;
        }

        int[] bestUpTo = new int[n];
        bestUpTo[0] = stations[0].inspectionValue;
        bestUpTo[1] = (stations[0].inspectionValue > stations[1].inspectionValue)
                ? stations[0].inspectionValue : stations[1].inspectionValue;

        for (int i = 2; i < n; i++) {
            int include = bestUpTo[i - 2] + stations[i].inspectionValue;
            int skip = bestUpTo[i - 1];
            bestUpTo[i] = (include > skip) ? include : skip;
        }

        boolean[] chosen = new boolean[n];
        int i = n - 1;

        while (i >= 0) {
            if (i == 0) {
                chosen[0] = bestUpTo[0] > 0;
                break;
            }
            if (i == 1) {
                chosen[0] = stations[0].inspectionValue >= stations[1].inspectionValue;
                chosen[1] = !chosen[0];
                break;
            }

            int include = bestUpTo[i - 2] + stations[i].inspectionValue;
            int skip = bestUpTo[i - 1];

            if (include > skip) {
                chosen[i] = true;
                i = i - 2;
            } else {
                i = i - 1;
            }
        }

        StringBuilder plan = new StringBuilder();
        boolean first = true;
        for (int idx = 0; idx < n; idx++) {
            if (chosen[idx]) {
                if (!first) {
                    plan.append(" -> ");
                }
                plan.append("Station ").append(stations[idx].stationNumber);
                first = false;
            }
        }

        if (first) {
            return "No inspections";
        }
        return plan.toString();
    }

    /**
     * Builds sample stations.
     *
     * @return station array
     */
    static PumpStationInspection[] buildDemoStations() {
        // Time: O(1) fixed-size setup.
        return new PumpStationInspection[]{
                new PumpStationInspection(1, 3),
                new PumpStationInspection(2, 5),
                new PumpStationInspection(3, 4),
                new PumpStationInspection(4, 11),
                new PumpStationInspection(5, 2),
                new PumpStationInspection(6, 7),
                new PumpStationInspection(7, 9),
                new PumpStationInspection(8, 4)
        };
    }

    /**
     * Main demonstration.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== Sasol Fuel Pipeline Maintenance Schedule (Intermediate DP) ===");

        PumpStationInspection[] stations = buildDemoStations();

        int[] memo = new int[stations.length];
        for (int i = 0; i < memo.length; i++) {
            memo[i] = -1;
        }

        System.out.println("\n-- Main scenario: maximize inspections with no adjacent stations --");
        int topDown = maxInspectionTopDown(stations, 0, memo);
        int bottomUp = maxInspectionBottomUp(stations);
        System.out.println("  Top-down memoized value: " + topDown);
        System.out.println("  Bottom-up tabulated value: " + bottomUp);
        System.out.println("  Match: " + (topDown == bottomUp));
        System.out.println("  One optimal plan: " + reconstructInspectionPlan(stations));

        System.out.println("\n-- Edge case: empty station list --");
        PumpStationInspection[] empty = new PumpStationInspection[0];
        System.out.println("  Result: " + maxInspectionBottomUp(empty));

        System.out.println("\n-- Edge case: single station --");
        PumpStationInspection[] single = new PumpStationInspection[]{new PumpStationInspection(1, 6)};
        System.out.println("  Result: " + maxInspectionBottomUp(single));

        System.out.println("\n-- Edge case: duplicate station values --");
        PumpStationInspection[] duplicates = new PumpStationInspection[]{
                new PumpStationInspection(1, 5),
                new PumpStationInspection(2, 5),
                new PumpStationInspection(3, 5),
                new PumpStationInspection(4, 5)
        };
        int[] dupMemo = new int[duplicates.length];
        for (int i = 0; i < dupMemo.length; i++) {
            dupMemo[i] = -1;
        }
        System.out.println("  Top-down: " + maxInspectionTopDown(duplicates, 0, dupMemo));
        System.out.println("  Bottom-up: " + maxInspectionBottomUp(duplicates));
        System.out.println("  Plan: " + reconstructInspectionPlan(duplicates));
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - memoization: recursive results are cached per index.
 - tabulation: iterative DP array bestUpTo[] built from base cases upward.
 - overlapping subproblems: max from index k appears in many recursive branches.
 - optimal substructure: best at k = max(best at k-1, best at k-2 + value[k]).
 - bottom-up: compute from smallest prefixes to full length.
 - top-down: recurse from start and cache.
 - cache: memo[] prevents repeated work.

 Big-O:
 - maxInspectionTopDown: O(n) time, O(n) cache, O(n) recursion depth.
 - maxInspectionBottomUp: O(n) time, O(n) space.
 - reconstructInspectionPlan: O(n) time, O(n) space.

 Interview questions:
 - Why is this pipeline problem equivalent to house robber DP?
 - How do top-down and bottom-up versions encode the same recurrence?
 - Why does memoization convert exponential recursion to linear time here?
 - How do you reconstruct chosen decisions from a DP table?
 - Can space be optimized to O(1) if plan reconstruction is not required?

 Common mistake and prevention:
 - Mistake: wrong base initialization for first two DP positions.
 - Avoided here: explicit base handling for n=0, n=1, and bestUpTo[0..1].

 Comparison guidance:
 - Use DP for constrained local-choice optimization with overlapping subproblems.
 - Use greedy only if local best is provably globally best (not true here).
*/