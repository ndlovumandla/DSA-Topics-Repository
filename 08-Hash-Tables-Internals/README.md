# 08 - Hash Tables Internals

Three South African-themed projects showing how hash tables work under the hood: hashing, buckets, collisions, chaining, load factor, and O(1) average-time lookups.

## Subprojects

| # | Folder | Difficulty | Main Focus | Storyline |
|---|---|---|---|---|
| 1 | [Johannesburg-Library-Fine-System-Beginner](Johannesburg-Library-Fine-System-Beginner/) | Beginner | Core hash table with chaining and load-factor awareness | Nandi rebuilds library fine lookup for 180,000 members |
| 2 | [Eswatini-Border-Vehicle-Registry](Eswatini-Border-Vehicle-Registry/) | Intermediate | Full custom hash map, plate hashing, collision behavior, good vs poor hash comparison | Precious speeds up border verification for 280,000 vehicles |
| 3 | [Johannesburg-Library-Fine-System-Advanced](Johannesburg-Library-Fine-System-Advanced/) | Advanced | put/get/remove/contains, resize/rehash, diagnostics, built-in HashMap comparison | Nandi builds a production-aware fine lookup engine |

## Complexity Snapshot

| Operation | Average | Worst Case | Why |
|---|---|---|---|
| put | O(1) | O(n) | collisions can create long chains |
| get | O(1) | O(n) | same-bucket chain traversal in worst distribution |
| remove | O(1) | O(n) | must find node inside chain |
| resize + rehash | O(n) | O(n) | all entries are redistributed |

## How To Run

```powershell
# Beginner
cd "Johannesburg-Library-Fine-System-Beginner"
javac JohannesburgLibraryFineSystemBeginnerApp.java ; java JohannesburgLibraryFineSystemBeginnerApp

# Intermediate
cd "../Eswatini-Border-Vehicle-Registry"
javac EswatiniBorderVehicleRegistryApp.java ; java EswatiniBorderVehicleRegistryApp

# Advanced
cd "../Johannesburg-Library-Fine-System-Advanced"
javac JohannesburgLibraryFineSystemAdvancedApp.java ; java JohannesburgLibraryFineSystemAdvancedApp
```
