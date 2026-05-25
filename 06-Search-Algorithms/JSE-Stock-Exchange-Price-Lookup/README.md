# JSE Stock Exchange Price Lookup

**Difficulty:** Advanced  
**Topic:** Search Algorithms — Linear Search · Binary Search (iterative + recursive) · Floor/Ceiling Search  
**Complexity:** Linear O(n) · Binary O(log n) · Sorted insert O(n)

---

## Storyline

The JSE's trading platform handles price lookups for 3 200 listed instruments — equities, ETFs, and bonds. Every broker order requires an instant ticker-to-price resolution; even microseconds of latency matter at high trading volumes. A junior developer's linear scan was fine at 400 instruments but grew noticeably slow at 3 200.

Systems developer **Ayesha** replaces the linear scan with binary search on the alphabetically sorted ticker register. The worst case drops from **3 200 comparisons to 12**. She also implements floor/ceiling search variants, a sorted-insert mechanism to keep the register in order, and validates the result against Java's `Arrays.binarySearch()` to confirm identical output.

---

## What This Program Demonstrates

| Algorithm | Complexity | Space | Notes |
|---|---|---|---|
| Linear Search | O(n) | O(1) | Original implementation |
| Binary Search (iterative) | O(log n) | O(1) | Ayesha's fix — production choice |
| Binary Search (recursive) | O(log n) | O(log n) | Same logic via call stack |
| Floor Search | O(log n) | O(1) | Largest ticker ≤ query |
| Ceiling Search | O(log n) | O(1) | Smallest ticker ≥ query |
| Sorted Insert | O(n) | O(1) | Binary find position + O(n) shift |
| `Arrays.binarySearch()` | O(log n) | O(1) | Built-in validation — outputs match |

---

## Advanced Concepts Demonstrated

- **Iterative vs recursive binary search** — same complexity, different space trade-off
- **Floor and ceiling variants** — track a running candidate while narrowing the window
- **Sorted array insert** — use binary search to find the slot, then shift to maintain sort order
- **Overflow-safe midpoint** — `low + (high - low) / 2` instead of `(low + high) / 2`
- **Built-in comparison** — `Arrays.binarySearch()` result verified to match our implementation

---

## How to Run

```powershell
cd "06-Search-Algorithms\JSE-Stock-Exchange-Price-Lookup"
javac JSEPriceLookupApp.java
java JSEPriceLookupApp
```

---

## Sample Output (abridged)

```
=== JSE Trading Platform — Securities Price Lookup Benchmark ===

  Ticker: AA3199
  [Linear scan            ]  Comparisons: 3200  Time: 123,456 ns
  [Binary iterative       ]  Comparisons:   12  Time:   1,234 ns
  [Binary recursive       ]  Comparisons:   12  Time:   1,456 ns

-- Validation against Arrays.binarySearch() --
  Ticker AA0000   our index:    0  Arrays.binarySearch:    0  match: true
  Ticker ZZZTOP   our index:   -1  Arrays.binarySearch:  -3201  match: true

== Comparison count at 3 200 instruments ==
  Linear search worst case: 3,200 comparisons
  Binary search worst case: 12 comparisons (log₂ 3,200)
```

---

## Key Concepts

- **O(log n) intuition** — doubling the register from 3 200 to 6 400 adds only 1 comparison
- **Floor/ceiling** — binary search extensions that answer "nearest below" and "nearest above" queries
- **Sorted array trade-off** — O(n) to insert but O(log n) to search; use when reads >> writes
- **Recursive vs iterative** — iterative preferred in production (no call-stack overhead)
