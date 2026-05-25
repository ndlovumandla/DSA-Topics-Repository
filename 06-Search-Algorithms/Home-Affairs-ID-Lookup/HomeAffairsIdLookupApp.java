/*
 The Department of Home Affairs maintains a database of 60 million South African ID numbers.
 Banks, employers, and government departments rely on this system to verify identities in real
 time — the requirement is a response in under one second. In the 1990s the verification system
 used a linear scan through an unsorted flat file; on a 60-million record database this meant up
 to 60 million comparisons per lookup, and by 2000 banks were waiting 45 seconds per query.
 Developer Monde was brought in to fix the problem. She first sorts the ID database using merge
 sort, then replaces the linear scan with binary search: every lookup now takes at most 26
 comparisons regardless of how large the database grows (log₂(60 000 000) ≈ 25.8). She
 implements both algorithms side-by-side, times them on growing dataset sizes, and explains in
 comments exactly what "at most 26 comparisons" means compared to the old worst case of 60 million.
*/

/**
 * Demonstrates linear search vs binary search on a simulated Home Affairs ID verification system.
 * Monde's solution: sort the database once with merge sort, then use binary search for every lookup.
 */
public class HomeAffairsIdLookupApp {

    // ──────────────────────────────────────────────────────────────────────────────
    //  DATA MODEL
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Represents one South African ID record in the Home Affairs database.
     * The ID number is a 13-digit string (YYMMDD SSSS C A Z format); it is the search key.
     */
    static class CitizenIdRecord {
        String idNumber;      // 13-character SA ID number — the search key
        String fullName;
        String dateOfBirth;
        boolean isAlive;      // false = deceased — important for fraud detection

        /**
         * Creates one citizen record as it would exist in the Home Affairs database.
         *
         * @param idNumber    13-digit SA ID string — sort and search key
         * @param fullName    citizen's full name as registered at birth
         * @param dateOfBirth ISO date string YYYY-MM-DD
         * @param isAlive     false for deceased citizens (ghost-identity fraud flag)
         */
        CitizenIdRecord(String idNumber, String fullName, String dateOfBirth, boolean isAlive) {
            // Time: O(1) — four field assignments.
            this.idNumber    = idNumber;
            this.fullName    = fullName;
            this.dateOfBirth = dateOfBirth;
            this.isAlive     = isAlive;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  VERIFICATION RESULT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Holds the outcome of one ID verification request.
     * Banks and employers read the verificationStatus field to decide whether to proceed.
     */
    static class VerificationResult {
        CitizenIdRecord matchedRecord;   // null if not found
        int             foundAtIndex;    // -1 if not found
        long            comparisons;
        String          verificationStatus;

        VerificationResult(CitizenIdRecord matchedRecord, int foundAtIndex,
                           long comparisons, String verificationStatus) {
            this.matchedRecord      = matchedRecord;
            this.foundAtIndex       = foundAtIndex;
            this.comparisons        = comparisons;
            this.verificationStatus = verificationStatus;
        }

        /** Returns true when a live citizen record was found. */
        boolean isVerified() { return matchedRecord != null && matchedRecord.isAlive; }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MERGE SORT — used once to sort the database before lookups begin
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Sorts the ID database in ascending ID-number order using merge sort.
     * This one-time O(n log n) sort unlocks O(log n) binary search for all future lookups.
     *
     * @param records array of citizen records to sort in place
     */
    static void mergeSortIdDatabase(CitizenIdRecord[] records) {
        // Time: O(n log n) — divide-and-conquer; log n recursion levels, each doing O(n) work.
        // This cost is paid once at startup; every subsequent lookup is O(log n).
        if (records.length <= 1) return;

        int midPoint = records.length / 2;
        CitizenIdRecord[] leftBlock  = new CitizenIdRecord[midPoint];
        CitizenIdRecord[] rightBlock = new CitizenIdRecord[records.length - midPoint];

        System.arraycopy(records, 0, leftBlock, 0, midPoint);
        System.arraycopy(records, midPoint, rightBlock, 0, rightBlock.length);

        // Recursively sort each block.
        mergeSortIdDatabase(leftBlock);
        mergeSortIdDatabase(rightBlock);

        // Merge the two sorted blocks back into the original array.
        mergeIdBlocks(records, leftBlock, rightBlock);
    }

    /**
     * Merges two sorted citizen-record blocks back into the destination array.
     * Comparison is done on the ID number string, which sorts lexicographically
     * (SA ID numbers are zero-padded to 13 digits so lexicographic = numeric order).
     *
     * @param destination array to write merged records into
     * @param leftBlock   sorted left half
     * @param rightBlock  sorted right half
     */
    static void mergeIdBlocks(CitizenIdRecord[] destination,
                               CitizenIdRecord[] leftBlock,
                               CitizenIdRecord[] rightBlock) {
        // Time: O(n) per call — every element is placed in the destination exactly once.
        int leftIndex = 0, rightIndex = 0, destIndex = 0;

        while (leftIndex < leftBlock.length && rightIndex < rightBlock.length) {
            // Lexicographic string compare: SA IDs are 13-digit zero-padded strings,
            // so String.compareTo() gives the correct numeric ordering.
            if (leftBlock[leftIndex].idNumber.compareTo(rightBlock[rightIndex].idNumber) <= 0) {
                destination[destIndex++] = leftBlock[leftIndex++];
            } else {
                destination[destIndex++] = rightBlock[rightIndex++];
            }
        }

        while (leftIndex  < leftBlock.length)  destination[destIndex++] = leftBlock[leftIndex++];
        while (rightIndex < rightBlock.length) destination[destIndex++] = rightBlock[rightIndex++];
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  LINEAR SEARCH — the 1990s legacy system
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Verifies an ID by scanning the entire database from the first record.
     * This is the original implementation that caused 45-second bank delays.
     *
     * @param database      array of citizen records (sort order not required)
     * @param targetIdNumber 13-digit SA ID to verify
     * @return verification result with comparison count
     */
    static VerificationResult linearVerifyId(CitizenIdRecord[] database, String targetIdNumber) {
        // Time: O(n) — must scan every record in the worst case.
        //             For 60 million records: up to 60 000 000 comparisons.
        long comparisons = 0;

        for (int index = 0; index < database.length; index++) {
            comparisons++;
            // String equality comparison — checks all 13 characters.
            if (database[index].idNumber.equals(targetIdNumber)) {
                String status = database[index].isAlive ? "VERIFIED" : "DECEASED — FLAG FRAUD";
                return new VerificationResult(database[index], index, comparisons, status);
            }
        }

        return new VerificationResult(null, -1, comparisons, "NOT FOUND — POSSIBLE FAKE ID");
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BINARY SEARCH — Monde's fix
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Verifies an ID using binary search on the SORTED Home Affairs database.
     * Each comparison eliminates half the remaining records. For 60 million records
     * this means at most log₂(60 000 000) ≈ 26 comparisons — regardless of database size.
     *
     * @param sortedDatabase array of citizen records sorted by idNumber ascending
     * @param targetIdNumber 13-digit SA ID to verify
     * @return verification result with comparison count
     */
    static VerificationResult binaryVerifyId(CitizenIdRecord[] sortedDatabase, String targetIdNumber) {
        // Time: O(log n) — each comparison halves the candidate range.
        //                  For 60 million records: at most 26 comparisons.
        long comparisons = 0;
        int  lowBound    = 0;
        int  highBound   = sortedDatabase.length - 1;

        while (lowBound <= highBound) {
            // Overflow-safe midpoint calculation.
            int midIndex = lowBound + (highBound - lowBound) / 2;
            comparisons++;

            int cmp = sortedDatabase[midIndex].idNumber.compareTo(targetIdNumber);

            if (cmp == 0) {
                // Exact match found — return the citizen record.
                String status = sortedDatabase[midIndex].isAlive
                        ? "VERIFIED" : "DECEASED — FLAG FRAUD";
                return new VerificationResult(sortedDatabase[midIndex], midIndex, comparisons, status);

            } else if (cmp < 0) {
                // Midpoint ID is less than target — target must be in the upper half.
                // Divide and conquer: discard all records below and including midIndex.
                lowBound = midIndex + 1;

            } else {
                // Midpoint ID is greater than target — target must be in the lower half.
                // Divide and conquer: discard all records above and including midIndex.
                highBound = midIndex - 1;
            }
        }

        return new VerificationResult(null, -1, comparisons, "NOT FOUND — POSSIBLE FAKE ID");
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  UTILITY
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Generates a shuffled (unsorted) array of citizen records for demonstration.
     * ID numbers are 13-digit zero-padded integers starting from 6001010001080.
     *
     * @param count  number of records to generate
     * @param sorted when true records are stored in order; when false they are shuffled
     * @return array of citizen records
     */
    static CitizenIdRecord[] buildIdDatabase(int count, boolean sorted) {
        // Time: O(n) — one record per iteration.
        CitizenIdRecord[] db = new CitizenIdRecord[count];
        for (int index = 0; index < count; index++) {
            long idLong = 6_001_010_001_080L + index;
            String idStr = String.format("%013d", idLong);
            db[index] = new CitizenIdRecord(
                    idStr,
                    "Citizen-" + index,
                    "1960-01-01",
                    true  // everyone alive for basic test; deceased cases added separately
            );
        }

        if (!sorted) {
            // Shuffle using a simple deterministic swap pattern to simulate unsorted input.
            // This is intentionally naive — the point is to show linear search works on unsorted data.
            for (int index = 0; index < db.length - 1; index += 2) {
                CitizenIdRecord hold = db[index];
                db[index]     = db[index + 1];
                db[index + 1] = hold;
            }
        }

        return db;
    }

    /**
     * Prints one verification result with performance context.
     *
     * @param algorithmName label for the search algorithm
     * @param result        verification result to display
     * @param targetId      ID number that was looked up
     */
    static void printVerificationResult(String algorithmName,
                                        VerificationResult result, String targetId) {
        // Time: O(1) — fixed-size output.
        System.out.printf("  [%-14s] ID: %s  |  Status: %-30s  |  Comparisons: %,d%n",
                algorithmName, targetId, result.verificationStatus, result.comparisons);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MAIN DEMO
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Simulates the Home Affairs ID verification system, demonstrating both algorithms
     * with edge cases and comparison-count benchmarks across growing dataset sizes.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Home Affairs ID Verification System — Search Algorithm Benchmark ===");

        // ── EDGE CASES ──
        System.out.println("\n-- Edge case: empty database --");
        CitizenIdRecord[] emptyDb = new CitizenIdRecord[0];
        System.out.println("  Linear: found=" + linearVerifyId(emptyDb, "6001010001080").isVerified());
        System.out.println("  Binary: found=" + binaryVerifyId(emptyDb, "6001010001080").isVerified());

        System.out.println("\n-- Edge case: single record, match --");
        CitizenIdRecord[] oneRecord = {
            new CitizenIdRecord("6001010001080", "Nelson Mandela Jr", "1960-01-01", true)
        };
        printVerificationResult("Linear", linearVerifyId(oneRecord, "6001010001080"), "6001010001080");
        printVerificationResult("Binary", binaryVerifyId(oneRecord, "6001010001080"), "6001010001080");

        System.out.println("\n-- Edge case: deceased citizen (ghost-identity fraud detection) --");
        CitizenIdRecord[] dbWithDeceased = {
            new CitizenIdRecord("6001010001080", "Thabo Vilakazi", "1960-01-01", false),
            new CitizenIdRecord("6001010001081", "Amahle Ndlovu",  "1960-01-02", true),
            new CitizenIdRecord("6001010001082", "Monde Sithole",  "1960-01-03", true)
        };
        mergeSortIdDatabase(dbWithDeceased); // ensure sorted before binary search
        printVerificationResult("Linear", linearVerifyId(dbWithDeceased, "6001010001080"), "6001010001080");
        printVerificationResult("Binary", binaryVerifyId(dbWithDeceased, "6001010001080"), "6001010001080");

        System.out.println("\n-- Edge case: ID not in database --");
        printVerificationResult("Linear", linearVerifyId(dbWithDeceased, "9999999999999"), "9999999999999");
        printVerificationResult("Binary", binaryVerifyId(dbWithDeceased, "9999999999999"), "9999999999999");

        // ── SCALING BENCHMARK ──
        int[] datasetSizes = {100, 1_000, 10_000, 100_000};

        System.out.println("\n-- Comparison count scaling (worst-case lookup = last record) --");
        System.out.printf("  %-12s  %-20s  %-20s%n", "DB Size", "Linear comparisons", "Binary comparisons");
        System.out.println("  " + "-".repeat(55));

        for (int size : datasetSizes) {
            CitizenIdRecord[] db = buildIdDatabase(size, false); // unsorted for linear
            CitizenIdRecord[] sortedDb = buildIdDatabase(size, true); // pre-sorted for binary

            String lastId = String.format("%013d", 6_001_010_001_080L + size - 1);

            long linearComparisons = linearVerifyId(db, lastId).comparisons;
            long binaryComparisons = binaryVerifyId(sortedDb, lastId).comparisons;

            System.out.printf("  %-12s  %-20s  %-20s%n",
                    String.format("%,d", size),
                    String.format("%,d", linearComparisons),
                    String.format("%,d", binaryComparisons));
        }

        // ── FULL PIPELINE DEMO ──
        int fullSize = 50_000;
        System.out.println("\n-- Full pipeline: unsorted database → sort → binary search --");
        System.out.println("  Building unsorted database of " + String.format("%,d", fullSize) + " records...");
        CitizenIdRecord[] unsortedDb = buildIdDatabase(fullSize, false);

        System.out.println("  Sorting with merge sort O(n log n)...");
        long sortStart = System.nanoTime();
        mergeSortIdDatabase(unsortedDb);
        long sortMs = (System.nanoTime() - sortStart) / 1_000_000;
        System.out.println("  Sort complete in " + sortMs + " ms.");

        // Run 500 lookups on the sorted database.
        int lookupBatch = 500;
        long binaryBatchStart = System.nanoTime();
        for (int query = 0; query < lookupBatch; query++) {
            // Always query the last record — the binary search worst case.
            String target = String.format("%013d", 6_001_010_001_080L + fullSize - 1);
            binaryVerifyId(unsortedDb, target);
        }
        long binaryBatchMs = (System.nanoTime() - binaryBatchStart) / 1_000_000;

        System.out.println("  " + lookupBatch + " binary search lookups completed in " + binaryBatchMs + " ms.");

        // ── THEORETICAL AT 60 MILLION ──
        System.out.println("\n== Theoretical at 60 million records ==");
        long fullDb = 60_000_000L;
        System.out.printf("  Linear search worst case : %,d comparisons%n", fullDb);
        System.out.printf("  Binary search worst case : %d comparisons (log₂ %,d)%n",
                (long)(Math.log(fullDb) / Math.log(2)) + 1, fullDb);
        System.out.println("\nMonde's result: verification time drops from 45 seconds to under 1 ms.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Linear search: Scan every record until the target is found or the list ends.
 - Binary search: On a sorted array, inspect the midpoint; discard half the remaining candidates.
 - Merge sort: Sort the database once in O(n log n) to enable O(log n) lookups forever after.
 - O(n): Work scales with the number of records — 60 M records = 60 M comparisons worst case.
 - O(log n): Work scales with the logarithm — 60 M records = 26 comparisons worst case.
 - Sorted array: Binary search's pre-condition; string comparison on zero-padded ID numbers.
 - Divide and conquer: Binary search halves the candidate range on every comparison.
 - Index: Binary search tracks lowBound and highBound, shrinking the window each step.

 Big-O for operations implemented:
 - linearVerifyId:    O(n) — examines up to every record.
 - binaryVerifyId:    O(log n) — each comparison halves the search space.
 - mergeSortIdDatabase: O(n log n) — paid once; unlocks O(log n) for all future lookups.
 - mergeIdBlocks:     O(n) per call.
 - buildIdDatabase:   O(n).
 Space complexity:    O(n) for merge sort sub-arrays; O(1) for binary search.

 Interview questions this code prepares you for:
 - What pre-condition does binary search require, and what is the cost of meeting it?
 - Why is O(log n) so much better than O(n) at 60 million records?
 - Why use String.compareTo() for ID number comparison rather than <, =, >?
 - How does the "pay once to sort, then search cheaply forever" trade-off work?
 - What would you use instead of binary search if the data changed continuously?

 Most common mistake and how this code avoids it:
 - Mistake: Applying binary search to an unsorted array and getting incorrect results.
 - Avoided: mergeSortIdDatabase() is called explicitly before any binary search, and the
   sort-then-search pattern is demonstrated step-by-step in main().

 When to use this vs the common alternative:
 - Use binary search when: data is sorted (or can be sorted once), array is random-access,
   lookups are frequent relative to insertions.
 - Use linear search when: data is unsorted, dataset is tiny, or you need the first match
   in an unsorted list.
 - Use a hash map when: O(1) average lookup is needed and data changes frequently.
*/
