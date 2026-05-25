/*
 South Africa's progressive income-tax model applies different rates to successive slices of income,
 from 18% in the first bracket up to 45% above R1.817 million. Computing tax is equivalent to a
 recursive definition: tax up to bracket k equals tax up to bracket k-1 plus tax on the slice inside
 bracket k. SARS developer Lerato is building an advanced eFiling calculator and wants both
 correctness and teachability: iterative and recursive implementations must match exactly.
 She also introduces memoization for bracket-threshold tax values so repeated calculations for many
 taxpayers avoid recomputing lower-bracket totals. To make the educational comparison complete,
 she benchmarks naive recursion, memoized recursion, and iterative logic, and includes a built-in
 comparison path using Java stream reduction for verification only.
*/

import java.util.stream.IntStream;

/**
 * Advanced recursion project for SARS progressive tax.
 * Includes iterative, recursive, memoized-recursive, and built-in comparison variants.
 */
public class SarsTaxBracketCalculatorAdvancedApp {

    /**
     * Represents one progressive tax bracket definition.
     */
    static class TaxBand {
        double lowerBound;
        double upperBound;
        double rate;

        /**
         * Creates one tax bracket.
         *
         * @param lowerBound inclusive lower bound
         * @param upperBound exclusive upper bound (Double.MAX_VALUE for top bracket)
         * @param rate tax rate for this band
         */
        TaxBand(double lowerBound, double upperBound, double rate) {
            // Time: O(1) - constant assignment.
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.rate = rate;
        }
    }

    /** SARS brackets used in this educational implementation. */
    static final TaxBand[] TAX_BANDS = {
            new TaxBand(0,        237100,   0.18),
            new TaxBand(237100,   370500,   0.26),
            new TaxBand(370500,   512800,   0.31),
            new TaxBand(512800,   673000,   0.36),
            new TaxBand(673000,   857900,   0.39),
            new TaxBand(857900,   1817000,  0.41),
            new TaxBand(1817000,  Double.MAX_VALUE, 0.45)
    };

    /**
     * Stores tax result and profiling metadata.
     */
    static class TaxComputationResult {
        double taxAmount;
        long recursionCalls;
        long elapsedNanos;

        TaxComputationResult(double taxAmount, long recursionCalls, long elapsedNanos) {
            this.taxAmount = taxAmount;
            this.recursionCalls = recursionCalls;
            this.elapsedNanos = elapsedNanos;
        }
    }

    /**
     * Mutable counter wrapper so recursion can update call counts.
     */
    static class CallCounter {
        long value = 0;
    }

    /**
     * Iterative baseline for tax computation.
     *
     * @param annualIncome taxable income
     * @return computed tax
     */
    static double calculateTaxIterative(double annualIncome) {
        // Time: O(b) where b is number of bands; effectively constant for fixed bracket table.
        if (annualIncome <= 0) {
            return 0;
        }

        double totalTax = 0;

        for (int bandIndex = 0; bandIndex < TAX_BANDS.length; bandIndex++) {
            TaxBand band = TAX_BANDS[bandIndex];
            if (annualIncome <= band.lowerBound) {
                break;
            }

            double taxableSliceTop = Math.min(annualIncome, band.upperBound);
            double taxableSlice = taxableSliceTop - band.lowerBound;
            totalTax += taxableSlice * band.rate;
        }

        return totalTax;
    }

    /**
     * Naive recursive tax computation without memoized threshold cache.
     *
     * @param annualIncome taxable income
     * @return result including call count and elapsed time
     */
    static TaxComputationResult calculateTaxRecursiveProfiled(double annualIncome) {
        // Time: O(b) per query for fixed bracket table.
        long start = System.nanoTime();
        CallCounter callCounter = new CallCounter();
        double tax = recursiveTaxNaive(annualIncome, TAX_BANDS.length - 1, callCounter);
        long elapsed = System.nanoTime() - start;
        return new TaxComputationResult(tax, callCounter.value, elapsed);
    }

    /**
     * Recursive helper for naive version.
     *
     * @param annualIncome income
     * @param bandIndex current band index
     * @param callCounter counts recursive calls
     * @return tax up to this band
     */
    static double recursiveTaxNaive(double annualIncome, int bandIndex, CallCounter callCounter) {
        // Time: O(b) call chain; space O(b) call stack.
        callCounter.value++;

        // Base case: no band left or no taxable income.
        if (bandIndex < 0 || annualIncome <= 0) {
            return 0;
        }

        TaxBand band = TAX_BANDS[bandIndex];

        // If income does not reach this band, recurse into lower band.
        if (annualIncome <= band.lowerBound) {
            return recursiveTaxNaive(annualIncome, bandIndex - 1, callCounter);
        }

        double taxableSliceTop = Math.min(annualIncome, band.upperBound);
        double taxableSlice = taxableSliceTop - band.lowerBound;
        double thisBandTax = taxableSlice * band.rate;

        // Recursive decomposition: tax at this level + tax below lower bound.
        return thisBandTax + recursiveTaxNaive(band.lowerBound, bandIndex - 1, callCounter);
    }

    /**
     * Memoized recursive tax computation.
     * Caches cumulative tax at each band threshold to avoid recomputing lower totals repeatedly.
     *
     * @param annualIncome taxable income
     * @return result including call count and elapsed time
     */
    static TaxComputationResult calculateTaxRecursiveMemoizedProfiled(double annualIncome) {
        // Time: O(b) first query with cold cache; repeated queries benefit from threshold cache reuse.
        long start = System.nanoTime();
        CallCounter callCounter = new CallCounter();

        // cacheTaxAtThreshold[i] stores total tax for full income up to TAX_BANDS[i].lowerBound.
        double[] cacheTaxAtThreshold = new double[TAX_BANDS.length];
        boolean[] cacheReady = new boolean[TAX_BANDS.length];

        double tax = recursiveTaxMemoized(annualIncome, TAX_BANDS.length - 1,
                cacheTaxAtThreshold, cacheReady, callCounter);

        long elapsed = System.nanoTime() - start;
        return new TaxComputationResult(tax, callCounter.value, elapsed);
    }

    /**
     * Memoized recursive helper.
     *
     * @param annualIncome income
     * @param bandIndex current band index
     * @param cacheTaxAtThreshold threshold-tax cache
     * @param cacheReady indicates whether a cache cell is valid
     * @param callCounter recursion call counter
     * @return tax value
     */
    static double recursiveTaxMemoized(
            double annualIncome,
            int bandIndex,
            double[] cacheTaxAtThreshold,
            boolean[] cacheReady,
            CallCounter callCounter) {

        // Time: O(b) with caching; repeated threshold states are O(1) retrieval.
        callCounter.value++;

        if (bandIndex < 0 || annualIncome <= 0) {
            return 0;
        }

        TaxBand band = TAX_BANDS[bandIndex];

        if (annualIncome <= band.lowerBound) {
            return recursiveTaxMemoized(annualIncome, bandIndex - 1,
                    cacheTaxAtThreshold, cacheReady, callCounter);
        }

        double taxableSliceTop = Math.min(annualIncome, band.upperBound);
        double taxableSlice = taxableSliceTop - band.lowerBound;
        double thisBandTax = taxableSlice * band.rate;

        // Optimization: tax below this band's lower bound is a repeated subproblem.
        double lowerThresholdTax = getTaxAtThresholdMemoized(
                band.lowerBound,
                bandIndex - 1,
                cacheTaxAtThreshold,
                cacheReady,
                callCounter);

        return thisBandTax + lowerThresholdTax;
    }

    /**
     * Returns tax due exactly at a threshold income using memoization.
     *
     * @param thresholdIncome target threshold income
     * @param bandIndex corresponding lower band index
     * @param cacheTaxAtThreshold cache array
     * @param cacheReady validity array
     * @param callCounter recursion counter
     * @return tax amount at threshold
     */
    static double getTaxAtThresholdMemoized(
            double thresholdIncome,
            int bandIndex,
            double[] cacheTaxAtThreshold,
            boolean[] cacheReady,
            CallCounter callCounter) {

        // Time: O(1) for cache hit, otherwise O(b) down recursive chain once per band index.
        if (bandIndex < 0 || thresholdIncome <= 0) {
            return 0;
        }

        if (cacheReady[bandIndex]) {
            return cacheTaxAtThreshold[bandIndex];
        }

        double tax = recursiveTaxMemoized(thresholdIncome, bandIndex,
                cacheTaxAtThreshold, cacheReady, callCounter);

        cacheTaxAtThreshold[bandIndex] = tax;
        cacheReady[bandIndex] = true;
        return tax;
    }

    /**
     * Built-in equivalent verification using Java Streams for reduction.
     * This is not the primary teaching implementation; used only for comparison.
     *
     * @param annualIncome taxable income
     * @return computed tax by stream reduction
     */
    static double calculateTaxWithBuiltInStreams(double annualIncome) {
        // Time: O(b) - stream traverses all bands once.
        if (annualIncome <= 0) {
            return 0;
        }

        return IntStream.range(0, TAX_BANDS.length)
                .mapToDouble(i -> {
                    TaxBand band = TAX_BANDS[i];
                    if (annualIncome <= band.lowerBound) {
                        return 0;
                    }
                    double taxableSliceTop = Math.min(annualIncome, band.upperBound);
                    double taxableSlice = taxableSliceTop - band.lowerBound;
                    return taxableSlice * band.rate;
                })
                .sum();
    }

    /**
     * Recursive factorial for concept coverage.
     *
     * @param n non-negative integer
     * @return n!
     */
    static long factorialRecursive(int n) {
        // Time: O(n), Space: O(n) call stack.
        if (n < 0) {
            throw new IllegalArgumentException("Factorial requires n >= 0");
        }
        if (n <= 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }

    /**
     * Naive Fibonacci recursion for complexity contrast.
     *
     * @param n sequence position
     * @return Fibonacci(n)
     */
    static long fibonacciNaive(int n) {
        // Time: O(2^n), Space: O(n) call stack.
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci requires n >= 0");
        }
        if (n <= 1) {
            return n;
        }
        return fibonacciNaive(n - 1) + fibonacciNaive(n - 2);
    }

    /**
     * Memoized Fibonacci recursion with array cache.
     *
     * @param n sequence position
     * @param memo cache array initialized with -1
     * @return Fibonacci(n)
     */
    static long fibonacciMemoized(int n, long[] memo) {
        // Time: O(n), Space: O(n) cache + O(n) stack depth.
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci requires n >= 0");
        }
        if (n <= 1) {
            return n;
        }
        if (memo[n] != -1) {
            return memo[n];
        }
        memo[n] = fibonacciMemoized(n - 1, memo) + fibonacciMemoized(n - 2, memo);
        return memo[n];
    }

    /**
     * Safe recursion-depth counter to discuss stack-overflow risk.
     *
     * @param depth remaining depth
     * @return traversed depth
     */
    static int safeDepthCounter(int depth) {
        // Time: O(depth), Space: O(depth).
        if (depth == 0) {
            return 0;
        }
        return 1 + safeDepthCounter(depth - 1);
    }

    /**
     * Prints one scenario comparing all calculation variants.
     *
     * @param income taxable income
     */
    static void printAdvancedScenario(double income) {
        // Time: O(b) per method.
        double iterative = calculateTaxIterative(income);
        TaxComputationResult naiveRec = calculateTaxRecursiveProfiled(income);
        TaxComputationResult memoRec = calculateTaxRecursiveMemoizedProfiled(income);
        double builtIn = calculateTaxWithBuiltInStreams(income);

        System.out.printf("Income R%,.2f%n", income);
        System.out.printf("  Iterative tax         : R%,.2f%n", iterative);
        System.out.printf("  Recursive naive tax   : R%,.2f  | calls=%d | time=%d ns%n",
                naiveRec.taxAmount, naiveRec.recursionCalls, naiveRec.elapsedNanos);
        System.out.printf("  Recursive memoized tax: R%,.2f  | calls=%d | time=%d ns%n",
                memoRec.taxAmount, memoRec.recursionCalls, memoRec.elapsedNanos);
        System.out.printf("  Built-in stream tax   : R%,.2f%n", builtIn);

        boolean allMatch = Math.abs(iterative - naiveRec.taxAmount) < 0.0001
                && Math.abs(iterative - memoRec.taxAmount) < 0.0001
                && Math.abs(iterative - builtIn) < 0.0001;
        System.out.println("  All methods match: " + allMatch);
    }

    /**
     * Runs a story-driven advanced demonstration.
     *
     * @param args unused args
     */
    public static void main(String[] args) {
        System.out.println("=== SARS eFiling Advanced Tax Calculator (Recursion + Memoization) ===");

        System.out.println("\n-- Edge Cases --");
        printAdvancedScenario(0);
        printAdvancedScenario(-100000);

        System.out.println("\n-- Typical and High-Income Scenarios --");
        printAdvancedScenario(230000);
        printAdvancedScenario(512800);
        printAdvancedScenario(1200000);
        printAdvancedScenario(4000000);

        System.out.println("\n-- Factorial and Fibonacci Recursion Concepts --");
        System.out.println("  factorial(8) = " + factorialRecursive(8));

        int fibN = 40;
        long naiveStart = System.nanoTime();
        long fibNaive = fibonacciNaive(fibN);
        long naiveMs = (System.nanoTime() - naiveStart) / 1_000_000;

        long[] memo = new long[fibN + 1];
        for (int i = 0; i < memo.length; i++) {
            memo[i] = -1;
        }
        long memoStart = System.nanoTime();
        long fibMemo = fibonacciMemoized(fibN, memo);
        long memoMs = (System.nanoTime() - memoStart) / 1_000_000;

        System.out.println("  fibonacciNaive(" + fibN + ") = " + fibNaive + " in " + naiveMs + " ms");
        System.out.println("  fibonacciMemoized(" + fibN + ") = " + fibMemo + " in " + memoMs + " ms");

        int safeDepth = 150;
        System.out.println("\n-- Call Stack Depth Safety --");
        System.out.println("  Safe depth reached: " + safeDepthCounter(safeDepth));
        System.out.println("  Warning: uncontrolled recursion depth can throw StackOverflowError.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - base case: recursion stops at bandIndex < 0, n <= 1, or depth == 0.
 - recursive call: each step solves a smaller subproblem (lower bracket or n-1/n-2).
 - call stack: recursive frames accumulate until base case returns begin unwinding.
 - factorial: single-branch recursion example.
 - Fibonacci: multi-branch recursion with overlapping subproblems.
 - memoization: caches repeated subproblem results (threshold tax / Fibonacci values).
 - stack overflow: occurs when recursion depth exceeds available call-stack memory.

 Big-O for implemented operations:
 - calculateTaxIterative: O(b) time, O(1) space.
 - recursiveTaxNaive / calculateTaxRecursiveProfiled: O(b) time, O(b) stack.
 - recursiveTaxMemoized / calculateTaxRecursiveMemoizedProfiled: O(b) time with cache reuse,
   O(b) cache space + O(b) stack.
 - getTaxAtThresholdMemoized: O(1) on cache hit, O(b) on first computation.
 - calculateTaxWithBuiltInStreams: O(b) time, O(1) extra space.
 - factorialRecursive: O(n) time, O(n) stack.
 - fibonacciNaive: O(2^n) time, O(n) stack.
 - fibonacciMemoized: O(n) time, O(n) stack + O(n) memo.
 - safeDepthCounter: O(d) time, O(d) stack.

 Interview questions this prepares you for:
 - How does recursive tax decomposition mirror progressive-bracket mathematics?
 - What repeated subproblem is memoized in recursive tax computation?
 - Why does naive Fibonacci explode exponentially while memoized Fibonacci is linear?
 - What are the trade-offs between recursion clarity and stack-memory risk?
 - How do you validate a custom recursive implementation against a built-in alternative?

 Common mistake and prevention:
 - Mistake: recursive calls that do not strictly reduce problem size, causing non-termination.
 - Avoided here: each recursive branch reduces bandIndex or n, guaranteeing base-case reach.

 When to use recursion vs the common alternative:
 - Use recursion when the business rule is naturally hierarchical (tax brackets, trees, divide-and-conquer).
 - Use iteration for very deep sequences where stack limits are a concern.
 - Use built-ins (e.g., streams) for concise verification paths, but keep explicit recursion for pedagogy.
*/