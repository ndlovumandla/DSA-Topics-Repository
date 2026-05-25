# Binary Search Tree: National Library of South Africa Catalogue

## Storyline
The National Library of South Africa in Pretoria holds more than a million titles.
The old alphabetical catalogue was searchable, but it still required scanning far too many entries as the collection grew.
Developer Mandla rebuilds the catalogue using a binary search tree keyed on ISBN so each new book lands in the correct left/right position as it is inserted.
That means the catalogue stays ordered during insertion, and an in-order traversal can print books in sorted ISBN order without a separate sort step.
This approach also supports fast average-case search using recursion down from the root.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| BST | `LibraryCatalogueTree` |
| root | `libraryRoot` |
| left/right child | `leftChild` and `rightChild` |
| in-order traversal | `printSortedCatalogue()` |
| pre-order | `printPreOrderCatalogue()` |
| post-order | `printPostOrderCatalogue()` |
| recursion | recursive helper methods |
| search O(log n) | `containsIsbn()` in a balanced tree |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\04-Binary-Search-Tree\National-Library-of-South-Africa-Catalogue
javac NationalLibraryCatalogueApp.java
java NationalLibraryCatalogueApp
```

## Difficulty
Intermediate
