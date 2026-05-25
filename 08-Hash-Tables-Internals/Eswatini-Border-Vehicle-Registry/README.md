# Eswatini Border Vehicle Registry (Intermediate)

Difficulty: Intermediate  
Topic: Hash Tables and HashMap Internals

## Storyline
At the Ngwenya border, officers must verify vehicle registration quickly while handling peak traffic. Precious replaces linear scanning with a custom hash map keyed by licence plate. The project demonstrates a full hash function, bucket indexing, chaining for collision handling, and a side-by-side comparison against a deliberately poor hash function to show how collisions degrade performance.

## What This Program Demonstrates
- Custom plate hashing logic and bucket mapping
- Collision handling by linked-list chaining
- Duplicate key update behavior
- Load factor and chain-length metrics
- Good hash vs poor hash distribution comparison
- Border verification flow with status outputs

## Run
```powershell
cd "08-Hash-Tables-Internals\Eswatini-Border-Vehicle-Registry"
javac EswatiniBorderVehicleRegistryApp.java
java EswatiniBorderVehicleRegistryApp
```
