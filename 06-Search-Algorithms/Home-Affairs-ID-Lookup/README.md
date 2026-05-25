# Home Affairs ID Lookup System

**Difficulty:** Intermediate  
**Topic:** Search Algorithms — Linear Search · Binary Search · Merge Sort (pre-processing)  
**Complexity:** Linear O(n) · Binary O(log n) · Sort pre-processing O(n log n)

---

## Storyline

The Department of Home Affairs holds 60 million South African ID records. Banks and employers need sub-second identity verification. In the 1990s the system used a linear scan — up to 60 million comparisons per query — causing 45-second bank delays.

Developer **Monde** fixes it with a two-step solution: sort the database once with merge sort, then use binary search for every lookup. The sort cost is O(n log n) paid once at startup; every subsequent query costs at most **26 comparisons** regardless of how large the database grows.

---

## What This Program Demonstrates

| Algorithm | Complexity | Comparisons on 60 M records |
|---|---|---|
| Linear Search | O(n) | 60 000 000 |
| Binary Search | O(log n) | 26 |
| Merge Sort (pre-sort) | O(n log n) | paid once |

- Both search algorithms implemented **from scratch**
- Merge sort for database pre-sorting (also from scratch)
- Deceased citizen fraud-detection flag
- Comparison count scaling table across 4 dataset sizes
- Full pipeline: unsorted input → merge sort → batch binary search lookups

---

## How to Run

```powershell
cd "06-Search-Algorithms\Home-Affairs-ID-Lookup"
javac HomeAffairsIdLookupApp.java
java HomeAffairsIdLookupApp
```

---

## Sample Output

```
-- Comparison count scaling (worst-case lookup = last record) --
  DB Size       Linear comparisons   Binary comparisons
  -------------------------------------------------------
  100           100                  7
  1,000         1,000                10
  10,000        10,000               14
  100,000       100,000              17

== Theoretical at 60 million records ==
  Linear search worst case : 60,000,000 comparisons
  Binary search worst case : 26 comparisons (log₂ 60,000,000)

Monde's result: verification time drops from 45 seconds to under 1 ms.
```

---

## Key Concepts

- **Sort-then-search trade-off** — pay O(n log n) once to unlock O(log n) for all future lookups
- **Lexicographic ID comparison** — 13-digit zero-padded SA IDs sort correctly with `String.compareTo()`
- **Deceased flag** — binary search finds the record; the `isAlive` field catches ghost-identity fraud
- **O(log n) intuition** — doubling the database from 60 M to 120 M adds only one more comparison
