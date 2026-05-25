# Johannesburg Library Fine System (Advanced)

Difficulty: Advanced  
Topic: Hash Table Internals and HashMap Behavior

## Storyline
Nandi builds an advanced fine system for Johannesburg Public Library, where 180,000 members need fast balance lookups. Her custom hash table supports full operations and tracks load factor to keep average-time performance near O(1). When load factor crosses 0.75, the table resizes and rehashes entries to reduce chain growth. The project also compares selected outputs against Java's built-in HashMap for correctness validation.

## What This Program Demonstrates
- Custom hashing and bucket indexing
- Collision handling via chaining
- put, get, containsKey, remove
- Load-factor threshold and automatic resize/rehash
- Diagnostics: collisions, max chain, used buckets
- Built-in HashMap comparison checks
- Edge cases: empty table, duplicate updates, missing removals/lookups

## Run
```powershell
cd "08-Hash-Tables-Internals\Johannesburg-Library-Fine-System-Advanced"
javac JohannesburgLibraryFineSystemAdvancedApp.java
java JohannesburgLibraryFineSystemAdvancedApp
```
