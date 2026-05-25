# Joburg Water Pipe Network Fault Tracing

Difficulty: Intermediate  
Topic: Graph Traversal (DFS and BFS)

## Storyline
Ayanda models Johannesburg's pipe system as a graph where junctions are vertices and pipe segments are edges. When a burst happens in Sandton, she uses DFS to trace the full connected fault zone. For emergency rerouting from an intake point, she uses BFS to find junctions reachable within a 3-hop limit.

## What This Program Demonstrates
- Undirected adjacency-list graph implementation from scratch
- DFS connected-zone tracing from a fault point
- BFS hop-limited reachability for rerouting
- Visited set handling for cycles
- Queue implementation for BFS
- Edge cases: unknown node, hop limit 0, disconnected zone

## Run
```powershell
cd "09-Graph-Traversal\Joburg-Water-Pipe-Network-Fault-Tracing"
javac JoburgWaterPipeFaultTracingApp.java
java JoburgWaterPipeFaultTracingApp
```
