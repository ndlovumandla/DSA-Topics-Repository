# 10 - Dynamic Programming

Three South African-themed projects that demonstrate dynamic programming patterns from beginner tabulation/memoization to advanced optimization and reconstruction.

## Subprojects

| # | Folder | Difficulty | Main Focus | Storyline |
|---|---|---|---|---|
| 1 | [Comrades-Entrant-Seeding-System-Beginner](Comrades-Entrant-Seeding-System-Beginner/) | Beginner | Top-down memoization and bottom-up tabulation | Yusuf allocates Comrades entrants to reduce congestion under capacity limits |
| 2 | [Sasol-Fuel-Pipeline-Maintenance-Schedule](Sasol-Fuel-Pipeline-Maintenance-Schedule/) | Intermediate | House-robber DP recurrence and plan reconstruction | Sizwe schedules non-adjacent pump-station inspections to maximize completion |
| 3 | [Comrades-Entrant-Seeding-System-Advanced](Comrades-Entrant-Seeding-System-Advanced/) | Advanced | Full DP solve, reconstruction, and built-in equivalence validation | Yusuf builds a production-aware seeding optimizer with diagnostics |

## Complexity Snapshot

| DP Pattern | Brute Force | DP Complexity |
|---|---|---|
| Knapsack-style allocation | Exponential | O(n*m) |
| House-robber sequence optimization | O(2^n) | O(n) |
| Fibonacci (conceptual comparison) | O(2^n) naive | O(n) memoized/tabulated |

## How To Run

```powershell
# Beginner
cd "Comrades-Entrant-Seeding-System-Beginner"
javac ComradesEntrantSeedingBeginnerApp.java ; java ComradesEntrantSeedingBeginnerApp

# Intermediate
cd "../Sasol-Fuel-Pipeline-Maintenance-Schedule"
javac SasolPipelineMaintenanceScheduleApp.java ; java SasolPipelineMaintenanceScheduleApp

# Advanced
cd "../Comrades-Entrant-Seeding-System-Advanced"
javac ComradesEntrantSeedingAdvancedApp.java ; java ComradesEntrantSeedingAdvancedApp
```
