# 09 - Graph Traversal

Three South African-themed projects showing graph traversal fundamentals and practical routing analysis with BFS and DFS.

## Subprojects

| # | Folder | Difficulty | Main Focus | Storyline |
|---|---|---|---|---|
| 1 | [Metrorail-Cape-Town-Network](Metrorail-Cape-Town-Network/) | Beginner | Undirected adjacency list, BFS shortest-stop path, DFS exploration | Ravi builds a journey planner for Cape Town Metrorail |
| 2 | [Joburg-Water-Pipe-Network-Fault-Tracing](Joburg-Water-Pipe-Network-Fault-Tracing/) | Intermediate | DFS fault-zone tracing and BFS hop-limited reroute reachability | Ayanda traces pipe-fault impact and intake reroute options |
| 3 | [South-African-Road-Freight-Network](South-African-Road-Freight-Network/) | Advanced | Directed weighted adjacency list, BFS hop compliance, DFS route existence with closures | Lwazi models freight routing with road-closure constraints |

## Complexity Snapshot

| Operation | Complexity |
|---|---|
| BFS traversal | O(V + E) |
| DFS traversal | O(V + E) |
| Adjacency-list storage | O(V + E) |
| Adjacency-matrix storage (for comparison) | O(V^2) |

## How To Run

```powershell
# Beginner
cd "Metrorail-Cape-Town-Network"
javac MetrorailCapeTownNetworkApp.java ; java MetrorailCapeTownNetworkApp

# Intermediate
cd "../Joburg-Water-Pipe-Network-Fault-Tracing"
javac JoburgWaterPipeFaultTracingApp.java ; java JoburgWaterPipeFaultTracingApp

# Advanced
cd "../South-African-Road-Freight-Network"
javac SouthAfricanRoadFreightNetworkApp.java ; java SouthAfricanRoadFreightNetworkApp
```
