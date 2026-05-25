# Johannesburg Library Fine System (Beginner)

Difficulty: Beginner  
Topic: Hash Tables and HashMap Internals

## Storyline
Johannesburg Public Library has 180,000 members, and librarians must instantly show outstanding balances when books are returned. A linear search through all member records is too slow for daily desk operations. Nandi rebuilds the system using a hash table where membership numbers map to bucket indices. Collisions are handled through chaining (linked list per bucket), and load factor is tracked to explain how speed declines when buckets become crowded.

## What This Program Demonstrates
- Custom hashCode-like key hashing
- equals-style key comparison in collisions
- Chaining with linked-list nodes
- Bucket distribution and load factor reporting
- O(1) average lookup and O(n) worst-case collision behavior
- Edge cases: empty table, single entry, duplicate key update, not found

## Run
```powershell
cd "08-Hash-Tables-Internals\Johannesburg-Library-Fine-System-Beginner"
javac JohannesburgLibraryFineSystemBeginnerApp.java
java JohannesburgLibraryFineSystemBeginnerApp
```
