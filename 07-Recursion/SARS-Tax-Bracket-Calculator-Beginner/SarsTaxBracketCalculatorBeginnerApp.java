/*
 South Africa's personal income tax uses progressive brackets: each slice of income is taxed at
 that bracket's rate, and total tax is the sum of all slices below and including the taxpayer's
 top bracket. SARS developer Lerato is building an eFiling help-portal calculator and wants code
 that is easy for junior developers to reason about and debug. Recursion is a natural fit here
 because each bracket can be defined as "tax for the previous bracket plus tax on the current slice".
 Lerato implements both iterative and recursive versions so learners can compare style and output.
 She also adds factorial and Fibonacci demonstrations to teach base cases, recursive calls,
 memoization, and why naive recursion can cause stack overflow risks at large depths.
*/

/**
 * Beginner recursion demo for SARS tax calculation.
 * Shows iterative and recursive progressive-tax calculations plus recursion fundamentals.
 */
public class SarsTaxBracketCalculatorBeginnerApp {

    /**
     * Represents one SARS tax bracket.
     * lowerBound is inclusive; upperBound is exclusive except for the final bracket.
     */
    static class TaxBracket {
        double lowerBound;
        double upperBound;
        double rate;

        /**
         * Creates one tax bracket.
         *
         * @param lowerBound inclusive lower income bound
         * @param upperBound exclusive upper income bound (use Double.MAX_VALUE for final bracket)
         * @param rate       bracket tax rate (e.g., 0.18)
         */
        TaxBracket(double lowerBound, double upperBound, double rate) {
            // Time: O(1) - constant-time assignments.
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.rate = rate;
        }
    }

    /** SARS tax brackets used in this educational example. */
    static final TaxBracket[] SARS_BRACKETS = {
            new TaxBracket(0,        237100,   0.18),
            new TaxBracket(237100,   370500,   0.26),
            new TaxBracket(370500,   512800,   0.31),
            new TaxBracket(512800,   673000,   0.36),
            new TaxBracket(673000,   857900,   0.39),
            new TaxBracket(857900,   1817000,  0.41),
            new TaxBracket(1817000,  Double.MAX_VALUE, 0.45)
    };

    /**
     * Iterative baseline for progressive tax.
     *
     * @param annualIncome taxable annual income in Rand
     * @return total tax due
     */
    static double calculateTaxIterative(double annualIncome) {
        // Time: O(b) where b is number of brackets; here b=7 so effectively O(1).
        if (annualIncome <= 0) {
            return 0;
        }

        double totalTax = 0;

        for (int bracketIndex = 0; bracketIndex < SARS_BRACKETS.length; bracketIndex++) {
            TaxBracket bracket = SARS_BRACKETS[bracketIndex];

            // If income is above this bracket's lower bound, this bracket contributes tax.
            if (annualIncome > bracket.lowerBound) {
                // Taxable slice is capped at bracket upper bound.
                double taxableSliceTop = Math.min(annualIncome, bracket.upperBound);
                double taxableSlice = taxableSliceTop - bracket.lowerBound;
                totalTax += taxableSlice * bracket.rate;
            } else {
                break;
            }
        }

        return totalTax;
    }

    /**
     * Recursive progressive tax calculation.
     * Starts from highest bracket and recursively accumulates lower-bracket tax.
     *
     * @param annualIncome taxable annual income in Rand
     * @return total tax due
     */
    static double calculateTaxRecursive(double annualIncome) {
        // Time: O(b) where b is number of brackets; recursion depth is at most number of brackets.
        if (annualIncome <= 0) {
            return 0;
        }
        return calculateTaxRecursiveByBracket(annualIncome, SARS_BRACKETS.length - 1);
    }

    /**
     * Recursive helper: computes tax contribution up to a given bracket index.
     *
     * @param annualIncome taxpayer income
     * @param bracketIndex current bracket being evaluated
     * @return tax due up to this bracket
     */
    static double calculateTaxRecursiveByBracket(double annualIncome, int bracketIndex) {
        // Time: O(b) total across recursion chain.
        // Base case: no brackets left.
        if (bracketIndex < 0 || annualIncome <= 0) {
            return 0;
        }

        TaxBracket bracket = SARS_BRACKETS[bracketIndex];

        // If income does not reach this bracket, recurse to the bracket below.
        if (annualIncome <= bracket.lowerBound) {
            return calculateTaxRecursiveByBracket(annualIncome, bracketIndex - 1);
        }

        // Recursive case: this bracket contributes its slice + all lower brackets.
        double taxableSliceTop = Math.min(annualIncome, bracket.upperBound);
        double taxableSlice = taxableSliceTop - bracket.lowerBound;
        double thisBracketTax = taxableSlice * bracket.rate;

        return thisBracketTax + calculateTaxRecursiveByBracket(bracket.lowerBound, bracketIndex - 1);
    }

    /**
     * Classic recursive factorial for recursion fundamentals.
     *
     * @param number non-negative integer
     * @return number!
     */
    static long factorialRecursive(int number) {
        // Time: O(n) - one recursive call per decrement.
        if (number < 0) {
            throw new IllegalArgumentException("Factorial is undefined for negative numbers.");
        }

        // Base case: 0! and 1! are both 1.
        if (number <= 1) {
            return 1;
        }

        // Recursive call: n! = n * (n-1)!
        return number * factorialRecursive(number - 1);
    }

    /**
     * Naive recursive Fibonacci (educational only; intentionally expensive).
     *
     * @param position sequence index (0-based)
     * @return Fibonacci value at position
     */
    static long fibonacciNaive(int position) {
        // Time: O(2^n) - repeated overlapping subproblems.
        if (position < 0) {
            throw new IllegalArgumentException("Fibonacci position must be non-negative.");
        }

        // Base cases.
        if (position <= 1) {
            return position;
        }

        // Recursive tree branches into two calls.
        return fibonacciNaive(position - 1) + fibonacciNaive(position - 2);
    }

    /**
     * Memoized recursive Fibonacci using an array cache.
     *
     * @param position sequence index (0-based)
     * @param memo     cache where -1 means "not yet computed"
     * @return Fibonacci value at position
     */
    static long fibonacciMemoized(int position, long[] memo) {
        // Time: O(n) - each Fibonacci value is computed once and cached.
        if (position < 0) {
            throw new IllegalArgumentException("Fibonacci position must be non-negative.");
        }

        if (position <= 1) {
            return position;
        }

        // Memoization check: reuse previous result instead of recomputing full subtree.
        if (memo[position] != -1) {
            return memo[position];
        }

        memo[position] = fibonacciMemoized(position - 1, memo) + fibonacciMemoized(position - 2, memo);
        return memo[position];
    }

    /**
     * Demonstrates recursion depth risk.
     *
     * @param depthRemaining recursive depth countdown
     * @return recursive call count
     */
    static int demonstrateCallStackDepth(int depthRemaining) {
        // Time: O(d) where d is depthRemaining.
        // Base case prevents unbounded recursion and stack overflow.
        if (depthRemaining == 0) {
            return 0;
        }

        return 1 + demonstrateCallStackDepth(depthRemaining - 1);
    }

    /**
     * Prints one tax scenario comparing iterative and recursive outputs.
     *
     * @param income annual income to test
     */
    static void printTaxScenario(double income) {
        // Time: O(b) because both methods evaluate bracket chain.
        double iterativeTax = calculateTaxIterative(income);
        double recursiveTax = calculateTaxRecursive(income);

        System.out.printf("Income: R%,.2f%n", income);
        System.out.printf("  Iterative tax: R%,.2f%n", iterativeTax);
        System.out.printf("  Recursive tax: R%,.2f%n", recursiveTax);
        System.out.printf("  Match: %b%n", Math.abs(iterativeTax - recursiveTax) < 0.0001);
    }

    /**
     * Story-driven demonstration entry point.
     *
     * @param args unused command-line args
     */
    public static void main(String[] args) {
        System.out.println("=== SARS eFiling Tax Bracket Calculator (Beginner Recursion) ===");

        // Edge case: zero and negative income should return zero tax.
        System.out.println("\n-- Edge Cases: Zero and Negative Income --");
        printTaxScenario(0);
        printTaxScenario(-50000);

        System.out.println("\n-- Typical Income Scenarios --");
        printTaxScenario(180000);
        printTaxScenario(450000);
        printTaxScenario(900000);
        printTaxScenario(2500000);

        System.out.println("\n-- Recursion Fundamentals: Factorial --");
        System.out.println("  5! = " + factorialRecursive(5));
        System.out.println("  10! = " + factorialRecursive(10));

        System.out.println("\n-- Recursion Fundamentals: Fibonacci --");
        int fibPosition = 35;

        long naiveStart = System.nanoTime();
        long naiveValue = fibonacciNaive(fibPosition);
        long naiveMs = (System.nanoTime() - naiveStart) / 1_000_000;

        long[] memo = new long[fibPosition + 1];
        for (int i = 0; i < memo.length; i++) {
            memo[i] = -1;
        }

        long memoStart = System.nanoTime();
        long memoValue = fibonacciMemoized(fibPosition, memo);
        long memoMs = (System.nanoTime() - memoStart) / 1_000_000;

        System.out.println("  Naive Fibonacci(" + fibPosition + ") = " + naiveValue + " in " + naiveMs + " ms");
        System.out.println("  Memoized Fibonacci(" + fibPosition + ") = " + memoValue + " in " + memoMs + " ms");

        System.out.println("\n-- Call Stack Safety Demonstration --");
        int safeDepth = 100;
        System.out.println("  Safe recursive depth handled: " + demonstrateCallStackDepth(safeDepth));
        System.out.println("  Note: very deep recursion can trigger StackOverflowError if no safe base case exists.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - base case: the stopping condition of recursion (e.g., bracketIndex < 0, n <= 1).
 - recursive call: a method calling itself with a smaller input.
 - call stack: each recursive call adds a stack frame until base case returns.
 - factorial: classic linear recursion example, n! = n * (n-1)!
 - Fibonacci: recursion tree example with overlapping subproblems.
 - memoization: cache results to avoid recomputing identical recursive states.
 - stack overflow: happens when recursion depth is too large without timely base-case returns.

 Big-O of implemented operations:
 - calculateTaxIterative: O(b) where b is number of tax brackets.
 - calculateTaxRecursive: O(b) with recursion depth up to bracket count.
 - calculateTaxRecursiveByBracket: O(b) across the full call chain.
 - factorialRecursive: O(n) time, O(n) stack space.
 - fibonacciNaive: O(2^n) time, O(n) stack space.
 - fibonacciMemoized: O(n) time, O(n) stack space and O(n) cache space.
 - demonstrateCallStackDepth: O(d) time and O(d) stack space.

 Interview questions this prepares you for:
 - Why is a base case mandatory in recursive functions?
 - Why is naive Fibonacci exponential while memoized Fibonacci is linear?
 - How do you detect and prevent potential stack overflow in recursion?
 - When is recursion clearer than iteration for business logic like tax brackets?
 - What is the space complexity trade-off of memoization?

 Common mistake and prevention:
 - Mistake: forgetting to reduce the problem toward the base case.
 - Avoided here: each recursive method strictly moves toward termination (index-1, n-1, depth-1).

 When to use recursion vs the common alternative:
 - Use recursion when the problem is naturally hierarchical (trees, divide-and-conquer, bracket chains).
 - Use iteration when recursion depth could be huge or when stack memory must remain minimal.
*/