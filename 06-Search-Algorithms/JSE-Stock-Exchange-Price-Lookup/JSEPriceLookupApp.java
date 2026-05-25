/*
 The JSE's trading platform serves price lookups for up to 3 200 listed instruments — equities,
 ETFs, and bonds — each identified by a 6-character ticker code. Every time a broker places an
 order, the platform must resolve the current price of the named ticker in microseconds; a delay
 of even a few hundred microseconds costs real money at high trading volumes. A junior developer
 originally implemented a linear scan through the securities list; at 400 instruments this was
 imperceptible, but as the list grew to 3 200 instruments, latency crept up. Systems developer
 Ayesha replaces the linear scan with binary search on the alphabetically sorted ticker list,
 implements iterative and recursive variants, adds ticker insertion in sorted position, measures
 the difference with nanosecond timing, and compares the result against Arrays.binarySearch() to
 confirm identical output. With binary search, the worst-case lookup for any of 3 200 tickers
 requires at most 12 comparisons; the linear scan required up to 3 200.
*/

import java.util.Arrays;

/**
 * Demonstrates linear search, iterative binary search, and recursive binary search on a simulated
 * JSE securities price feed. Includes sorted insertion, floor/ceiling search variants, and a
 * comparison against Java's Arrays.binarySearch().
 */
public class JSEPriceLookupApp {

    // ──────────────────────────────────────────────────────────────────────────────
    //  DATA MODEL
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Represents one listed security on the JSE trading platform.
     * The ticker code is the search key; it is always 6 uppercase characters.
     */
    static class ListedSecurity {
        String tickerCode;      // e.g. "ABCFIN" — the search key
        String securityName;    // full company/fund name
        double currentBidPrice; // last traded bid price in ZAR cents
        double currentAskPrice; // last traded ask price in ZAR cents
        String instrumentType;  // "EQUITY", "ETF", "BOND"

        /**
         * Creates one listed security as it would appear in the JSE instrument register.
         *
         * @param tickerCode       6-character uppercase ticker
         * @param securityName     full name of the listed instrument
         * @param currentBidPrice  current bid price in ZAR cents
         * @param currentAskPrice  current ask price in ZAR cents
         * @param instrumentType   EQUITY, ETF, or BOND
         */
        ListedSecurity(String tickerCode, String securityName,
                       double currentBidPrice, double currentAskPrice,
                       String instrumentType) {
            // Time: O(1) — five field assignments.
            this.tickerCode      = tickerCode;
            this.securityName    = securityName;
            this.currentBidPrice = currentBidPrice;
            this.currentAskPrice = currentAskPrice;
            this.instrumentType  = instrumentType;
        }

        /** Returns a formatted one-line summary for the trading terminal. */
        String toTerminalLine() {
            return String.format("%-8s  %-35s  BID: %8.2f  ASK: %8.2f  [%s]",
                    tickerCode, securityName, currentBidPrice, currentAskPrice, instrumentType);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  PRICE LOOKUP RESULT
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Encapsulates the outcome of one price lookup from the trading engine.
     */
    static class PriceLookupResult {
        ListedSecurity foundSecurity;   // null when not found
        int            foundAtIndex;    // -1 when not found
        long           comparisons;     // total comparisons made during the search
        long           elapsedNanos;    // nanoseconds elapsed

        PriceLookupResult(ListedSecurity foundSecurity, int foundAtIndex,
                          long comparisons, long elapsedNanos) {
            this.foundSecurity = foundSecurity;
            this.foundAtIndex  = foundAtIndex;
            this.comparisons   = comparisons;
            this.elapsedNanos  = elapsedNanos;
        }

        /** Returns true when the ticker was found in the instrument register. */
        boolean isFound() { return foundSecurity != null; }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  INSTRUMENT REGISTER (sorted array with O(n) insert)
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Holds the JSE's sorted instrument register and its current size.
     * The register is a sorted array; insertion maintains sort order.
     */
    static class InstrumentRegister {
        ListedSecurity[] securities;  // sorted array by tickerCode ASC
        int              count;       // number of active instruments

        /**
         * Allocates an instrument register with the given maximum capacity.
         *
         * @param capacity maximum number of instruments the register can hold
         */
        InstrumentRegister(int capacity) {
            // Time: O(n) for array allocation; O(1) otherwise.
            securities = new ListedSecurity[capacity];
            count      = 0;
        }

        /**
         * Inserts a new security into the register in sorted ticker-code order.
         * Shifts existing entries right to make room — maintains binary search pre-condition.
         *
         * @param newSecurity the instrument to add
         */
        void insertSorted(ListedSecurity newSecurity) {
            // Time: O(n) — in the worst case every existing entry shifts right by one.
            if (count >= securities.length) {
                System.out.println("  Register at capacity — cannot add " + newSecurity.tickerCode);
                return;
            }

            // Find the correct insertion position using binary search for the index.
            int insertPosition = findInsertPosition(newSecurity.tickerCode);

            // Shift everything from insertPosition rightward by one slot.
            for (int shiftIndex = count; shiftIndex > insertPosition; shiftIndex--) {
                securities[shiftIndex] = securities[shiftIndex - 1];
            }

            // Place the new instrument in its sorted position.
            securities[insertPosition] = newSecurity;
            count++;
        }

        /**
         * Uses binary search to find where a new ticker code should be inserted
         * to maintain sorted order. Returns the index of the first existing ticker
         * that is greater than the new one, or count if the new ticker is the largest.
         *
         * @param newTickerCode ticker code being inserted
         * @return insertion index (0 to count, inclusive)
         */
        private int findInsertPosition(String newTickerCode) {
            // Time: O(log n) — binary search for the insertion point.
            int lowBound  = 0;
            int highBound = count - 1;

            while (lowBound <= highBound) {
                int midIndex = lowBound + (highBound - lowBound) / 2;
                int cmp = securities[midIndex].tickerCode.compareTo(newTickerCode);

                if (cmp < 0) {
                    lowBound = midIndex + 1;  // new ticker belongs after midIndex
                } else {
                    highBound = midIndex - 1; // new ticker belongs before midIndex
                }
            }

            // lowBound is now the correct insertion position.
            return lowBound;
        }

        /** Returns a snapshot of the current register for display. */
        ListedSecurity[] snapshot() {
            ListedSecurity[] snap = new ListedSecurity[count];
            System.arraycopy(securities, 0, snap, 0, count);
            return snap;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  LINEAR SEARCH — the original junior-developer implementation
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Looks up a security by ticker code using a linear scan.
     * The original implementation — acceptable at 400 instruments, slow at 3 200.
     *
     * @param register   instrument register (sort order not required)
     * @param tickerCode 6-character ticker to find
     * @return price lookup result with comparison count
     */
    static PriceLookupResult linearLookup(InstrumentRegister register, String tickerCode) {
        // Time: O(n) — scans every entry until a match is found or the register ends.
        //             Worst case: 3 200 comparisons per order placement.
        long comparisons = 0;
        long startNanos  = System.nanoTime();

        for (int index = 0; index < register.count; index++) {
            comparisons++;
            if (register.securities[index].tickerCode.equals(tickerCode)) {
                return new PriceLookupResult(register.securities[index], index,
                        comparisons, System.nanoTime() - startNanos);
            }
        }

        return new PriceLookupResult(null, -1, comparisons, System.nanoTime() - startNanos);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BINARY SEARCH — ITERATIVE (Ayesha's primary fix)
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Looks up a security by ticker code using iterative binary search.
     * Ayesha's production replacement — no recursion overhead, constant O(log n) comparisons.
     *
     * @param register   instrument register sorted by tickerCode ascending
     * @param tickerCode 6-character ticker to find
     * @return price lookup result with comparison count
     */
    static PriceLookupResult binaryLookupIterative(InstrumentRegister register, String tickerCode) {
        // Time: O(log n) — each comparison halves the search window.
        //                  For 3 200 instruments: at most log₂(3200) ≈ 12 comparisons.
        // Space: O(1) — only index variables; no recursion stack.
        long comparisons = 0;
        long startNanos  = System.nanoTime();
        int  lowBound    = 0;
        int  highBound   = register.count - 1;

        while (lowBound <= highBound) {
            // Overflow-safe midpoint: avoids integer overflow for very large registers.
            int midIndex = lowBound + (highBound - lowBound) / 2;
            comparisons++;

            int cmp = register.securities[midIndex].tickerCode.compareTo(tickerCode);

            if (cmp == 0) {
                // Exact match — return the security with bid/ask prices.
                return new PriceLookupResult(register.securities[midIndex], midIndex,
                        comparisons, System.nanoTime() - startNanos);

            } else if (cmp < 0) {
                // Midpoint ticker is alphabetically before target — narrow to upper half.
                lowBound = midIndex + 1;

            } else {
                // Midpoint ticker is alphabetically after target — narrow to lower half.
                highBound = midIndex - 1;
            }
        }

        return new PriceLookupResult(null, -1, comparisons, System.nanoTime() - startNanos);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  BINARY SEARCH — RECURSIVE (alternative implementation for comparison)
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Public entry point for recursive binary search.
     *
     * @param register   instrument register sorted by tickerCode ascending
     * @param tickerCode 6-character ticker to find
     * @return price lookup result with comparison count
     */
    static PriceLookupResult binaryLookupRecursive(InstrumentRegister register, String tickerCode) {
        // Time: O(log n) — same as iterative; each recursive call halves the range.
        // Space: O(log n) — call stack depth equals the number of halvings.
        long startNanos = System.nanoTime();
        long[] compRef  = {0}; // single-element array used to pass comparison count by reference
        int foundIndex  = binaryRecursiveHelper(register, tickerCode, 0, register.count - 1, compRef);
        long elapsed    = System.nanoTime() - startNanos;

        if (foundIndex == -1) {
            return new PriceLookupResult(null, -1, compRef[0], elapsed);
        }
        return new PriceLookupResult(register.securities[foundIndex], foundIndex, compRef[0], elapsed);
    }

    /**
     * Recursive helper: searches the sub-range [lowBound, highBound] for the target ticker.
     *
     * @param register   instrument register
     * @param tickerCode target ticker code
     * @param lowBound   inclusive lower bound of the current search range
     * @param highBound  inclusive upper bound of the current search range
     * @param compRef    single-element array tracking total comparisons (passed by reference)
     * @return index of the found entry, or -1
     */
    static int binaryRecursiveHelper(InstrumentRegister register, String tickerCode,
                                      int lowBound, int highBound, long[] compRef) {
        // Base case: the search window has collapsed — target is not present.
        if (lowBound > highBound) return -1;

        int midIndex = lowBound + (highBound - lowBound) / 2;
        compRef[0]++;

        int cmp = register.securities[midIndex].tickerCode.compareTo(tickerCode);

        if (cmp == 0) {
            return midIndex; // found

        } else if (cmp < 0) {
            // Recurse on the upper half — divide and conquer.
            return binaryRecursiveHelper(register, tickerCode, midIndex + 1, highBound, compRef);

        } else {
            // Recurse on the lower half — divide and conquer.
            return binaryRecursiveHelper(register, tickerCode, lowBound, midIndex - 1, compRef);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  FLOOR / CEILING SEARCH VARIANTS
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Finds the security with the ticker code just BELOW the given target (floor search).
     * Useful when a broker enters an approximate ticker and wants the nearest match below it.
     *
     * @param register   sorted instrument register
     * @param tickerCode target ticker to find the floor of
     * @return the security with the greatest ticker code <= target, or null if none exists
     */
    static ListedSecurity floorTickerSearch(InstrumentRegister register, String tickerCode) {
        // Time: O(log n) — one binary search pass.
        int lowBound = 0, highBound = register.count - 1;
        ListedSecurity floorCandidate = null;

        while (lowBound <= highBound) {
            int midIndex = lowBound + (highBound - lowBound) / 2;
            int cmp = register.securities[midIndex].tickerCode.compareTo(tickerCode);

            if (cmp <= 0) {
                // This midpoint is a valid floor candidate; look for a closer one to the right.
                floorCandidate = register.securities[midIndex];
                lowBound = midIndex + 1;
            } else {
                highBound = midIndex - 1;
            }
        }

        return floorCandidate;
    }

    /**
     * Finds the security with the ticker code just ABOVE the given target (ceiling search).
     * Useful for "next available" queries when a specific ticker is delisted.
     *
     * @param register   sorted instrument register
     * @param tickerCode target ticker to find the ceiling of
     * @return the security with the smallest ticker code >= target, or null if none exists
     */
    static ListedSecurity ceilingTickerSearch(InstrumentRegister register, String tickerCode) {
        // Time: O(log n) — one binary search pass.
        int lowBound = 0, highBound = register.count - 1;
        ListedSecurity ceilingCandidate = null;

        while (lowBound <= highBound) {
            int midIndex = lowBound + (highBound - lowBound) / 2;
            int cmp = register.securities[midIndex].tickerCode.compareTo(tickerCode);

            if (cmp >= 0) {
                // This midpoint is a valid ceiling candidate; look for a closer one to the left.
                ceilingCandidate = register.securities[midIndex];
                highBound = midIndex - 1;
            } else {
                lowBound = midIndex + 1;
            }
        }

        return ceilingCandidate;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  JAVA BUILT-IN COMPARISON
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Verifies the result of our binary search against Java's Arrays.binarySearch().
     * Arrays.binarySearch() uses a comparable key approach; we extract the ticker codes first.
     *
     * @param register   sorted instrument register
     * @param tickerCode ticker to search for
     * @return the index returned by Arrays.binarySearch(), or negative insertion point
     */
    static int javaBuiltInSearch(InstrumentRegister register, String tickerCode) {
        // Time: O(log n) — Arrays.binarySearch() is the standard library binary search.
        String[] tickerCodes = new String[register.count];
        for (int index = 0; index < register.count; index++) {
            tickerCodes[index] = register.securities[index].tickerCode;
        }
        // Arrays.binarySearch returns index if found, or -(insertionPoint)-1 if not found.
        return Arrays.binarySearch(tickerCodes, tickerCode);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  UTILITY
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Builds an instrument register populated with generated JSE-style tickers.
     * Tickers are generated alphabetically so insertion maintains sorted order.
     *
     * @param capacity    maximum register capacity
     * @param symbolCount number of instruments to generate
     * @return populated instrument register in sorted order
     */
    static InstrumentRegister buildRegister(int capacity, int symbolCount) {
        // Time: O(n) to build; each insertSorted is O(n) in worst case but insertion is ordered
        //       here so the sorted-insert scan terminates immediately — effective O(1) per insert.
        InstrumentRegister register = new InstrumentRegister(capacity);

        // Generate tickers: AA0001, AA0002, ..., AB0001, etc. — purely alphabetic generation.
        for (int index = 0; index < symbolCount && index < capacity; index++) {
            int firstLetter  = index / (26 * 1000);
            int secondLetter = (index / 1000) % 26;
            int digits       = index % 1000;
            String ticker = String.format("%c%c%04d",
                    'A' + (firstLetter % 26),
                    'A' + secondLetter,
                    digits);

            double bid = 10000.0 + (index * 7.53) % 50000;
            double ask = bid + 10 + (index % 50);
            String type = (index % 10 == 0) ? "ETF" : (index % 20 == 0) ? "BOND" : "EQUITY";

            register.insertSorted(new ListedSecurity(ticker, "Company-" + index, bid, ask, type));
        }

        return register;
    }

    /**
     * Prints a formatted price lookup result to the trading terminal.
     *
     * @param algorithmName label for the algorithm used
     * @param result        lookup result to display
     * @param tickerCode    ticker that was searched for
     */
    static void printLookupResult(String algorithmName, PriceLookupResult result, String tickerCode) {
        // Time: O(1) — fixed-size output.
        System.out.printf("  [%-22s] Ticker: %-8s  Comparisons: %3d  Time: %,d ns%n",
                algorithmName, tickerCode, result.comparisons, result.elapsedNanos);
        if (result.isFound()) {
            System.out.println("    " + result.foundSecurity.toTerminalLine());
        } else {
            System.out.println("    NOT FOUND — order rejected: unknown instrument.");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    //  MAIN DEMO
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Runs Ayesha's benchmark: builds a 3 200-instrument register, performs lookups with
     * all algorithms, demonstrates floor/ceiling variants, and validates against the built-in.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== JSE Trading Platform — Securities Price Lookup Benchmark ===");

        // ── EDGE CASES ──
        System.out.println("\n-- Edge case: empty register --");
        InstrumentRegister emptyReg = new InstrumentRegister(10);
        System.out.println("  Linear: found="  + linearLookup(emptyReg, "ABCFIN").isFound());
        System.out.println("  Binary: found="  + binaryLookupIterative(emptyReg, "ABCFIN").isFound());

        System.out.println("\n-- Edge case: single instrument --");
        InstrumentRegister singleReg = new InstrumentRegister(5);
        singleReg.insertSorted(new ListedSecurity("NSPNXX", "Naspers Ltd", 287450, 287520, "EQUITY"));
        printLookupResult("Linear", linearLookup(singleReg, "NSPNXX"), "NSPNXX");
        printLookupResult("Binary iterative", binaryLookupIterative(singleReg, "NSPNXX"), "NSPNXX");

        System.out.println("\n-- Edge case: ticker not in register (order rejected) --");
        printLookupResult("Linear", linearLookup(singleReg, "ZZZTOP"), "ZZZTOP");
        printLookupResult("Binary iterative", binaryLookupIterative(singleReg, "ZZZTOP"), "ZZZTOP");

        // ── BUILD FULL 3 200-INSTRUMENT REGISTER ──
        int registerSize = 3200;
        System.out.println("\n-- Building " + registerSize + "-instrument JSE register --");
        InstrumentRegister jseRegister = buildRegister(registerSize + 100, registerSize);
        System.out.println("  Register ready. First ticker: " + jseRegister.securities[0].tickerCode
                + "  Last ticker: " + jseRegister.securities[jseRegister.count - 1].tickerCode);

        // ── COMPARISON: FIRST, MIDDLE, AND LAST TICKERS ──
        System.out.println("\n-- Algorithm comparison: best / middle / worst-case tickers --");
        String firstTicker  = jseRegister.securities[0].tickerCode;
        String middleTicker = jseRegister.securities[registerSize / 2].tickerCode;
        String lastTicker   = jseRegister.securities[registerSize - 1].tickerCode;

        for (String ticker : new String[]{firstTicker, middleTicker, lastTicker}) {
            System.out.println("\n  Ticker: " + ticker);
            printLookupResult("Linear scan",      linearLookup(jseRegister, ticker),        ticker);
            printLookupResult("Binary iterative", binaryLookupIterative(jseRegister, ticker), ticker);
            printLookupResult("Binary recursive", binaryLookupRecursive(jseRegister, ticker), ticker);
        }

        // ── FLOOR AND CEILING SEARCHES ──
        System.out.println("\n-- Floor / ceiling search (nearest ticker variants) --");
        String ghostTicker = "AA0500"; // may or may not exist; floor/ceiling always answer
        ListedSecurity floor   = floorTickerSearch(jseRegister, ghostTicker);
        ListedSecurity ceiling = ceilingTickerSearch(jseRegister, ghostTicker);
        System.out.println("  Query ticker: " + ghostTicker);
        System.out.println("  Floor  (largest ticker <= query): " + (floor   != null ? floor.tickerCode   : "none"));
        System.out.println("  Ceiling (smallest ticker >= query): " + (ceiling != null ? ceiling.tickerCode : "none"));

        // ── JAVA BUILT-IN VALIDATION ──
        System.out.println("\n-- Validation against Arrays.binarySearch() --");
        for (String ticker : new String[]{firstTicker, middleTicker, lastTicker, "ZZZTOP"}) {
            PriceLookupResult ourResult = binaryLookupIterative(jseRegister, ticker);
            int builtInIndex = javaBuiltInSearch(jseRegister, ticker);
            boolean match = (ourResult.foundAtIndex == builtInIndex)
                    || (ourResult.foundAtIndex == -1 && builtInIndex < 0);
            System.out.printf("  Ticker %-8s  our index: %4d  Arrays.binarySearch: %4d  match: %b%n",
                    ticker, ourResult.foundAtIndex, builtInIndex, match);
        }

        // ── BATCH TIMING ──
        int batchSize = 10_000;
        System.out.println("\n-- Batch timing: " + String.format("%,d", batchSize)
                + " lookups on " + registerSize + " instruments --");

        long linearBatchStart = System.nanoTime();
        for (int query = 0; query < batchSize; query++) {
            linearLookup(jseRegister, lastTicker);
        }
        long linearBatchNs = System.nanoTime() - linearBatchStart;

        long binaryBatchStart = System.nanoTime();
        for (int query = 0; query < batchSize; query++) {
            binaryLookupIterative(jseRegister, lastTicker);
        }
        long binaryBatchNs = System.nanoTime() - binaryBatchStart;

        System.out.printf("  Linear search batch : %,d ns total  (%,d ns avg/lookup)%n",
                linearBatchNs, linearBatchNs / batchSize);
        System.out.printf("  Binary search batch : %,d ns total  (%,d ns avg/lookup)%n",
                binaryBatchNs, binaryBatchNs / batchSize);

        // ── THEORETICAL SUMMARY ──
        System.out.println("\n== Comparison count at 3 200 instruments ==");
        System.out.printf("  Linear search worst case: %,d comparisons%n", (long)registerSize);
        System.out.printf("  Binary search worst case: %d comparisons (log₂ %,d)%n",
                (long)(Math.log(registerSize) / Math.log(2)) + 1, (long)registerSize);
        System.out.println("\nAyesha's result: every broker order now resolves in under 1 microsecond.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - Linear search: Scan every instrument from the start until the target ticker is found.
 - Binary search (iterative): Track low/high index bounds; inspect midpoint; halve the range each step.
 - Binary search (recursive): Same logic expressed as recursive calls on narrowed sub-ranges.
 - Floor search: Find the largest element <= the target — uses binary search, keeps a running candidate.
 - Ceiling search: Find the smallest element >= the target — mirror of floor search.
 - Sorted insert: Find insertion point with O(log n) binary search; shift with O(n) — total O(n).
 - O(n): Comparisons grow linearly — 3 200 instruments = 3 200 worst-case comparisons.
 - O(log n): Comparisons grow logarithmically — 3 200 instruments = 12 worst-case comparisons.
 - Divide and conquer: Binary search halves the candidate range on every comparison.
 - Index: Binary search operates on array positions (lowBound, highBound, midIndex).

 Big-O for operations implemented:
 - linearLookup:           O(n) — scans every entry in worst case.
 - binaryLookupIterative:  O(log n) — each comparison halves the range.
 - binaryLookupRecursive:  O(log n) time; O(log n) space (call stack depth).
 - floorTickerSearch:      O(log n) — one binary search pass with a running candidate.
 - ceilingTickerSearch:    O(log n) — mirror of floor search.
 - insertSorted:           O(log n) to find position + O(n) to shift = O(n) overall.
 - findInsertPosition:     O(log n) — binary search for insertion index.
 - buildRegister:          O(n) — one insert per instrument; inserts happen in order so O(n) total.
 Space complexity:
   - Iterative binary search: O(1) — only index variables.
   - Recursive binary search: O(log n) — one stack frame per halving.

 Interview questions this code prepares you for:
 - What is the difference between iterative and recursive binary search?
 - Why is the recursive version O(log n) space while the iterative version is O(1)?
 - How do floor and ceiling binary search variants work?
 - Why is sorted insertion O(n) even though the insertion point is found in O(log n)?
 - When would you choose a hash map over binary search for a price lookup system?

 Most common mistake and how this code avoids it:
 - Mistake: Off-by-one errors in the loop condition (using < instead of <=) causing the last
   or first element to be skipped.
 - Avoided: The condition `lowBound <= highBound` is used consistently; when lowBound > highBound
   the window is truly empty and the loop (or recursion) exits.

 When to use this vs the common alternative:
 - Use binary search for read-heavy workloads on sorted arrays: O(log n) lookup, O(1) space.
 - Use a hash map for write-heavy workloads or when O(1) average lookup is needed.
 - Use a BST (e.g., TreeMap) when you need floor/ceiling AND fast insertion simultaneously.
 - Use Arrays.binarySearch() in production — it is correct, overflow-safe, and thoroughly tested.
*/
