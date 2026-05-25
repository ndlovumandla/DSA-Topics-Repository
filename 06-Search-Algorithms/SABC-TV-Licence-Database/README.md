# SABC TV Licence Database

**Difficulty:** Beginner  
**Topic:** Search Algorithms — Linear Search · Binary Search  
**Complexity:** Linear O(n) · Binary O(log n)

---

## Storyline

The SABC maintains a database of 8 million TV licence records stored in a sorted flat file. Every day, the enforcement system performs up to 200 000 lookups to check reported unlicensed addresses. Enforcement officer **Bongi** discovers the overnight job takes 14 hours because every single lookup uses a for-loop linear scan through all 8 million records.

The database is already sorted — so Bongi implements **binary search**, which eliminates half the remaining candidates on every comparison. For 8 million records, the worst case drops from 8 000 000 comparisons down to just 23. The overnight job now completes in 4 minutes.

---

## What This Program Demonstrates

| Algorithm | Complexity | Max comparisons on 8 M records |
|---|---|---|
| Linear Search | O(n) | 8 000 000 |
| Binary Search | O(log n) | 23 |

- Both algorithms implemented **from scratch** — no `Arrays.binarySearch()` used
- Comparison counter on every lookup
- Overflow-safe midpoint: `low + (high - low) / 2`
- Edge cases: empty database, single record, licence not found

---

## How to Run

```powershell
cd "06-Search-Algorithms\SABC-TV-Licence-Database"
javac SABCTvLicenceDatabaseApp.java
java SABCTvLicenceDatabaseApp
```

---

## Key Concepts

- **Sorted array required** — binary search only works when elements are in order
- **Divide and conquer** — each step cuts the remaining search space in half
- **Index bounds** — binary search tracks `lowBound` and `highBound`, shrinking them each iteration
- **O(log n) intuition** — doubling the database size adds only one extra comparison
