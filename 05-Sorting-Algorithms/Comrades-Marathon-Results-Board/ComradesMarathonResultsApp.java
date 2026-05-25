/*
 The Comrades Marathon between Durban and Pietermaritzburg is the world's largest ultra-marathon,
 with up to 25 000 finishers crossing the line over a 12-hour window. The results board at the
 finish line must display every finisher's name and chip time in ascending order, updated live as
 runners complete the race. In the early hours when only a handful have finished, the list is nearly
 sorted already — each new arrival just needs to slot into the right position. Insertion sort is
 perfect for this pattern. But from midday onwards, thousands of runners finish in a rush and the
 board update falls seconds behind. Timing system developer Yusuf implements all four classic O(n²)
 and O(n log n) algorithms on the same finisher dataset, measures performance as the list grows from
 10 to 10 000 entries, and proves in code comments exactly why merge sort is the algorithm of
 choice once more than a few hundred runners have crossed the line.
*/

/**
 * Demonstrates four sorting algorithms on a Comrades Marathon finisher list.
 * Implements bubble sort, insertion sort, selection sort, and merge sort from scratch,
 * tracking comparisons for each to show real performance differences as dataset grows.
 */
public class ComradesMarathonResultsApp {

    // ──────────────────────────────────────────────────────────────────────────────
    //  DATA MODEL
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Represents a single runner who has crossed the finish line.
     * Finish time in seconds is the sort key for the results board.
     */
    static class MarathonFinisher {
        String runnerName;
        int    finishTimeSeconds;   // sort key: lower = better placing

        /**
         * Creates one finisher record as a runner crosses the line.
         *
         * @param runnerName         runner's full name as captured by chip timing
         * @param finishTimeSeconds  chip time from 05:30 start in seconds
         */
        MarathonFinisher(String runnerName, int finishTimeSeconds) {
            // Time: O(1) — constant field assignments.
            this.runnerName         = runnerName;
            this.finishTimeSeconds  = finishTimeSeconds;
        }

        /** Converts seconds to a human-readable h:mm:ss string for the board display. */
        String formattedTime() {
            // Time: O(1) — arithmetic on fixed-size fields.
            int hours   = finishTimeSeconds / 3600;
            int minutes = (finishTimeSeconds % 3600) / 60;
            int seconds = finishTimeSeconds % 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  UTILITY HELPERS
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Creates a deep copy of a finisher array so every algorithm sorts an identical input.
     *
     * @param source original finisher list
     * @return independent copy
     */
    static MarathonFinisher[] copyFinisherList(MarathonFinisher[] source) {
        // Time: O(n) — copies every element exactly once.
        MarathonFinisher[] copy = new MarathonFinisher[source.length];
        for (int index = 0; index < source.length; index++) {
            copy[index] = new MarathonFinisher(source[index].runnerName,
                                               source[index].finishTimeSeconds);
        }
        return copy;
    }

    /**
     * Verifies the results board is in ascending finish-time order.
     *
     * @param finishers sorted results board
     * @return true when every adjacent pair is in order
     */
    static boolean isResultsBoardSorted(MarathonFinisher[] finishers) {
        // Time: O(n) — checks every adjacent pair.
        for (int index = 0; index < finishers.length - 1; index++) {
            if (finishers[index].finishTimeSeconds > finishers[index + 1].finishTimeSeconds) {
                return false;
            }
        }
        return true;
    }

    /**
     * Swaps two finishers on the results board.
     *
     * @param board results board array
     * @param posA  first position
     * @param posB  second position
     */
    static void swapFinishers(MarathonFinisher[] board, int posA, int posB) {
        // Time: O(1) — three pointer assignments.
        MarathonFinisher hold = board[posA];
        board[posA] = board[posB];
        board[posB] = hold;
    }

    /**
     * Prints the top section of the results board for visual verification.
     *
     * @param board     sorted finisher list
     * @param maxToShow maximum rows to display
     */
    static void printResultsBoardSample(MarathonFinisher[] board, int maxToShow) {
        // Time: O(maxToShow) — bounded print.
        System.out.printf("  %-5s %-25s %s%n", "Pos", "Name", "Time");
        System.out.println("  " + "-".repeat(42));
        int limit = Math.min(maxToShow, board.length);
        for (int index = 0; index < limit; index++) {
            System.out.printf("  %-5d %-25s %s%n",
                    index + 1, board[index].runnerName, board[index].formattedTime());
        }
        if (board.length > maxToShow) {
            System.out.println("  ... (" + (board.length - maxToShow) + " more finishers)");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BUBBLE SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the results board by finish time using bubble sort.
     * Adjacent finishers are compared and swapped until every runner is in order.
     *
     * @param board results board to sort in place
     * @return number of comparisons made
     */
    static long bubbleSortResultsBoard(MarathonFinisher[] board) {
        // Time: O(n²) worst/average — n passes each examining up to n elements.
        long comparisonCount = 0;
        int total = board.length;

        for (int outerPass = 0; outerPass < total - 1; outerPass++) {
            // Bubble: the largest remaining value bubbles to the back each pass.
            boolean swappedThisPass = false;

            for (int index = 0; index < total - 1 - outerPass; index++) {
                comparisonCount++;
                // Compare adjacent finishers — if left is slower, swap.
                if (board[index].finishTimeSeconds > board[index + 1].finishTimeSeconds) {
                    swapFinishers(board, index, index + 1);
                    swappedThisPass = true;
                }
            }

            // Early-exit: if no swap occurred the board is already sorted.
            if (!swappedThisPass) {
                break;
            }
        }

        return comparisonCount;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  INSERTION SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the results board by finish time using insertion sort.
     * Works like adding new finishers one at a time to an already-ordered list,
     * sliding each newcomer leftward until they reach the right position.
     *
     * @param board results board to sort in place
     * @return number of comparisons made
     */
    static long insertionSortResultsBoard(MarathonFinisher[] board) {
        // Time: O(n²) worst; O(n) best when already sorted — ideal for near-sorted live updates.
        long comparisonCount = 0;
        int total = board.length;

        for (int nextRunner = 1; nextRunner < total; nextRunner++) {
            // Insertion concept: hold the new finisher out of the queue.
            MarathonFinisher arrivingRunner = board[nextRunner];
            int insertPosition = nextRunner - 1;

            // Shift all slower finishers one position to the right.
            while (insertPosition >= 0
                    && board[insertPosition].finishTimeSeconds > arrivingRunner.finishTimeSeconds) {
                comparisonCount++;
                // Shift: moves each displaced runner one slot to the right.
                board[insertPosition + 1] = board[insertPosition];
                insertPosition--;
            }

            if (insertPosition >= 0) {
                comparisonCount++; // the comparison that ended the while-loop
            }

            // Place the arriving runner in their correct position on the board.
            board[insertPosition + 1] = arrivingRunner;
        }

        return comparisonCount;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  SELECTION SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the results board by finish time using selection sort.
     * Each pass scans the unsorted tail for the fastest remaining runner and moves them forward.
     *
     * @param board results board to sort in place
     * @return number of comparisons made
     */
    static long selectionSortResultsBoard(MarathonFinisher[] board) {
        // Time: O(n²) — always makes n*(n-1)/2 comparisons regardless of input order.
        long comparisonCount = 0;
        int total = board.length;

        for (int sortedEnd = 0; sortedEnd < total - 1; sortedEnd++) {
            // Selection: find the index of the fastest remaining runner.
            int fastestRemaining = sortedEnd;

            for (int scanIndex = sortedEnd + 1; scanIndex < total; scanIndex++) {
                comparisonCount++;
                if (board[scanIndex].finishTimeSeconds < board[fastestRemaining].finishTimeSeconds) {
                    fastestRemaining = scanIndex;
                }
            }

            // Swap only if the fastest runner is not already in position.
            if (fastestRemaining != sortedEnd) {
                swapFinishers(board, sortedEnd, fastestRemaining);
            }
        }

        return comparisonCount;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MERGE SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the results board by finish time using merge sort (divide and conquer).
     *
     * @param board results board to sort
     * @return number of comparisons made across all merge calls
     */
    static long mergeSortResultsBoard(MarathonFinisher[] board) {
        // Time: O(n log n) — recursion tree is log n levels deep; each level merges n elements.
        if (board.length <= 1) {
            return 0; // base case — single-runner board is already sorted
        }

        int midPoint = board.length / 2;

        // Divide: allocate two halves.
        MarathonFinisher[] earlyGroup = new MarathonFinisher[midPoint];
        MarathonFinisher[] lateGroup  = new MarathonFinisher[board.length - midPoint];

        System.arraycopy(board, 0, earlyGroup, 0, midPoint);
        System.arraycopy(board, midPoint, lateGroup, 0, lateGroup.length);

        // Conquer: sort each group recursively.
        long comparisons = mergeSortResultsBoard(earlyGroup)
                         + mergeSortResultsBoard(lateGroup);

        // Combine: merge the two sorted groups.
        comparisons += mergeFinisherGroups(board, earlyGroup, lateGroup);
        return comparisons;
    }

    /**
     * Merges two sorted finisher groups back into the destination array.
     *
     * @param destination array to write the merged result into
     * @param earlyGroup  sorted first half
     * @param lateGroup   sorted second half
     * @return number of element comparisons performed in this merge
     */
    static long mergeFinisherGroups(MarathonFinisher[] destination,
                                    MarathonFinisher[] earlyGroup,
                                    MarathonFinisher[] lateGroup) {
        // Time: O(n) per call — each finisher is placed into the destination exactly once.
        long comparisons = 0;
        int earlyIndex = 0, lateIndex = 0, destIndex = 0;

        // Merge: always take the faster finish time from either group.
        while (earlyIndex < earlyGroup.length && lateIndex < lateGroup.length) {
            comparisons++;
            // Stable merge: <= means tied times preserve the original ordering (ties stay together).
            if (earlyGroup[earlyIndex].finishTimeSeconds <= lateGroup[lateIndex].finishTimeSeconds) {
                destination[destIndex++] = earlyGroup[earlyIndex++];
            } else {
                destination[destIndex++] = lateGroup[lateIndex++];
            }
        }

        // Flush any remaining runners from the group that still has entries.
        while (earlyIndex < earlyGroup.length) destination[destIndex++] = earlyGroup[earlyIndex++];
        while (lateIndex  < lateGroup.length)  destination[destIndex++] = lateGroup[lateIndex++];

        return comparisons;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MAIN DEMO
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Runs a story-driven demonstration comparing all four sorting algorithms
     * on finisher lists of increasing size — mirroring how the Comrades board
     * grows from a handful of early finishers to thousands in the midday rush.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Comrades Marathon Results Board — Sorting Algorithm Benchmark ===");

        // ── EDGE CASES ──
        System.out.println("\n-- Edge case: empty finisher list --");
        MarathonFinisher[] emptyBoard = new MarathonFinisher[0];
        bubbleSortResultsBoard(emptyBoard);
        insertionSortResultsBoard(emptyBoard);
        selectionSortResultsBoard(emptyBoard);
        mergeSortResultsBoard(emptyBoard);
        System.out.println("  All sorts handled empty list without error.");

        System.out.println("\n-- Edge case: single finisher --");
        MarathonFinisher[] soloBoard = { new MarathonFinisher("Lungelo Dlamini", 27000) };
        mergeSortResultsBoard(soloBoard);
        System.out.println("  Single finisher: " + soloBoard[0].runnerName
                + " " + soloBoard[0].formattedTime());

        System.out.println("\n-- Edge case: tied finish times (two runners cross together) --");
        MarathonFinisher[] tiedBoard = {
            new MarathonFinisher("Thabo Nkosi",  31500),
            new MarathonFinisher("Amahle Zulu",  31500),
            new MarathonFinisher("Sipho Mokoena", 31200)
        };
        mergeSortResultsBoard(tiedBoard);
        printResultsBoardSample(tiedBoard, 3);
        System.out.println("  Tied times preserved in insertion order (stable merge). Sorted: "
                + isResultsBoardSorted(tiedBoard));

        // ── BUILD FINISHER DATASETS OF GROWING SIZE ──
        int[] sizes = {10, 100, 1000, 5000};

        for (int datasetSize : sizes) {
            System.out.println("\n══════════════════════════════════════════════════");
            System.out.println(" Dataset size: " + datasetSize + " finishers");
            System.out.println("══════════════════════════════════════════════════");

            // Generate a finisher list that mimics randomly ordered chip-time arrivals.
            MarathonFinisher[] master = new MarathonFinisher[datasetSize];
            for (int index = 0; index < datasetSize; index++) {
                // Chip times spread from 5h 30m (19 800s) to 12h 00m (43 200s).
                int chipTime = 19800 + ((index * 1301) % 23401);
                master[index] = new MarathonFinisher("Runner-" + String.format("%05d", index), chipTime);
            }

            // Force one duplicate to test tie handling.
            if (datasetSize > 1) {
                master[datasetSize / 2].finishTimeSeconds = master[datasetSize / 2 + 1].finishTimeSeconds;
            }

            // Sort with each algorithm and record comparisons.
            MarathonFinisher[] bubbleBoard    = copyFinisherList(master);
            MarathonFinisher[] insertionBoard = copyFinisherList(master);
            MarathonFinisher[] selectionBoard = copyFinisherList(master);
            MarathonFinisher[] mergeBoard     = copyFinisherList(master);

            long bubbleComparisons    = bubbleSortResultsBoard(bubbleBoard);
            long insertionComparisons = insertionSortResultsBoard(insertionBoard);
            long selectionComparisons = selectionSortResultsBoard(selectionBoard);
            long mergeComparisons     = mergeSortResultsBoard(mergeBoard);

            System.out.printf("  %-20s comparisons: %,10d  sorted: %b%n",
                    "Bubble sort",    bubbleComparisons,    isResultsBoardSorted(bubbleBoard));
            System.out.printf("  %-20s comparisons: %,10d  sorted: %b%n",
                    "Insertion sort", insertionComparisons, isResultsBoardSorted(insertionBoard));
            System.out.printf("  %-20s comparisons: %,10d  sorted: %b%n",
                    "Selection sort", selectionComparisons, isResultsBoardSorted(selectionBoard));
            System.out.printf("  %-20s comparisons: %,10d  sorted: %b%n",
                    "Merge sort",     mergeComparisons,     isResultsBoardSorted(mergeBoard));

            if (datasetSize <= 10) {
                System.out.println("\n  Results board top 5:");
                printResultsBoardSample(mergeBoard, 5);
            }
        }

        System.out.println("\n=== Yusuf's conclusion: Use insertion sort for live updates (nearly sorted).");
        System.out.println("    Switch to merge sort once 500+ runners are on the board. ===");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Bubble sort: Compare adjacent pairs and swap them repeatedly until no swaps are needed.
 - Insertion sort: Build the sorted list one item at a time by shifting each newcomer left.
 - Selection sort: Find the minimum remaining element and swap it to the front on each pass.
 - Merge sort: Divide the list in half, sort each half, then merge the sorted halves together.
 - O(n²): Two nested loops — comparisons grow as the square of the list size.
 - O(n log n): Divide-and-conquer; recursion tree is log₂(n) levels, each doing O(n) work.
 - comparisons: Every time two elements are tested to decide their relative order.
 - swaps: Every time two elements are exchanged during sorting.
 - stable sort: A sort that preserves the relative order of equal-valued elements.

 Big-O for operations implemented:
 - bubbleSortResultsBoard:    O(n²) worst/average; O(n) best (already sorted with early exit).
 - insertionSortResultsBoard: O(n²) worst; O(n) best when input is nearly sorted.
 - selectionSortResultsBoard: O(n²) always — cannot exit early.
 - mergeSortResultsBoard:     O(n log n) always — guaranteed regardless of input order.
 - mergeFinisherGroups:       O(n) per call.
 - copyFinisherList:          O(n) per call.
 - isResultsBoardSorted:      O(n) per call.
 - swapFinishers:             O(1) per call.
 Space: O(n) for merge sort due to temporary sub-arrays; O(1) for the others.

 Interview questions this code prepares you for:
 - Why is insertion sort fast on nearly-sorted data?
 - Why is merge sort always O(n log n) but insertion sort is sometimes O(n)?
 - What does "stable sort" mean and why does it matter for a results board with tied times?
 - What is the space complexity of merge sort and why?
 - Why does selection sort always make O(n²) comparisons even on a sorted list?

 Most common mistake and how this code avoids it:
 - Mistake: Using System.arraycopy without checking bounds, causing ArrayIndexOutOfBoundsException.
 - Avoided: midPoint and lateGroup size are calculated so both halves together equal board.length.

 When to use this vs the common alternative:
 - Use insertion sort when the dataset is small or nearly sorted (live updates with few items).
 - Use merge sort when the dataset is large and O(n log n) is required.
 - Avoid bubble and selection sort in production — they are useful only for teaching.
 - Use quicksort when average-case O(n log n) with O(log n) stack space is preferred.
*/
