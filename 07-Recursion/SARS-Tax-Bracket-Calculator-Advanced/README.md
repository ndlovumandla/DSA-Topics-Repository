# SARS Tax Bracket Calculator (Advanced)

**Difficulty:** Advanced  
**Topic:** Recursion + Memoization + Comparative Validation  
**Core Idea:** Progressive-tax recursion with threshold caching, profiled and cross-checked against iterative and built-in stream logic.

## Storyline
Lerato is building an advanced SARS eFiling calculator for progressive tax computation. Because each bracket depends on the tax accumulated in lower brackets, recursion maps directly to the mathematics. She implements iterative, naive recursive, and memoized recursive variants, then profiles calls and timings. To validate correctness, she also compares results against a built-in Java stream reduction implementation. The project demonstrates both practical tax calculation and core recursion ideas like call stack growth, base cases, and stack-overflow risk.

## What This Project Demonstrates
- Iterative bracket computation
- Recursive bracket computation
- Memoized recursive threshold caching
- Built-in stream comparison for validation
- Factorial and Fibonacci (naive vs memoized)
- Edge-case coverage (zero and negative income)

## How To Run
```powershell
cd "07-Recursion\SARS-Tax-Bracket-Calculator-Advanced"
javac SarsTaxBracketCalculatorAdvancedApp.java
java SarsTaxBracketCalculatorAdvancedApp
```

## Expected Learning Outcome
You will understand how to design recursion that is both mathematically elegant and production-aware, including caching repeated subproblems and verifying results against an independent implementation.
