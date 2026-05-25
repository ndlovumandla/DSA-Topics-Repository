# 05 — Sorting Algorithms

Three South African-themed projects that implement every major sorting algorithm from scratch in Java, benchmarked and compared side-by-side.

---

## Subprojects

| # | Folder | Difficulty | Algorithms | Story |
|---|---|---|---|---|
| 1 | [Joburg-Municipal-Rates-Bill-Ordering](Joburg-Municipal-Rates-Bill-Ordering/) | Beginner | Bubble · Selection · Merge | Siphamandla cuts the City of Joburg's overnight billing run from 3.5 hours to 11 minutes |
| 2 | [Comrades-Marathon-Results-Board](Comrades-Marathon-Results-Board/) | Intermediate | Bubble · Insertion · Selection · Merge | Yusuf builds a live-sorted results board for 25 000 Comrades finishers |
| 3 | [Cape-Town-Film-Festival-Schedule](Cape-Town-Film-Festival-Schedule/) | Advanced | Bubble · Merge · Quicksort · Multi-key comparator | Ravi sorts 240 film screenings across 14 venues by date → venue → time using quicksort |

---

## Complexity Reference

| Algorithm | Best | Average | Worst | Space | Stable |
|---|---|---|---|---|---|
| Bubble Sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection Sort | O(n²) | O(n²) | O(n²) | O(1) | No |
| Insertion Sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quicksort | O(n log n) | O(n log n) | O(n²) | O(log n) | No |

---

## Core Concepts Across All Three Projects

- **Comparison** — every time two elements are tested to determine their relative order
- **Swap** — every time two elements exchange positions in the array
- **O(n²) algorithms** — two nested loops; impractical for large datasets
- **O(n log n) algorithms** — divide-and-conquer; each level of recursion processes n elements, and there are log n levels
- **Stable sort** — preserves the original order of equal-valued elements (important for multi-key sorts and live updates with tied values)
- **Pivot** — the chosen dividing element in quicksort; good pivot selection is critical for avoiding O(n²) worst case
- **Multi-key comparator** — a single compare function that chains multiple sort criteria (date → venue → time)

---

## How to Run All Three

```powershell
# Beginner
cd "Joburg-Municipal-Rates-Bill-Ordering"
javac JoburgRatesBillOrderingApp.java ; java JoburgRatesBillOrderingApp

# Intermediate
cd "../Comrades-Marathon-Results-Board"
javac ComradesMarathonResultsApp.java ; java ComradesMarathonResultsApp

# Advanced
cd "../Cape-Town-Film-Festival-Schedule"
javac CapeTownFilmFestivalApp.java ; java CapeTownFilmFestivalApp
```
