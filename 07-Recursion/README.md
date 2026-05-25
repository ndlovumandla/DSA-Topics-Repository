# 07 - Recursion

Three South African-themed recursion projects that move from fundamentals (base case and recursive call) to backtracking and memoized recursive optimization.

## Subprojects

| # | Folder | Difficulty | Main Concepts | Storyline |
|---|---|---|---|---|
| 1 | [SARS-Tax-Bracket-Calculator-Beginner](SARS-Tax-Bracket-Calculator-Beginner/) | Beginner | Base case, recursive bracket chain, factorial, Fibonacci, memoization | Lerato builds a beginner-friendly SARS eFiling tax calculator and compares iterative vs recursive tax logic |
| 2 | [Drakensberg-Hiking-Trail-Mapper](Drakensberg-Hiking-Trail-Mapper/) | Intermediate | Recursive DFS, backtracking, cycle handling, call stack | Sibusiso maps all possible hiking routes from a start camp to a destination peak |
| 3 | [SARS-Tax-Bracket-Calculator-Advanced](SARS-Tax-Bracket-Calculator-Advanced/) | Advanced | Memoized recursion, profiling, built-in equivalence check, stack-safety analysis | Lerato builds a production-aware advanced recursive SARS tax calculator |

## Complexity Snapshot

| Concept / Operation | Complexity |
|---|---|
| Factorial recursion | O(n) |
| Naive Fibonacci | O(2^n) |
| Memoized Fibonacci | O(n) |
| Recursive progressive tax (fixed bands) | O(b), where b = number of bands |
| Recursive route discovery with backtracking | Varies; can be exponential in number of branches |

## How To Run

```powershell
# Beginner
cd "SARS-Tax-Bracket-Calculator-Beginner"
javac SarsTaxBracketCalculatorBeginnerApp.java ; java SarsTaxBracketCalculatorBeginnerApp

# Intermediate
cd "../Drakensberg-Hiking-Trail-Mapper"
javac DrakensbergHikingTrailMapperApp.java ; java DrakensbergHikingTrailMapperApp

# Advanced
cd "../SARS-Tax-Bracket-Calculator-Advanced"
javac SarsTaxBracketCalculatorAdvancedApp.java ; java SarsTaxBracketCalculatorAdvancedApp
```
