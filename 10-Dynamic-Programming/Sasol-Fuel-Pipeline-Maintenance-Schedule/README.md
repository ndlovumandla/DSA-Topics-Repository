# Sasol Fuel Pipeline Maintenance Schedule

Difficulty: Intermediate  
Topic: Dynamic Programming (House-Robber Pattern)

## Storyline
Sizwe schedules inspections across Sasol pump stations with one key safety rule: adjacent stations cannot be inspected on the same day. He models the problem as dynamic programming, where best up to station k is max(best up to k-1, best up to k-2 plus current station value). This collapses brute-force exponential branching into linear-time DP.

## What This Program Demonstrates
- Top-down memoized recurrence
- Bottom-up tabulation
- Optimal-plan reconstruction
- Overlapping subproblems and optimal substructure
- Edge cases: empty input, single station, duplicate values

## Run
```powershell
cd "10-Dynamic-Programming\Sasol-Fuel-Pipeline-Maintenance-Schedule"
javac SasolPipelineMaintenanceScheduleApp.java
java SasolPipelineMaintenanceScheduleApp
```
