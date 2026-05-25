# Binary Search Tree: Kruger National Park Advanced Species Registry

## Storyline
Every ten years, SANParks conducts a full animal census of Kruger National Park.
Rangers submit species sightings in unpredictable order, and the ecologist needs search, deletion, and sorted reporting in one structure.
Developer Nomsa uses a binary search tree keyed by species code so each sighting is placed into the correct left or right branch as it arrives.
This supports fast average-case search, simple min/max lookup, and an in-order traversal that prints the final sorted report directly.
The advanced version also shows deletion, duplicate counting, and tree height analysis so students can see the full BST lifecycle.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| BST | `SpeciesRegistryTree` |
| root | `parkRoot` |
| left/right child | `leftChild` and `rightChild` |
| in-order traversal | `printInOrderReport()` |
| pre-order | `printPreOrderReport()` |
| post-order | `printPostOrderReport()` |
| recursion | recursive helper methods |
| search O(log n) | `containsSpecies()` in a balanced tree |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\04-Binary-Search-Tree\Kruger-National-Park-Animal-Census-Advanced
javac KrugerAdvancedSpeciesRegistryApp.java
java KrugerAdvancedSpeciesRegistryApp
```

## Difficulty
Advanced
