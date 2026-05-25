# Drakensberg Hiking Trail Mapper (Intermediate)

**Difficulty:** Intermediate  
**Topic:** Recursion + Backtracking on a Trail Graph  
**Core Idea:** Recursively explore every branch, backtrack on dead ends, and avoid cycles with visited-state tracking.

## Storyline
The Drakensberg has a web of connected hiking trails with branches and loops. KZN Wildlife wants a route finder that lists all complete routes from a chosen start point to a target peak. Sibusiso implements this with recursive depth-first search because each junction creates a smaller subproblem: "find all routes from this next junction to the destination." If a branch fails, the algorithm backtracks and tries the next branch. If a cycle appears, visited checks prevent infinite recursion.

## What This Project Demonstrates
- Recursive DFS route discovery
- Base case (destination reached)
- Recursive case (explore neighbors)
- Backtracking (unmark visited on return)
- Cycle handling in recursive graph traversal
- Factorial and Fibonacci (naive vs memoized) as supporting recursion concepts

## How To Run
```powershell
cd "07-Recursion\Drakensberg-Hiking-Trail-Mapper"
javac DrakensbergHikingTrailMapperApp.java
java DrakensbergHikingTrailMapperApp
```

## Expected Learning Outcome
You will understand how recursion and backtracking work together to enumerate all valid routes in branched/cyclic trail networks.
