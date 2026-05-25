# Comrades Marathon Results Board

**Difficulty:** Intermediate  
**Topic:** Sorting Algorithms — Bubble Sort · Insertion Sort · Selection Sort · Merge Sort  
**Complexity:** Bubble/Selection O(n²) · Insertion O(n) best · Merge O(n log n)

---

## Storyline

The Comrades Marathon is the world's largest ultra-marathon with up to 25 000 finishers. The results board at the finish line must display every runner's name and chip time in ascending order — updated live as athletes cross the line. In the early hours with only a handful of finishers, insertion sort is perfect: the list is nearly sorted, and each new runner slots into position with minimal work. But by midday when thousands finish in a rush, a faster algorithm is needed.

Timing system developer **Yusuf** implements all four classic algorithms on the same finisher dataset, measuring comparison counts as the list grows from 10 to 5 000 entries, and proves exactly why merge sort wins once more than a few hundred runners are on the board.

---

## What This Program Demonstrates

| Algorithm | Complexity | Best Case | Notes |
|---|---|---|---|
| Bubble Sort | O(n²) | O(n) | Early-exit when already sorted |
| Insertion Sort | O(n²) | O(n) | Ideal for near-sorted live updates |
| Selection Sort | O(n²) | O(n²) | Cannot exit early |
| Merge Sort | O(n log n) | O(n log n) | Stable, guaranteed performance |

- All four algorithms implemented **from scratch** — no `java.util` sorting used
- Comparison counters per algorithm for each dataset size
- Benchmarks across four dataset sizes: 10 · 100 · 1 000 · 5 000
- Edge cases: empty list, single finisher, duplicate (tied) finish times
- Stable merge preserves tied-time ordering

---

## How to Run

```powershell
cd "05-Sorting-Algorithms\Comrades-Marathon-Results-Board"
javac ComradesMarathonResultsApp.java
java ComradesMarathonResultsApp
```

---

## Sample Output (abridged)

```
=== Comrades Marathon Results Board — Sorting Algorithm Benchmark ===

-- Edge case: tied finish times --
  Pos   Name                      Time
  1     Sipho Mokoena             8:40:00
  2     Thabo Nkosi               8:45:00
  3     Amahle Zulu               8:45:00

══════════════════════════════════════════════════
 Dataset size: 1000 finishers
══════════════════════════════════════════════════
  Bubble sort          comparisons:    499,500  sorted: true
  Insertion sort       comparisons:    248,921  sorted: true
  Selection sort       comparisons:    499,500  sorted: true
  Merge sort           comparisons:      8,716  sorted: true
```

---

## Key Concepts

- **Insertion sort is O(n) best case** — when each new runner is faster than all previous finishers (early race), no shifting is needed at all
- **Merge sort stable property** — runners with identical chip times keep their original order
- **O(n log n) vs O(n²)** — for 25 000 finishers: ~375M ops vs ~625M; merge sort wins decisively
