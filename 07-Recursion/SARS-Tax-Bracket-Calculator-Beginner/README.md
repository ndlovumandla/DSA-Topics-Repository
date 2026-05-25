# SARS Tax Bracket Calculator (Beginner)

**Difficulty:** Beginner  
**Topic:** Recursion  
**Core Idea:** Progressive tax is naturally recursive because each bracket builds on lower brackets.

## Storyline
South Africa's progressive income tax model taxes each income slice at different rates. Lerato is building an eFiling helper for SARS and needs code that is clear to explain to new developers. She implements tax calculation both iteratively and recursively to show they produce identical totals. The recursive method mirrors the math definition directly: tax at this level plus tax below this threshold. The project also includes factorial and Fibonacci examples to explain base cases, recursive calls, call stacks, and memoization.

## What This Project Demonstrates
- Iterative progressive tax calculation
- Recursive progressive tax calculation
- Factorial recursion
- Naive Fibonacci vs memoized Fibonacci
- Stack-depth safety discussion and edge-case handling

## How To Run
```powershell
cd "07-Recursion\SARS-Tax-Bracket-Calculator-Beginner"
javac SarsTaxBracketCalculatorBeginnerApp.java
java SarsTaxBracketCalculatorBeginnerApp
```

## Expected Learning Outcome
You will see exactly how recursion can model bracket-by-bracket tax logic, and why memoization turns Fibonacci from impractical exponential time into linear time.
