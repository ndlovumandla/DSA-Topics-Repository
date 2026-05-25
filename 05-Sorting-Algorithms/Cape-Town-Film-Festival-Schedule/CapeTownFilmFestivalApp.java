/*
 The Cape Town International Film Festival screens 240 films across 14 venues over 11 days.
 The printed programme booklet must list every screening sorted by date first, then by venue
 name within each date, then by start time within each venue — a three-key sort. Programme
 coordinator Fatima sends developer Ravi a completely unsorted scheduling spreadsheet two days
 before print deadline; the films were added in the order each sponsor confirmed their slot.
 Ravi implements quicksort as the primary algorithm with a custom multi-key comparator that
 encodes all three sort keys into a single comparison function. He also includes bubble sort and
 merge sort for comparison, explains quicksort's pivot strategy and partition logic in detail,
 and compares the result against Java's Arrays.sort() in the study notes to confirm identical
 output with the same O(n log n) average complexity.
*/

import java.util.Arrays;
import java.util.Comparator;

/**
 * Sorts a Cape Town Film Festival screening schedule using quicksort with a multi-key comparator,
 * plus bubble sort and merge sort for comparison. All algorithms are implemented from scratch.
 * Demonstrates pivot selection, partition logic, and recursive divide-and-conquer.
 */
public class CapeTownFilmFestivalApp {

    // ──────────────────────────────────────────────────────────────────────────────
    //  DATA MODEL
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Represents one film screening slot in the festival programme.
     * Sort order: date ASC → venue ASC → startTimeMinutes ASC.
     */
    static class ScreeningSlot {
        int    festivalDay;         // 1–11
        String venueName;           // e.g. "The Labia Theatre"
        int    startTimeMinutes;    // minutes after midnight, e.g. 840 = 14:00
        String filmTitle;

        /**
         * Creates one screening slot as entered by Fatima from the sponsor's confirmation.
         *
         * @param festivalDay       day number within the festival (1-11)
         * @param venueName         venue name used as second sort key
         * @param startTimeMinutes  start time in minutes-after-midnight (third sort key)
         * @param filmTitle         title of the film being screened
         */
        ScreeningSlot(int festivalDay, String venueName, int startTimeMinutes, String filmTitle) {
            // Time: O(1) — constant field assignments.
            this.festivalDay      = festivalDay;
            this.venueName        = venueName;
            this.startTimeMinutes = startTimeMinutes;
            this.filmTitle        = filmTitle;
        }

        /** Returns a formatted display line for the printed programme. */
        String toProgrammeLine() {
            // Time: O(1) — formatting of fixed-size fields.
            int hours   = startTimeMinutes / 60;
            int minutes = startTimeMinutes % 60;
            return String.format("Day %-2d  %-28s  %02d:%02d  %s",
                    festivalDay, venueName, hours, minutes, filmTitle);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MULTI-KEY COMPARATOR
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Compares two screening slots using a three-key sort: day → venue → start time.
     * Returning a negative value means slotA comes before slotB in the programme.
     *
     * @param slotA first screening slot
     * @param slotB second screening slot
     * @return negative if A precedes B, zero if equal, positive if B precedes A
     */
    static int compareScreeningSlots(ScreeningSlot slotA, ScreeningSlot slotB) {
        // Time: O(k) where k is the number of comparison keys — here k = 3, so effectively O(1).

        // First key: festival day (integer comparison — cheaper).
        if (slotA.festivalDay != slotB.festivalDay) {
            return slotA.festivalDay - slotB.festivalDay;
        }

        // Second key: venue name (String comparison — lexicographic order).
        int venueOrder = slotA.venueName.compareTo(slotB.venueName);
        if (venueOrder != 0) {
            return venueOrder;
        }

        // Third key: start time in minutes (integer comparison).
        return slotA.startTimeMinutes - slotB.startTimeMinutes;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  UTILITY HELPERS
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Creates a deep copy of a screening schedule so each algorithm starts from the same input.
     *
     * @param source original schedule
     * @return independent copy
     */
    static ScreeningSlot[] copySchedule(ScreeningSlot[] source) {
        // Time: O(n) — copies every slot once.
        ScreeningSlot[] copy = new ScreeningSlot[source.length];
        for (int index = 0; index < source.length; index++) {
            copy[index] = new ScreeningSlot(source[index].festivalDay, source[index].venueName,
                    source[index].startTimeMinutes, source[index].filmTitle);
        }
        return copy;
    }

    /**
     * Verifies a schedule is in the required three-key order.
     *
     * @param schedule sorted screening schedule
     * @return true when every adjacent pair is correctly ordered
     */
    static boolean isScheduleSorted(ScreeningSlot[] schedule) {
        // Time: O(n) — checks every adjacent pair exactly once.
        for (int index = 0; index < schedule.length - 1; index++) {
            if (compareScreeningSlots(schedule[index], schedule[index + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Swaps two screening slots in the schedule array.
     *
     * @param schedule schedule array
     * @param posA     first index
     * @param posB     second index
     */
    static void swapSlots(ScreeningSlot[] schedule, int posA, int posB) {
        // Time: O(1) — three reference swaps.
        ScreeningSlot hold = schedule[posA];
        schedule[posA] = schedule[posB];
        schedule[posB] = hold;
    }

    /**
     * Prints a section of the formatted programme.
     *
     * @param schedule  sorted screening schedule
     * @param maxToShow maximum rows to display
     */
    static void printProgrammeSample(ScreeningSlot[] schedule, int maxToShow) {
        // Time: O(maxToShow) — bounded output.
        System.out.println("  " + "-".repeat(75));
        int limit = Math.min(maxToShow, schedule.length);
        for (int index = 0; index < limit; index++) {
            System.out.println("  " + schedule[index].toProgrammeLine());
        }
        if (schedule.length > maxToShow) {
            System.out.println("  ... (" + (schedule.length - maxToShow) + " more screenings)");
        }
        System.out.println("  " + "-".repeat(75));
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BUBBLE SORT (with multi-key comparator)
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the screening schedule using bubble sort and the three-key comparator.
     * Included for comparison — this would be impractical for a real 240-film schedule.
     *
     * @param schedule screening schedule to sort in place
     * @return comparison count
     */
    static long bubbleSortSchedule(ScreeningSlot[] schedule) {
        // Time: O(n²) — each of n passes compares up to n adjacent pairs.
        long comparisons = 0;
        int total = schedule.length;

        for (int pass = 0; pass < total - 1; pass++) {
            boolean swapped = false;
            for (int index = 0; index < total - 1 - pass; index++) {
                comparisons++;
                if (compareScreeningSlots(schedule[index], schedule[index + 1]) > 0) {
                    swapSlots(schedule, index, index + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // early exit when no swap occurred this pass
        }

        return comparisons;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MERGE SORT (with multi-key comparator)
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the screening schedule using merge sort and the three-key comparator.
     *
     * @param schedule screening schedule to sort
     * @return comparison count across all recursive calls
     */
    static long mergeSortSchedule(ScreeningSlot[] schedule) {
        // Time: O(n log n) — the recursion tree has log n levels; each level does O(n) merging.
        if (schedule.length <= 1) {
            return 0; // base case — a single-screening schedule is already sorted
        }

        int mid = schedule.length / 2;

        ScreeningSlot[] firstHalf  = new ScreeningSlot[mid];
        ScreeningSlot[] secondHalf = new ScreeningSlot[schedule.length - mid];

        System.arraycopy(schedule, 0, firstHalf, 0, mid);
        System.arraycopy(schedule, mid, secondHalf, 0, secondHalf.length);

        // Conquer: recursively sort each half.
        long comparisons = mergeSortSchedule(firstHalf) + mergeSortSchedule(secondHalf);

        // Combine: merge sorted halves back into the original array.
        comparisons += mergeScheduleHalves(schedule, firstHalf, secondHalf);
        return comparisons;
    }

    /**
     * Merges two sorted schedule halves back into the destination array.
     *
     * @param destination destination array to write into
     * @param firstHalf   sorted first half
     * @param secondHalf  sorted second half
     * @return number of comparisons made
     */
    static long mergeScheduleHalves(ScreeningSlot[] destination,
                                     ScreeningSlot[] firstHalf,
                                     ScreeningSlot[] secondHalf) {
        // Time: O(n) per call — every slot is placed in the destination exactly once.
        long comparisons = 0;
        int firstIndex = 0, secondIndex = 0, destIndex = 0;

        while (firstIndex < firstHalf.length && secondIndex < secondHalf.length) {
            comparisons++;
            if (compareScreeningSlots(firstHalf[firstIndex], secondHalf[secondIndex]) <= 0) {
                destination[destIndex++] = firstHalf[firstIndex++];
            } else {
                destination[destIndex++] = secondHalf[secondIndex++];
            }
        }

        while (firstIndex  < firstHalf.length)  destination[destIndex++] = firstHalf[firstIndex++];
        while (secondIndex < secondHalf.length) destination[destIndex++] = secondHalf[secondIndex++];

        return comparisons;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  QUICKSORT (with multi-key comparator) — PRIMARY ALGORITHM
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Public entry point: sorts the entire screening schedule using quicksort.
     * Ravi chose quicksort as the primary algorithm because the 240-slot dataset
     * is nearly random (sponsor-confirmation order), giving quicksort its best-case
     * O(n log n) average performance with no additional memory overhead.
     *
     * @param schedule screening schedule to sort in place
     * @return comparison count across all recursive calls
     */
    static long quickSortSchedule(ScreeningSlot[] schedule) {
        // Time: O(n log n) average; O(n²) worst case if pivot always chosen badly.
        // Space: O(log n) stack frames on average (recursion depth).
        return quickSortRange(schedule, 0, schedule.length - 1);
    }

    /**
     * Recursively quicksorts a sub-range of the schedule array.
     *
     * Quicksort structure:
     *   1. Choose a pivot element (median-of-three strategy used here).
     *   2. Partition: rearrange so all slots before pivot are "smaller", all after are "larger".
     *   3. Recursively sort the two sub-arrays on either side of the placed pivot.
     *
     * @param schedule  schedule array
     * @param lowIndex  inclusive start of the range to sort
     * @param highIndex inclusive end of the range to sort
     * @return comparison count for this sub-range and all recursive calls beneath it
     */
    static long quickSortRange(ScreeningSlot[] schedule, int lowIndex, int highIndex) {
        // Base case: a range of 0 or 1 element is already sorted.
        if (lowIndex >= highIndex) {
            return 0;
        }

        // Partition and get the final pivot position.
        int[] partitionResult = partition(schedule, lowIndex, highIndex);
        int pivotFinalPosition = partitionResult[0];
        long comparisons       = partitionResult[1];

        // Conquer: recursively sort the left partition (slots before the pivot).
        comparisons += quickSortRange(schedule, lowIndex, pivotFinalPosition - 1);

        // Conquer: recursively sort the right partition (slots after the pivot).
        comparisons += quickSortRange(schedule, pivotFinalPosition + 1, highIndex);

        return comparisons;
    }

    /**
     * Partitions a sub-range of the schedule around a chosen pivot using Lomuto's scheme.
     *
     * Pivot strategy — median-of-three:
     *   Compare the first, middle, and last elements of the sub-range; use the median value
     *   as the pivot. This avoids O(n²) worst case on already-sorted or reverse-sorted input,
     *   which is a realistic risk here because sponsors sometimes confirm in venue order.
     *
     * Partition logic (Lomuto):
     *   Maintain a boundary index that tracks the last element known to be <= pivot.
     *   Scan from left to right; whenever a slot is <= pivot, extend the boundary and swap.
     *   After the scan, place the pivot at the boundary position.
     *
     * @param schedule  schedule array
     * @param lowIndex  inclusive start of sub-range
     * @param highIndex inclusive end of sub-range (pivot candidate)
     * @return int[2]: [pivotFinalIndex, comparisonCount]
     */
    static int[] partition(ScreeningSlot[] schedule, int lowIndex, int highIndex) {
        // Time: O(n) — scans every element in the sub-range exactly once.
        long comparisons = 0;

        // Median-of-three pivot selection: reduces probability of O(n²) degenerate case.
        int midIndex = lowIndex + (highIndex - lowIndex) / 2;
        medianOfThreePivot(schedule, lowIndex, midIndex, highIndex);
        // After medianOfThreePivot, the median value is placed at highIndex as the pivot.

        ScreeningSlot pivot = schedule[highIndex];
        int boundaryIndex = lowIndex - 1; // points to the last element confirmed <= pivot

        for (int scanIndex = lowIndex; scanIndex < highIndex; scanIndex++) {
            comparisons++;
            // Lomuto partition: if this slot belongs on the left side, extend the boundary.
            if (compareScreeningSlots(schedule[scanIndex], pivot) <= 0) {
                boundaryIndex++;
                swapSlots(schedule, boundaryIndex, scanIndex);
            }
        }

        // Place the pivot in its correct final position, just after the boundary.
        int pivotFinalPosition = boundaryIndex + 1;
        swapSlots(schedule, pivotFinalPosition, highIndex);

        return new int[]{ pivotFinalPosition, (int) comparisons };
    }

    /**
     * Arranges three candidate pivot positions so the median value ends up at highIndex.
     * Performs at most 3 comparisons.
     *
     * @param schedule  schedule array
     * @param lowIndex  first candidate index
     * @param midIndex  middle candidate index
     * @param highIndex last candidate index — the median value will be placed here
     */
    static void medianOfThreePivot(ScreeningSlot[] schedule,
                                    int lowIndex, int midIndex, int highIndex) {
        // Time: O(1) — at most 3 comparisons and 3 swaps.

        // Sort the three candidates so schedule[lowIndex] <= schedule[midIndex] <= schedule[highIndex].
        if (compareScreeningSlots(schedule[lowIndex], schedule[midIndex]) > 0) {
            swapSlots(schedule, lowIndex, midIndex);
        }
        if (compareScreeningSlots(schedule[lowIndex], schedule[highIndex]) > 0) {
            swapSlots(schedule, lowIndex, highIndex);
        }
        if (compareScreeningSlots(schedule[midIndex], schedule[highIndex]) > 0) {
            swapSlots(schedule, midIndex, highIndex);
        }
        // After the three swaps, schedule[highIndex] holds the median — used as pivot.
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  JAVA BUILT-IN COMPARISON
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the schedule using Java's Arrays.sort() for comparison.
     * Arrays.sort() uses a dual-pivot quicksort for object arrays — same O(n log n) average.
     * The custom Comparator passed here uses the same three-key logic as our implementation.
     *
     * @param schedule schedule to sort using the built-in
     * @return the sorted copy
     */
    static ScreeningSlot[] javaBuiltInSort(ScreeningSlot[] schedule) {
        // Time: O(n log n) — Arrays.sort() guarantees this for object arrays.
        ScreeningSlot[] copy = copySchedule(schedule);
        Arrays.sort(copy, Comparator
                .comparingInt((ScreeningSlot slot) -> slot.festivalDay)
                .thenComparing(slot -> slot.venueName)
                .thenComparingInt(slot -> slot.startTimeMinutes));
        return copy;
    }

    /**
     * Verifies that two sorted schedules contain identical slot orderings.
     *
     * @param scheduleA first sorted schedule
     * @param scheduleB second sorted schedule (from a different algorithm)
     * @return true when both schedules are identical slot-for-slot
     */
    static boolean schedulesMatch(ScreeningSlot[] scheduleA, ScreeningSlot[] scheduleB) {
        // Time: O(n) — compares each slot.
        if (scheduleA.length != scheduleB.length) return false;
        for (int index = 0; index < scheduleA.length; index++) {
            if (scheduleA[index].festivalDay      != scheduleB[index].festivalDay      ||
                scheduleA[index].startTimeMinutes != scheduleB[index].startTimeMinutes ||
                !scheduleA[index].venueName.equals(scheduleB[index].venueName)         ||
                !scheduleA[index].filmTitle.equals(scheduleB[index].filmTitle)) {
                return false;
            }
        }
        return true;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  FILM & VENUE DATA
    // ──────────────────────────────────────────────────────────────────────────────

    static final String[] VENUES = {
        "The Labia Theatre",
        "Ster-Kinekor V&A",
        "Nu Metro Canal Walk",
        "CTICC Auditorium",
        "The Bioscope Oranjezicht",
        "Galileo Open Air Cinema",
        "Artscape Theatre",
        "The Old Biscuit Mill",
        "Baxter Theatre Centre",
        "Waterfront Theatre School",
        "Cinema Nouveau Cavendish",
        "The Fugard Theatre",
        "Silvertree Cinema",
        "Cape Quarter Nouveau"
    };

    static final String[] FILM_TITLES = {
        "Ubuntu Rising", "Table Mountain Blues", "Cape Storm", "The Last Protea",
        "Kalk Bay Dreams", "District Six Echo", "Zulu Skies", "Sea Point Stories",
        "Franschhoek Summer", "The Bo-Kaap Chronicles", "Stellenbosch Noir", "Karoo Dust",
        "Hout Bay Harbour", "The Slave Lodge", "De Waterkant Nights", "Signal Hill Fire",
        "Langa Morning", "Robben Island", "Noordhoek Express", "Fish Hoek Winter",
        "Chapman's Peak Drive", "Three Anchor Bay", "Muizenberg Revival", "Constantia Vines",
        "Boulders Beach", "Kirstenbosch Magic", "Newlands Spring", "Athlone Rising",
        "Mitchell's Plain Stories", "Gugulethu Voices", "Llandudno Sunset", "Clifton Four",
        "Sea Mist", "The Promenade", "Green Point Lights", "Paarden Eiland",
        "Woodstock Revival", "Observatory Midnight", "Rondebosch Common", "Claremont Mall"
    };

    // ──────────────────────────────────────────────────────────────────────────────
    //  MAIN DEMO
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Builds an unsorted festival schedule, runs all three algorithms plus Java's built-in,
     * compares comparison counts, verifies outputs match, and handles all edge cases.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Cape Town International Film Festival — Screening Schedule Sorter ===");

        // ── EDGE CASES ──
        System.out.println("\n-- Edge case: empty schedule --");
        ScreeningSlot[] emptySchedule = new ScreeningSlot[0];
        bubbleSortSchedule(emptySchedule);
        mergeSortSchedule(emptySchedule);
        quickSortSchedule(emptySchedule);
        System.out.println("  All algorithms handled empty schedule without error.");

        System.out.println("\n-- Edge case: single screening --");
        ScreeningSlot[] singleSlot = {
            new ScreeningSlot(3, "The Labia Theatre", 720, "Ubuntu Rising")
        };
        quickSortSchedule(singleSlot);
        System.out.println("  Single screening sorted: " + isScheduleSorted(singleSlot));

        System.out.println("\n-- Edge case: duplicate screenings (same day, venue, time, different films) --");
        ScreeningSlot[] duplicateSlots = {
            new ScreeningSlot(2, "Artscape Theatre", 840, "Film B"),
            new ScreeningSlot(2, "Artscape Theatre", 840, "Film A"),
            new ScreeningSlot(1, "The Labia Theatre", 600, "Film C")
        };
        quickSortSchedule(duplicateSlots);
        System.out.println("  Sorted correctly: " + isScheduleSorted(duplicateSlots));
        printProgrammeSample(duplicateSlots, 5);

        // ── BUILD 240-SLOT FESTIVAL SCHEDULE (40 films × up to 6 screenings) ──
        System.out.println("\n-- Building Fatima's unsorted 240-slot schedule --");
        int totalSlots = 240;
        ScreeningSlot[] masterSchedule = new ScreeningSlot[totalSlots];
        for (int index = 0; index < totalSlots; index++) {
            // Deliberately shuffle to simulate sponsor-confirmation entry order.
            int day        = ((index * 97)  % 11) + 1;           // 1–11
            int venueIndex = ((index * 53)  % VENUES.length);
            int timeStart  = 540 + ((index * 37) % 840);         // 09:00–23:00 range
            int timeRound  = (timeStart / 30) * 30;              // snap to 30-min slots
            int filmIndex  = index % FILM_TITLES.length;
            masterSchedule[index] = new ScreeningSlot(day, VENUES[venueIndex],
                    timeRound, FILM_TITLES[filmIndex]);
        }

        System.out.println("  First 5 slots in unsorted (sponsor-confirmation) order:");
        printProgrammeSample(masterSchedule, 5);

        // ── BUBBLE SORT ──
        System.out.println("\n-- Bubble sort O(n²) --");
        ScreeningSlot[] bubbleSchedule = copySchedule(masterSchedule);
        long bubbleStart = System.nanoTime();
        long bubbleComparisons = bubbleSortSchedule(bubbleSchedule);
        long bubbleMs = (System.nanoTime() - bubbleStart) / 1_000_000;
        System.out.println("  Comparisons: " + bubbleComparisons);
        System.out.println("  Time: " + bubbleMs + " ms  |  Sorted: " + isScheduleSorted(bubbleSchedule));

        // ── MERGE SORT ──
        System.out.println("\n-- Merge sort O(n log n) --");
        ScreeningSlot[] mergeSchedule = copySchedule(masterSchedule);
        long mergeStart = System.nanoTime();
        long mergeComparisons = mergeSortSchedule(mergeSchedule);
        long mergeMs = (System.nanoTime() - mergeStart) / 1_000_000;
        System.out.println("  Comparisons: " + mergeComparisons);
        System.out.println("  Time: " + mergeMs + " ms  |  Sorted: " + isScheduleSorted(mergeSchedule));

        // ── QUICKSORT ── (primary algorithm)
        System.out.println("\n-- Quicksort O(n log n) average — Ravi's choice --");
        ScreeningSlot[] quickSchedule = copySchedule(masterSchedule);
        long quickStart = System.nanoTime();
        long quickComparisons = quickSortSchedule(quickSchedule);
        long quickMs = (System.nanoTime() - quickStart) / 1_000_000;
        System.out.println("  Comparisons: " + quickComparisons);
        System.out.println("  Time: " + quickMs + " ms  |  Sorted: " + isScheduleSorted(quickSchedule));

        // ── JAVA BUILT-IN FOR COMPARISON ──
        System.out.println("\n-- Java Arrays.sort() (dual-pivot quicksort) --");
        long javaStart = System.nanoTime();
        ScreeningSlot[] javaSchedule = javaBuiltInSort(masterSchedule);
        long javaMs = (System.nanoTime() - javaStart) / 1_000_000;
        System.out.println("  Time: " + javaMs + " ms  |  Sorted: " + isScheduleSorted(javaSchedule));
        System.out.println("  Output matches our quicksort: " + schedulesMatch(quickSchedule, javaSchedule));

        // ── PRINT FIRST 12 LINES OF FINAL PROGRAMME ──
        System.out.println("\n-- Final printed programme (first 12 lines, quicksort output) --");
        printProgrammeSample(quickSchedule, 12);

        // ── COMPARISON SUMMARY ──
        System.out.println("\n== Complexity comparison for " + totalSlots + " screening slots ==");
        System.out.printf("  %-20s  comparisons: %,7d  (O(n²))%n",    "Bubble sort",  bubbleComparisons);
        System.out.printf("  %-20s  comparisons: %,7d  (O(n log n))%n","Merge sort",   mergeComparisons);
        System.out.printf("  %-20s  comparisons: %,7d  (O(n log n))%n","Quicksort",    quickComparisons);
        System.out.println("\nRavi's verdict: quicksort wins — O(n log n) average, sorts in-place,");
        System.out.println("and produces the same output as Java's Arrays.sort().");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Bubble sort: Compare adjacent pairs and swap them until no swaps are needed.
 - Merge sort: Recursively divide in half, sort each half, merge back together. Stable.
 - Quicksort: Choose a pivot, partition into two sides, recursively sort each side.
 - Multi-key comparator: A single compare function that chains multiple sort criteria.
 - Pivot strategy: The choice of pivot element affects whether quicksort hits O(n log n) or O(n²).
 - Partition (Lomuto): Maintain a boundary; scan left-to-right, extend boundary for elements <= pivot.
 - Median-of-three: Select median of first/mid/last elements as pivot to reduce degenerate risk.
 - O(n²): Two nested loops — grows quadratically with input size.
 - O(n log n): Divide-and-conquer; recursion tree is log n levels, each doing O(n) work.
 - comparisons: The number of times two elements are compared to determine their order.
 - swaps: The number of times two elements are exchanged in the array.
 - stable sort: Merge sort preserves the original relative order of equal-keyed slots.

 Big-O for operations implemented:
 - bubbleSortSchedule:     O(n²) worst/average; O(n) best with early exit.
 - mergeSortSchedule:      O(n log n) always; space O(n) for sub-arrays.
 - quickSortSchedule:      O(n log n) average; O(n²) worst (mitigated by median-of-three).
 - partition:              O(n) per call — scans the sub-range once.
 - medianOfThreePivot:     O(1) — three comparisons and at most three swaps.
 - mergeScheduleHalves:    O(n) per call.
 - compareScreeningSlots:  O(1) — at most 3 comparisons on fixed-size fields.
 - copySchedule:           O(n).
 - isScheduleSorted:       O(n).
 - swapSlots:              O(1).
 Space complexity:
   - Quicksort: O(log n) stack space average for recursion.
   - Merge sort: O(n) for temporary sub-arrays.
   - Bubble sort: O(1) in-place.

 Interview questions this code prepares you for:
 - What is a partition function in quicksort and why is it O(n)?
 - Why is the median-of-three pivot strategy better than always using the last element?
 - Why is merge sort preferred over quicksort when stability is required?
 - What is the worst-case time complexity of quicksort and when does it occur?
 - How would you implement a multi-key sort without using multiple separate sort passes?

 Most common mistake and how this code avoids it:
 - Mistake: Always using the last element as the pivot, causing O(n²) on sorted or reverse-sorted input.
 - Avoided: medianOfThreePivot() selects the median of three candidates, distributing the split more evenly.

 When to use this vs the common alternative:
 - Use quicksort for general in-place sorting of random data — O(n log n) average, O(log n) stack space.
 - Use merge sort when stability is needed (equal elements must preserve insertion order).
 - Use insertion sort for very small arrays (< 16 elements) — many production quicksorts switch to it.
 - Use Java's Arrays.sort() in production; it is a dual-pivot quicksort tuned by JVM engineers
   and produces identical output to this implementation on the same comparator.
*/
