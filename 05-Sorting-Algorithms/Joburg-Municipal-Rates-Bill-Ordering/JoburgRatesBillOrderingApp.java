/*
 The City of Johannesburg prints 1.4 million rates bills every month. Before the print queue can
 start at 4 AM, bills must be sorted by postal code so the post office can bundle them for its
 bulk-mail discount. IT manager Dineo's team has been using bubble sort since 2003, and for 1.4
 million records that now takes 3.5 hours — regularly missing the print-queue deadline. Developer
 Siphamandla is brought in to fix the problem. He implements bubble sort, selection sort, and merge
 sort on the same billing dataset and benchmarks all three so the team can see exactly why merge sort
 cuts the overnight run from 3.5 hours to 11 minutes. The key insight is that bubble sort and
 selection sort both perform O(n²) comparisons, while merge sort's divide-and-conquer strategy
 produces O(n log n) comparisons regardless of the input order.
*/

/**
 * Benchmarks three sorting algorithms on a simulated Joburg billing dataset sorted by postal code.
 * Demonstrates bubble sort, selection sort, and merge sort — each implemented from scratch.
 */
public class JoburgRatesBillOrderingApp {

    /**
     * Represents one rates bill waiting to be sorted before printing.
     * The postal code is the sort key; the account number identifies the ratepayer.
     */
    static class RatesBill {
        int postalCode;
        String accountNumber;

        /**
         * Creates one rates bill with its postal code and account reference.
         *
         * @param postalCode   delivery postal code — the sort key used for bundle ordering
         * @param accountNumber unique ratepayer account reference
         */
        RatesBill(int postalCode, String accountNumber) {
            // Time: O(1) — constant field assignments.
            this.postalCode    = postalCode;
            this.accountNumber = accountNumber;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  UTILITY HELPERS
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Creates a deep copy of a bill array so each algorithm starts from the same input.
     *
     * @param source original bill array
     * @return independent copy
     */
    static RatesBill[] copyBillingRun(RatesBill[] source) {
        // Time: O(n) — visits every element once to copy it.
        RatesBill[] copy = new RatesBill[source.length];
        for (int index = 0; index < source.length; index++) {
            copy[index] = new RatesBill(source[index].postalCode, source[index].accountNumber);
        }
        return copy;
    }

    /**
     * Swaps two bills inside an array.
     *
     * @param bills billing run array
     * @param posA  first index
     * @param posB  second index
     */
    static void swapBills(RatesBill[] bills, int posA, int posB) {
        // Time: O(1) — three pointer updates only.
        RatesBill hold = bills[posA];
        bills[posA] = bills[posB];
        bills[posB] = hold;
    }

    /**
     * Checks that a billing run is sorted in ascending postal code order.
     *
     * @param bills sorted billing run
     * @return true when every adjacent pair is in order
     */
    static boolean isSortedByPostalCode(RatesBill[] bills) {
        // Time: O(n) — checks every adjacent pair once.
        for (int index = 0; index < bills.length - 1; index++) {
            if (bills[index].postalCode > bills[index + 1].postalCode) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prints up to a given number of bills for visual confirmation.
     *
     * @param bills     billing run
     * @param maxToShow maximum rows to display
     */
    static void printBillingRunSample(RatesBill[] bills, int maxToShow) {
        // Time: O(maxToShow) — bounded print, not O(n).
        int limit = Math.min(maxToShow, bills.length);
        for (int index = 0; index < limit; index++) {
            System.out.println("  postal " + bills[index].postalCode
                    + "  account " + bills[index].accountNumber);
        }
        if (bills.length > maxToShow) {
            System.out.println("  ... (" + (bills.length - maxToShow) + " more bills)");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BUBBLE SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts a billing run by postal code using bubble sort.
     * Adjacent bills are compared and swapped until the entire run is in order.
     *
     * @param bills billing run to sort in place
     * @return number of swaps performed
     */
    static long bubbleSortBillingRun(RatesBill[] bills) {
        // Time: O(n²) — each of the n passes compares up to n elements; worst case n*(n-1)/2 swaps.
        long swapCount = 0;
        int billCount = bills.length;

        for (int outerPass = 0; outerPass < billCount - 1; outerPass++) {
            // Bubble concept: each outer pass pushes the current largest value to the back.
            boolean swappedThisPass = false;

            for (int compareIndex = 0; compareIndex < billCount - 1 - outerPass; compareIndex++) {
                // Comparison: if the left bill has a higher postal code, swap them.
                if (bills[compareIndex].postalCode > bills[compareIndex + 1].postalCode) {
                    swapBills(bills, compareIndex, compareIndex + 1);
                    swapCount++;
                    swappedThisPass = true;
                }
            }

            // Early-exit optimisation: if no swap happened, the array is already sorted.
            if (!swappedThisPass) {
                break;
            }
        }

        return swapCount;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  SELECTION SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts a billing run by postal code using selection sort.
     * Each pass scans the unsorted region to find the minimum, then swaps it to the front.
     *
     * @param bills billing run to sort in place
     * @return number of swaps performed
     */
    static long selectionSortBillingRun(RatesBill[] bills) {
        // Time: O(n²) — n passes each scanning an O(n) unsorted region.
        long swapCount = 0;
        int billCount = bills.length;

        for (int sortedBoundary = 0; sortedBoundary < billCount - 1; sortedBoundary++) {
            // Selection concept: find the index of the smallest remaining postal code.
            int lowestCodeIndex = sortedBoundary;

            for (int scanIndex = sortedBoundary + 1; scanIndex < billCount; scanIndex++) {
                // Comparison: look for a bill with a smaller postal code.
                if (bills[scanIndex].postalCode < bills[lowestCodeIndex].postalCode) {
                    lowestCodeIndex = scanIndex;
                }
            }

            // Swap only if the minimum is not already in position.
            if (lowestCodeIndex != sortedBoundary) {
                swapBills(bills, sortedBoundary, lowestCodeIndex);
                swapCount++;
            }
        }

        return swapCount;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MERGE SORT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts a billing run by postal code using merge sort (divide and conquer).
     * Divides the run in half recursively, then merges the sorted halves back together.
     *
     * @param bills billing run to sort
     */
    static void mergeSortBillingRun(RatesBill[] bills) {
        // Time: O(n log n) — the tree has log n levels, each level merges n elements total.
        if (bills.length <= 1) {
            return; // base case: a single-element array is already sorted
        }

        int midPoint = bills.length / 2;

        // Divide: split into two halves.
        RatesBill[] leftHalf  = new RatesBill[midPoint];
        RatesBill[] rightHalf = new RatesBill[bills.length - midPoint];

        System.arraycopy(bills, 0, leftHalf, 0, midPoint);
        System.arraycopy(bills, midPoint, rightHalf, 0, rightHalf.length);

        // Conquer: recursively sort each half.
        mergeSortBillingRun(leftHalf);
        mergeSortBillingRun(rightHalf);

        // Combine: merge the two sorted halves back into the original array.
        mergeSortedHalves(bills, leftHalf, rightHalf);
    }

    /**
     * Merges two sorted billing-run halves back into the destination array.
     *
     * @param destination array to write into
     * @param leftHalf    sorted left portion
     * @param rightHalf   sorted right portion
     */
    static void mergeSortedHalves(RatesBill[] destination, RatesBill[] leftHalf, RatesBill[] rightHalf) {
        // Time: O(n) — each element from both halves is placed exactly once.
        int leftIndex = 0, rightIndex = 0, destIndex = 0;

        // Merge: always take the smaller postal code from either half.
        while (leftIndex < leftHalf.length && rightIndex < rightHalf.length) {
            if (leftHalf[leftIndex].postalCode <= rightHalf[rightIndex].postalCode) {
                destination[destIndex++] = leftHalf[leftIndex++];
            } else {
                destination[destIndex++] = rightHalf[rightIndex++];
            }
        }

        // Copy any remaining elements from the half that still has items.
        while (leftIndex  < leftHalf.length)  destination[destIndex++] = leftHalf[leftIndex++];
        while (rightIndex < rightHalf.length) destination[destIndex++] = rightHalf[rightIndex++];
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MAIN DEMO
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Builds a simulated billing run, runs all three sorts on identical copies,
     * and prints comparison metrics to illustrate why merge sort is the right choice.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Joburg Municipal Rates Bill Ordering System ===");

        // Edge cases: empty and single-element arrays.
        System.out.println("\n-- Edge case: empty billing run --");
        RatesBill[] emptyRun = new RatesBill[0];
        bubbleSortBillingRun(emptyRun);
        selectionSortBillingRun(emptyRun);
        mergeSortBillingRun(emptyRun);
        System.out.println("  All sorts handled empty array correctly.");

        System.out.println("\n-- Edge case: single-bill billing run --");
        RatesBill[] singleBill = { new RatesBill(2193, "ACC-0001") };
        bubbleSortBillingRun(singleBill);
        selectionSortBillingRun(singleBill);
        mergeSortBillingRun(singleBill);
        System.out.println("  All sorts handled single-element array correctly.");

        // Build a realistic simulated billing run of 5 000 bills.
        int billingRunSize = 5000;
        RatesBill[] masterRun = new RatesBill[billingRunSize];

        // Use a fixed seed-like pattern to produce a deterministic unsorted dataset.
        // Postal codes range from 0001 to 9999, deliberately shuffled.
        for (int index = 0; index < billingRunSize; index++) {
            int shuffledPostalCode = ((index * 1301) % 9999) + 1;
            masterRun[index] = new RatesBill(shuffledPostalCode, "ACC-" + String.format("%05d", index));
        }

        // Duplicate edge case: force two bills to share the same postal code.
        masterRun[100].postalCode = masterRun[200].postalCode;

        System.out.println("\n-- Unsorted billing run sample (first 5 bills) --");
        printBillingRunSample(masterRun, 5);

        // ── BUBBLE SORT ──
        System.out.println("\n-- Bubble sort O(n²) --");
        RatesBill[] bubbleRun = copyBillingRun(masterRun);
        long bubbleStart = System.nanoTime();
        long bubbleSwaps = bubbleSortBillingRun(bubbleRun);
        long bubbleMs = (System.nanoTime() - bubbleStart) / 1_000_000;
        System.out.println("  Swaps performed: " + bubbleSwaps);
        System.out.println("  Time taken: " + bubbleMs + " ms");
        System.out.println("  Sorted correctly: " + isSortedByPostalCode(bubbleRun));
        System.out.println("  First 5 bills after sort:");
        printBillingRunSample(bubbleRun, 5);

        // ── SELECTION SORT ──
        System.out.println("\n-- Selection sort O(n²) --");
        RatesBill[] selectionRun = copyBillingRun(masterRun);
        long selectionStart = System.nanoTime();
        long selectionSwaps = selectionSortBillingRun(selectionRun);
        long selectionMs = (System.nanoTime() - selectionStart) / 1_000_000;
        System.out.println("  Swaps performed: " + selectionSwaps);
        System.out.println("  Time taken: " + selectionMs + " ms");
        System.out.println("  Sorted correctly: " + isSortedByPostalCode(selectionRun));
        System.out.println("  First 5 bills after sort:");
        printBillingRunSample(selectionRun, 5);

        // ── MERGE SORT ──
        System.out.println("\n-- Merge sort O(n log n) --");
        RatesBill[] mergeRun = copyBillingRun(masterRun);
        long mergeStart = System.nanoTime();
        mergeSortBillingRun(mergeRun);
        long mergeMs = (System.nanoTime() - mergeStart) / 1_000_000;
        System.out.println("  Swaps performed: n/a (merge does not swap in place)");
        System.out.println("  Time taken: " + mergeMs + " ms");
        System.out.println("  Sorted correctly: " + isSortedByPostalCode(mergeRun));
        System.out.println("  First 5 bills after sort:");
        printBillingRunSample(mergeRun, 5);

        // ── COMPARISON SUMMARY ──
        System.out.println("\n== Complexity comparison for " + billingRunSize + " bills ==");
        System.out.println("  Bubble sort:    O(n²)      ~" + (long)billingRunSize * billingRunSize + " ops");
        System.out.println("  Selection sort: O(n²)      ~" + (long)billingRunSize * billingRunSize + " ops");
        System.out.println("  Merge sort:     O(n log n) ~" + (long)(billingRunSize * Math.round(Math.log(billingRunSize) / Math.log(2))) + " ops");
        System.out.println("\nFor 1.4 million bills: bubble takes ~3.5 hrs; merge sort takes ~11 min.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Bubble sort: Compare adjacent pairs and swap them repeatedly until no swaps are needed.
 - Selection sort: Find the smallest remaining element and swap it to the front on each pass.
 - Merge sort: Divide the array in half recursively, then merge the sorted halves back together.
 - O(n²): Each pass examines up to n elements, and there are up to n passes, giving n×n total ops.
 - O(n log n): The divide-and-conquer recursion tree has log n levels, each doing O(n) work.
 - comparisons: The number of times two elements are compared to decide their order.
 - swaps: The number of times two elements are exchanged during sorting.

 Big-O for operations implemented:
 - bubbleSortBillingRun: O(n²) worst/average; O(n) best with early exit when already sorted.
 - selectionSortBillingRun: O(n²) always; selection sort cannot exit early.
 - mergeSortBillingRun: O(n log n) always; divide-and-conquer guarantees this.
 - mergeSortedHalves: O(n) per call; called log n times across all recursion levels.
 - copyBillingRun: O(n) because it copies every element once.
 - isSortedByPostalCode: O(n) because it checks every adjacent pair.
 - swapBills: O(1) because it swaps three references only.
 Space complexity: O(n) for merge sort due to the temporary left/right arrays.

 Interview questions this code prepares you for:
 - Why is bubble sort O(n²) and merge sort O(n log n)?
 - When would bubble sort outperform merge sort?
 - What is the space complexity of merge sort?
 - How does the early-exit optimisation improve bubble sort in the best case?
 - Why does selection sort always make O(n²) comparisons even when the array is sorted?

 Most common mistake and how this code avoids it:
 - Mistake: Forgetting the base case in mergeSortBillingRun, causing infinite recursion.
 - Avoided: The method returns immediately when the array length is 1 or less.

 When to use this vs the common alternative:
 - Use merge sort for large datasets where O(n log n) is required and memory is available.
 - Use bubble or selection sort only for tiny datasets or for teaching purposes.
 - Use quicksort when average-case O(n log n) with no extra space is the goal.
*/
