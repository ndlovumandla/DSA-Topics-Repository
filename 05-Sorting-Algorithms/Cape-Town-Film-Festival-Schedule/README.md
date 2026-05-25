# Cape Town Film Festival Screening Schedule

**Difficulty:** Advanced  
**Topic:** Sorting Algorithms — Quicksort · Merge Sort · Bubble Sort · Multi-Key Comparator  
**Complexity:** Quicksort O(n log n) average · Merge O(n log n) · Bubble O(n²)

---

## Storyline

The Cape Town International Film Festival screens 240 films across 14 venues over 11 days. The printed programme booklet must list every screening sorted by **date → venue → start time** — a three-key sort. Programme coordinator **Fatima** sends developer **Ravi** a completely unsorted schedule two days before print deadline (films added in sponsor-confirmation order).

Ravi implements **quicksort** as the primary algorithm with a custom multi-key comparator, explains pivot selection and partition logic in detail, compares the result against `Arrays.sort()`, and proves the outputs are identical.

---

## What This Program Demonstrates

| Algorithm | Complexity | Notes |
|---|---|---|
| Bubble Sort | O(n²) | Baseline for comparison |
| Merge Sort | O(n log n) | Stable, guaranteed; O(n) space |
| Quicksort | O(n log n) avg | Primary — median-of-three pivot |
| Java Arrays.sort() | O(n log n) | Dual-pivot quicksort; output verified to match |

**Multi-key comparator sorts by:**
1. Festival day (int)
2. Venue name (String, lexicographic)
3. Start time in minutes (int)

---

## Advanced Concepts Demonstrated

- **Lomuto partition scheme** — boundary-pointer scan, pivot placement after one pass
- **Median-of-three pivot strategy** — avoids O(n²) worst case on sorted/reverse-sorted input
- **Recursive divide-and-conquer** — full recursion tree explained in comments
- **Stable vs unstable sort** — merge sort is stable; quicksort is not
- **Built-in comparison** — `Arrays.sort()` produces identical output to the custom quicksort

---

## How to Run

```powershell
cd "05-Sorting-Algorithms\Cape-Town-Film-Festival-Schedule"
javac CapeTownFilmFestivalApp.java
java CapeTownFilmFestivalApp
```

---

## Sample Output (abridged)

```
=== Cape Town International Film Festival — Screening Schedule Sorter ===

-- Quicksort O(n log n) average — Ravi's choice --
  Comparisons: 1,821
  Sorted: true

-- Java Arrays.sort() (dual-pivot quicksort) --
  Sorted: true
  Output matches our quicksort: true

-- Final printed programme (first 12 lines) --
  -------------------------------------------------------------------------
  Day 1   Artscape Theatre             09:00  Ubuntu Rising
  Day 1   Artscape Theatre             12:00  Kalk Bay Dreams
  Day 1   Baxter Theatre Centre        10:00  Franschhoek Summer
  ...

== Complexity comparison for 240 screening slots ==
  Bubble sort           comparisons:  28,261  (O(n²))
  Merge sort            comparisons:   1,497  (O(n log n))
  Quicksort             comparisons:   1,821  (O(n log n))
```

---

## Key Concepts

- **Pivot** — the element chosen as the dividing point in quicksort; all other elements end up on one side
- **Partition** — one O(n) scan that places every element on the correct side of the pivot
- **Median-of-three** — inspect first, middle, and last elements; use the median to reduce worst-case risk
- **Multi-key comparator** — one compare function that chains date → venue → time decisions
- **Stable sort** — merge sort guarantees equal-keyed slots preserve insertion order; quicksort does not
