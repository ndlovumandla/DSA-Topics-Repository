/*
 The SABC maintains a database of 8 million TV licence records, each identified by a unique licence
 number stored in a sorted flat file. Every day, the enforcement system performs up to 200 000 lookups
 against this database to check reported unlicensed addresses. Enforcement officer Bongi discovers that
 the overnight lookup job — which should take minutes — is consuming 14 hours because every lookup
 uses a for-loop linear scan through all 8 million records. The problem is purely algorithmic: the
 database is already sorted, so there is no excuse for scanning from the beginning every time. Bongi
 implements binary search as a replacement, writing both versions side-by-side in the same program and
 timing each. Binary search eliminates half the remaining candidates on every comparison: 8 million
 records require at most 23 comparisons, while linear search may require all 8 million. The result:
 the overnight job drops from 14 hours to 4 minutes.
*/

/**
 * Benchmarks linear search vs binary search on a simulated SABC TV licence database.
 * Demonstrates how sorted-array binary search turns a 14-hour job into a 4-minute run.
 */
public class SABCTvLicenceDatabaseApp {

    // ──────────────────────────────────────────────────────────────────────────────
    //  DATA MODEL
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Represents one TV licence record in the SABC enforcement database.
     * The licence number is the search key; the address is payload data returned on a hit.
     */
    static class TvLicenceRecord {
        long   licenceNumber;   // unique 8-digit identifier — the sort and search key
        String holderName;
        String address;

        /**
         * Creates one TV licence record as registered in the SABC database.
         *
         * @param licenceNumber unique licence number used as the search key
         * @param holderName    name of the registered licence holder
         * @param address       physical address associated with the licence
         */
        TvLicenceRecord(long licenceNumber, String holderName, String address) {
            // Time: O(1) — three field assignments.
            this.licenceNumber = licenceNumber;
            this.holderName    = holderName;
            this.address       = address;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  SEARCH RESULT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Holds the result of one licence lookup: the record found (or null), the index
     * where it was found, and the number of comparisons made.
     */
    static class LicenceLookupResult {
        TvLicenceRecord foundRecord;    // null when not found
        int             foundAtIndex;   // -1 when not found
        long            comparisons;    // comparisons made during the search

        LicenceLookupResult(TvLicenceRecord foundRecord, int foundAtIndex, long comparisons) {
            this.foundRecord  = foundRecord;
            this.foundAtIndex = foundAtIndex;
            this.comparisons  = comparisons;
        }

        /** Returns true when the licence was found. */
        boolean isFound() { return foundRecord != null; }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  LINEAR SEARCH — Bongi's inherited legacy code
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Searches the licence database from the first record to the last, stopping when
     * the target licence number is found. This is the legacy algorithm Bongi found.
     *
     * @param database      sorted array of licence records (sort order not required by this algorithm)
     * @param targetLicence licence number to look up
     * @return lookup result with the record, index, and comparison count
     */
    static LicenceLookupResult linearSearchLicence(TvLicenceRecord[] database, long targetLicence) {
        // Time: O(n) — in the worst case every record is examined before the target is found
        //             or confirmed absent. For 8 million records: up to 8 000 000 comparisons.
        long comparisons = 0;

        for (int index = 0; index < database.length; index++) {
            comparisons++;
            // Linear scan: compare each record's licence number to the target.
            if (database[index].licenceNumber == targetLicence) {
                // Match found — return immediately with the record and comparison count.
                return new LicenceLookupResult(database[index], index, comparisons);
            }
        }

        // Exhausted every record without finding the licence number.
        return new LicenceLookupResult(null, -1, comparisons);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BINARY SEARCH — Bongi's fix
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Searches the SORTED licence database using binary search.
     * Each comparison eliminates half the remaining candidates by inspecting the midpoint.
     * Requires the database to be sorted in ascending licence number order.
     *
     * @param database      sorted array of licence records (must be sorted by licenceNumber ASC)
     * @param targetLicence licence number to look up
     * @return lookup result with the record, index, and comparison count
     */
    static LicenceLookupResult binarySearchLicence(TvLicenceRecord[] database, long targetLicence) {
        // Time: O(log n) — each comparison halves the search range.
        //                  For 8 million records: at most log₂(8 000 000) ≈ 23 comparisons.
        long comparisons = 0;
        int  lowBound    = 0;
        int  highBound   = database.length - 1;

        while (lowBound <= highBound) {
            // Midpoint: calculated to avoid integer overflow (safer than (low + high) / 2).
            int midIndex = lowBound + (highBound - lowBound) / 2;
            comparisons++;

            if (database[midIndex].licenceNumber == targetLicence) {
                // Exact match: return immediately.
                return new LicenceLookupResult(database[midIndex], midIndex, comparisons);

            } else if (database[midIndex].licenceNumber < targetLicence) {
                // Target is in the upper half — discard everything at and below midIndex.
                // This is the divide-and-conquer step: half the remaining candidates are eliminated.
                lowBound = midIndex + 1;

            } else {
                // Target is in the lower half — discard everything at and above midIndex.
                highBound = midIndex - 1;
            }
        }

        // Search window collapsed to empty without finding the target.
        return new LicenceLookupResult(null, -1, comparisons);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  UTILITY
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Builds a sorted TV licence database of the given size for benchmarking.
     * Licence numbers start at 10 000 000 and increment by 1, guaranteeing sorted order.
     *
     * @param recordCount number of records to generate
     * @return sorted array of licence records
     */
    static TvLicenceRecord[] buildSortedDatabase(int recordCount) {
        // Time: O(n) — creates one record per iteration.
        TvLicenceRecord[] db = new TvLicenceRecord[recordCount];
        for (int index = 0; index < recordCount; index++) {
            long licenceNum = 10_000_000L + index;
            db[index] = new TvLicenceRecord(
                    licenceNum,
                    "Holder-" + index,
                    index + " Enforcement Street, Johannesburg"
            );
        }
        return db;
    }

    /**
     * Prints a formatted lookup result with algorithm name and performance data.
     *
     * @param algorithmName label for the search algorithm used
     * @param result        lookup result to display
     * @param targetLicence licence number that was searched for
     */
    static void printLookupResult(String algorithmName, LicenceLookupResult result, long targetLicence) {
        // Time: O(1) — formatting of fixed-size fields.
        System.out.printf("  [%s] Looking up licence %d%n", algorithmName, targetLicence);
        System.out.printf("    Comparisons made: %,d%n", result.comparisons);
        if (result.isFound()) {
            System.out.printf("    Found at index %,d — %s, %s%n",
                    result.foundAtIndex,
                    result.foundRecord.holderName,
                    result.foundRecord.address);
        } else {
            System.out.println("    NOT FOUND — address will receive enforcement notice.");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MAIN DEMO
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Runs Bongi's benchmark: builds a licence database, performs lookups with both
     * algorithms, and demonstrates the comparison-count difference.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== SABC TV Licence Enforcement Database Lookup System ===");

        // ── EDGE CASES ──
        System.out.println("\n-- Edge case: empty database --");
        TvLicenceRecord[] emptyDb = new TvLicenceRecord[0];
        LicenceLookupResult emptyLinear = linearSearchLicence(emptyDb, 10_000_001L);
        LicenceLookupResult emptyBinary = binarySearchLicence(emptyDb, 10_000_001L);
        System.out.println("  Linear search on empty DB — found: " + emptyLinear.isFound());
        System.out.println("  Binary search on empty DB — found: " + emptyBinary.isFound());

        System.out.println("\n-- Edge case: single-record database --");
        TvLicenceRecord[] singleDb = { new TvLicenceRecord(10_000_000L, "Sipho Dlamini", "1 Main St") };
        printLookupResult("Linear", linearSearchLicence(singleDb, 10_000_000L), 10_000_000L);
        printLookupResult("Binary", binarySearchLicence(singleDb, 10_000_000L), 10_000_000L);

        System.out.println("\n-- Edge case: licence number not in database (enforcement notice required) --");
        printLookupResult("Linear", linearSearchLicence(singleDb, 99_999_999L), 99_999_999L);
        printLookupResult("Binary", binarySearchLicence(singleDb, 99_999_999L), 99_999_999L);

        // ── REALISTIC DATABASE: 100 000 RECORDS ──
        int dbSize = 100_000;
        System.out.println("\n-- Building sorted licence database (" + dbSize + " records) --");
        TvLicenceRecord[] licenceDb = buildSortedDatabase(dbSize);
        System.out.println("  Database ready. First licence: " + licenceDb[0].licenceNumber
                + "  Last licence: " + licenceDb[dbSize - 1].licenceNumber);

        // Lookup targets: first record (best case for linear), middle, last (worst case for linear).
        long[] lookupTargets = {
            licenceDb[0].licenceNumber,              // index 0 — best case for linear
            licenceDb[dbSize / 2].licenceNumber,     // middle
            licenceDb[dbSize - 1].licenceNumber,     // last — worst case for linear
            licenceDb[0].licenceNumber - 1           // licence not in database
        };

        System.out.println("\n-- Lookup comparison: linear vs binary search --");
        for (long target : lookupTargets) {
            System.out.println();
            printLookupResult("Linear", linearSearchLicence(licenceDb, target), target);
            printLookupResult("Binary", binarySearchLicence(licenceDb, target), target);
        }

        // ── BATCH TIMING: SIMULATE 1 000 LOOKUPS ──
        int batchSize = 1000;
        System.out.println("\n-- Batch timing: " + batchSize + " lookups on " + dbSize + " records --");

        long linearStart = System.nanoTime();
        for (int query = 0; query < batchSize; query++) {
            long target = licenceDb[dbSize - 1].licenceNumber; // always worst-case for linear
            linearSearchLicence(licenceDb, target);
        }
        long linearMs = (System.nanoTime() - linearStart) / 1_000_000;

        long binaryStart = System.nanoTime();
        for (int query = 0; query < batchSize; query++) {
            long target = licenceDb[dbSize - 1].licenceNumber;
            binarySearchLicence(licenceDb, target);
        }
        long binaryMs = (System.nanoTime() - binaryStart) / 1_000_000;

        System.out.println("  Linear search batch time : " + linearMs + " ms");
        System.out.println("  Binary search batch time : " + binaryMs + " ms");

        // ── THEORETICAL COMPARISON ──
        System.out.println("\n== Theoretical comparison at scale ==");
        long bigDb = 8_000_000L;
        long linearWorstCase  = bigDb;
        long binaryWorstCase  = (long)(Math.log(bigDb) / Math.log(2)) + 1;
        System.out.println("  Database size: " + String.format("%,d", bigDb) + " records");
        System.out.printf ("  Linear search worst case: %,d comparisons%n", linearWorstCase);
        System.out.printf ("  Binary search worst case: %,d comparisons%n", binaryWorstCase);
        System.out.println("\nBongi's result: overnight job drops from 14 hours to 4 minutes.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Linear search: Scan every element from the start until the target is found or the end is reached.
 - Binary search: On a sorted array, inspect the midpoint; discard the half that cannot contain the target.
 - O(n): Work grows proportionally with the number of elements — 8 M records = 8 M comparisons worst case.
 - O(log n): Work grows logarithmically — each step halves the remaining candidates.
 - Sorted array: Required by binary search; the sort order lets us safely discard half the data each step.
 - Divide and conquer: Binary search is the simplest divide-and-conquer algorithm — split in two, pick one.
 - Index: The position of an element in an array; binary search tracks low and high index bounds.

 Big-O for operations implemented:
 - linearSearchLicence: O(n) — must scan up to every record before confirming absence.
 - binarySearchLicence: O(log n) — each comparison halves the range; log₂(8 000 000) ≈ 23.
 - buildSortedDatabase: O(n) — creates one record per iteration.
 - printLookupResult:   O(1) — fixed-size field formatting only.

 Interview questions this code prepares you for:
 - Why does binary search require the array to be sorted?
 - What is the maximum number of comparisons binary search makes on n elements?
 - Why is (low + high) / 2 risky for very large arrays and how do you fix it?
 - What is the difference between O(n) and O(log n) in practical terms for 8 million records?
 - When would you prefer linear search over binary search?

 Most common mistake and how this code avoids it:
 - Mistake: Computing midpoint as (lowBound + highBound) / 2, which overflows for large indices.
 - Avoided: Using lowBound + (highBound - lowBound) / 2 — mathematically equivalent but overflow-safe.

 When to use this vs the common alternative:
 - Use binary search whenever the data is sorted and random-access (array/ArrayList).
 - Use linear search only for unsorted data, very small datasets, or when sorting would cost more than one search.
 - Use a hash table when O(1) lookup is needed and the data changes frequently.
*/
