# Binary Search Tree: Kruger National Park Animal Census

## Storyline
Every ten years, SANParks conducts a full animal census of Kruger National Park.
Rangers submit species sightings from across the park in no predictable order, and each sighting carries a unique species code.
At the end of the census, the ecologist needs a sorted report of all species codes without running a separate sort algorithm.
Developer Nomsa uses a binary search tree so each sighting is inserted into the correct position as it arrives.
An in-order traversal then produces the final sorted report directly.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| BST | `SpeciesCensusTree` |
| root | `parkRoot` |
| left/right child | `leftChild` and `rightChild` |
| in-order traversal | `printSortedSpeciesReport()` |
| pre-order | `printPreOrderTraversal()` |
| post-order | `printPostOrderTraversal()` |
| recursion | recursive helper methods |
| search O(log n) | `containsSpeciesCode()` in a balanced tree |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\04-Binary-Search-Tree\Kruger-National-Park-Animal-Census
javac KrugerAnimalCensusApp.java
java KrugerAnimalCensusApp
```

## Difficulty
Beginner
