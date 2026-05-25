# South African Road Freight Network

Difficulty: Advanced  
Topic: Graph Traversal on Directed Weighted Graphs

## Storyline
Lwazi models Imperial Logistics routes as a directed weighted graph where cities are vertices and road links are weighted edges in kilometres. She uses BFS to determine cities reachable within a hop limit for driver-hours compliance. She uses DFS to test route existence while avoiding cities closed for road construction. The network is stored as an adjacency list to reduce memory usage on sparse real road graphs.

## What This Program Demonstrates
- Directed weighted adjacency-list graph from scratch
- BFS hop-limited reachability
- DFS route existence with closure constraints
- Custom queue and stack implementations
- Visited-set management
- Built-in graph-equivalence check using Java collections (verification only)
- Edge cases: unknown city, negative hop limit, blocked routes

## Run
```powershell
cd "09-Graph-Traversal\South-African-Road-Freight-Network"
javac SouthAfricanRoadFreightNetworkApp.java
java SouthAfricanRoadFreightNetworkApp
```
