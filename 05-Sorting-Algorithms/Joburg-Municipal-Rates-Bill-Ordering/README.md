# Joburg Municipal Rates Bill Ordering

**Difficulty:** Beginner  
**Topic:** Sorting Algorithms — Bubble Sort · Selection Sort · Merge Sort  
**Complexity:** Bubble O(n²) · Selection O(n²) · Merge O(n log n)

---

## Storyline

The City of Johannesburg generates 1.4 million rates bills every month. Before printing can begin at 4 AM, all bills must be sorted by postal code so the post office can bundle them for its bulk-mail discount. IT manager **Dineo's** team has been using bubble sort since 2003 — a decision that now takes 3.5 hours for 1.4 million records and regularly misses the print-queue deadline.

Developer **Siphamandla** is brought in to fix the problem. He implements bubble sort, selection sort, and merge sort on the same billing dataset, benchmarks all three, and demonstrates exactly why merge sort's O(n log n) complexity cuts the overnight billing run from 3.5 hours to 11 minutes.

---

## What This Program Demonstrates

| Algorithm | Complexity | Notes |
|---|---|---|
| Bubble Sort | O(n²) | Original legacy algorithm used since 2003 |
| Selection Sort | O(n²) | Fewer swaps than bubble but same comparisons |
| Merge Sort | O(n log n) | Divide-and-conquer — Siphamandla's fix |

- All three algorithms implemented **from scratch** with no `java.util` sorting utilities
- Comparison and swap counters per algorithm
- Edge cases: empty array, single element, duplicate postal codes
- Benchmark timing on a 5 000-bill dataset

---

## How to Run

```powershell
cd "05-Sorting-Algorithms\Joburg-Municipal-Rates-Bill-Ordering"
javac JoburgRatesBillOrderingApp.java
java JoburgRatesBillOrderingApp
```

---

## Sample Output

```
=== Joburg Municipal Rates Bill Ordering System ===

-- Edge case: empty billing run --
  All sorts handled empty array correctly.

-- Edge case: single-bill billing run --
  All sorts handled single-element array correctly.

-- Unsorted billing run sample (first 5 bills) --
  postal 1302  account ACC-00001
  postal 2603  account ACC-00002
  ...

-- Bubble sort O(n²) --
  Swaps performed: 6239871
  Sorted correctly: true

-- Merge sort O(n log n) --
  Sorted correctly: true

== Complexity comparison for 5000 bills ==
  Bubble sort:    O(n²)      ~25000000 ops
  Merge sort:     O(n log n) ~60000 ops
```

---

## Key Concepts

- **Bubble sort** — compares adjacent pairs and swaps them repeatedly until sorted
- **Selection sort** — scans for the minimum element and swaps it to the front
- **Merge sort** — divide the array in half, sort each half, merge back together
- **O(n²)** — two nested loops; for 1.4 M records that is 1.96 trillion operations
- **O(n log n)** — only ~30 M operations for the same 1.4 M records
