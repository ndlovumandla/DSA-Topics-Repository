# 06 — Search Algorithms

Three South African-themed projects that implement linear search and binary search from scratch, benchmarked side-by-side to demonstrate the real-world impact of O(n) versus O(log n).

---

## Subprojects

| # | Folder | Difficulty | Algorithms | Story |
|---|---|---|---|---|
| 1 | [SABC-TV-Licence-Database](SABC-TV-Licence-Database/) | Beginner | Linear · Binary | Bongi cuts the SABC's 14-hour enforcement job to 4 minutes by replacing a linear scan with binary search on 8 million licence records |
| 2 | [Home-Affairs-ID-Lookup](Home-Affairs-ID-Lookup/) | Intermediate | Linear · Binary · Merge Sort (pre-sort) | Monde reduces 45-second bank ID verifications to under 1 ms — sort once, search in 26 comparisons forever |
| 3 | [JSE-Stock-Exchange-Price-Lookup](JSE-Stock-Exchange-Price-Lookup/) | Advanced | Linear · Binary (iterative + recursive) · Floor · Ceiling · Sorted insert | Ayesha cuts broker order latency from 3 200 comparisons to 12, with floor/ceiling variants and `Arrays.binarySearch()` validation |

---

## Complexity Reference

| Algorithm | Best | Average | Worst | Space | Pre-condition |
|---|---|---|---|---|---|
| Linear Search | O(1) | O(n) | O(n) | O(1) | None |
| Binary Search | O(1) | O(log n) | O(log n) | O(1) iterative · O(log n) recursive | Array must be sorted |
| Floor Search | O(1) | O(log n) | O(log n) | O(1) | Array must be sorted |
| Ceiling Search | O(1) | O(log n) | O(log n) | O(1) | Array must be sorted |
| Sorted Insert | O(1) | O(n) | O(n) | O(1) | Array must be sorted |

---

## Core Concepts Across All Three Projects

- **Linear search** — scan every element from the start; works on unsorted data; O(n) always
- **Binary search** — requires sorted data; halves the search range each step; O(log n) always
- **Divide and conquer** — binary search is the simplest divide-and-conquer algorithm: inspect midpoint, discard one half
- **Index bounds** — binary search shrinks `lowBound` and `highBound` each iteration until the window collapses
- **Overflow-safe midpoint** — `low + (high - low) / 2` instead of `(low + high) / 2`
- **Floor/ceiling** — binary search variants that find the nearest element below or above a target
- **Sort-then-search trade-off** — pay O(n log n) once at startup to unlock O(log n) for every future lookup

---

## O(n) vs O(log n) at Scale

| Database size | Linear worst case | Binary worst case |
|---|---|---|
| 1 000 | 1 000 | 10 |
| 1 000 000 | 1 000 000 | 20 |
| 8 000 000 | 8 000 000 | 23 |
| 60 000 000 | 60 000 000 | 26 |

---

## How to Run All Three

```powershell
# Beginner
cd "SABC-TV-Licence-Database"
javac SABCTvLicenceDatabaseApp.java ; java SABCTvLicenceDatabaseApp

# Intermediate
cd "../Home-Affairs-ID-Lookup"
javac HomeAffairsIdLookupApp.java ; java HomeAffairsIdLookupApp

# Advanced
cd "../JSE-Stock-Exchange-Price-Lookup"
javac JSEPriceLookupApp.java ; java JSEPriceLookupApp
```
