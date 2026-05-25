/*
 The National Library of South Africa in Pretoria holds more than a million titles, and its catalogue
 team needs to find any book quickly even as new works are added every day. The old alphabetical list
 was sorted, but searching it still required too many comparisons as the collection grew. Developer
 Mandla chose a binary search tree keyed on ISBN because each new book can be placed to the left or
 right of the current node based on a simple comparison. That means the catalogue stays ordered during
 insertion, and an in-order traversal can later print the books in ascending ISBN order without a
 separate sort step. The BST is the right tool here because it gives fast average-case search and
 supports sorted reporting using recursion directly over the tree structure.
*/

/**
 * Demonstrates a National Library catalogue built with a custom binary search tree.
 * Books are keyed by ISBN, searched recursively, and printed in sorted order without sorting methods.
 */
public class NationalLibraryCatalogueApp {

    /**
     * Represents one book entry in the catalogue tree.
     * Each node stores the ISBN, title, copy count, and child links.
     */
    static class BookCatalogueNode {
        long isbn;
        String title;
        int copyCount;
        BookCatalogueNode leftChild;
        BookCatalogueNode rightChild;

        /**
         * Creates a new catalogue node for one ISBN.
         *
         * @param isbn unique book identifier used as the BST key
         * @param title title of the book
         */
        BookCatalogueNode(long isbn, String title) {
            // Time: O(1) — constant field assignments.
            this.isbn = isbn;
            this.title = title;
            this.copyCount = 1;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    /**
     * Binary search tree that stores the library's books by ISBN.
     * Smaller ISBNs go left; larger ISBNs go right.
     */
    static class LibraryCatalogueTree {
        private BookCatalogueNode libraryRoot;
        private int uniqueBookCount;
        private int totalCopyCount;

        /**
         * Creates an empty catalogue before books are digitised.
         */
        LibraryCatalogueTree() {
            // Time: O(1) — initialize references and counters.
            this.libraryRoot = null;
            this.uniqueBookCount = 0;
            this.totalCopyCount = 0;
        }

        /**
         * Checks whether the catalogue has no entries yet.
         *
         * @return true if the tree is empty
         */
        boolean isEmpty() {
            // Time: O(1) — one pointer check.
            return libraryRoot == null;
        }

        /**
         * Adds a book to the catalogue in BST order.
         * Duplicate ISBNs are treated as additional copies of the same book.
         *
         * @param isbn book ISBN used for tree ordering
         * @param title book title
         */
        void addBook(long isbn, String title) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            libraryRoot = insertRecursively(libraryRoot, isbn, title);
            totalCopyCount++;
        }

        /**
         * Looks up a book by ISBN.
         *
         * @param isbn book ISBN to search for
         * @return true if the ISBN exists in the catalogue
         */
        boolean containsIsbn(long isbn) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            return searchRecursively(libraryRoot, isbn) != null;
        }

        /**
         * Returns the matching book node for an ISBN.
         *
         * @param isbn book ISBN to search for
         * @return node or null if missing
         */
        BookCatalogueNode findBook(long isbn) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            return searchRecursively(libraryRoot, isbn);
        }

        /**
         * Prints all books in ascending ISBN order.
         */
        void printSortedCatalogue() {
            // Time: O(n) — every node is visited once.
            System.out.println("\nSorted catalogue (in-order traversal):");
            if (isEmpty()) {
                System.out.println("  Catalogue is empty.");
                return;
            }
            printInOrder(libraryRoot);
        }

        /**
         * Prints the catalogue in root-left-right order.
         */
        void printPreOrderCatalogue() {
            // Time: O(n) — every node is visited once.
            System.out.println("\nPre-order catalogue traversal:");
            if (isEmpty()) {
                System.out.println("  Catalogue is empty.");
                return;
            }
            printPreOrder(libraryRoot);
        }

        /**
         * Prints the catalogue in left-right-root order.
         */
        void printPostOrderCatalogue() {
            // Time: O(n) — every node is visited once.
            System.out.println("\nPost-order catalogue traversal:");
            if (isEmpty()) {
                System.out.println("  Catalogue is empty.");
                return;
            }
            printPostOrder(libraryRoot);
        }

        /**
         * Returns the number of unique ISBN nodes in the catalogue.
         *
         * @return unique ISBN count
         */
        int getUniqueBookCount() {
            // Time: O(1) — returns stored counter.
            return uniqueBookCount;
        }

        /**
         * Returns the total number of physical copies in the catalogue.
         *
         * @return total copy count
         */
        int getTotalCopyCount() {
            // Time: O(1) — returns stored counter.
            return totalCopyCount;
        }

        /**
         * Inserts recursively into the BST.
         *
         * @param currentNode subtree root
         * @param isbn key to insert
         * @param title book title
         * @return updated subtree root
         */
        private BookCatalogueNode insertRecursively(BookCatalogueNode currentNode, long isbn, String title) {
            // Time: O(h) — follows one branch of the tree.
            if (currentNode == null) {
                uniqueBookCount++;
                return new BookCatalogueNode(isbn, title);
            }

            if (isbn < currentNode.isbn) {
                currentNode.leftChild = insertRecursively(currentNode.leftChild, isbn, title);
            } else if (isbn > currentNode.isbn) {
                currentNode.rightChild = insertRecursively(currentNode.rightChild, isbn, title);
            } else {
                // Duplicate edge case: the ISBN already exists, so count another copy.
                currentNode.copyCount++;
                currentNode.title = title;
                System.out.println("[DUPLICATE ISBN] " + isbn + " already exists; copy count is now "
                        + currentNode.copyCount + ".");
            }

            return currentNode;
        }

        /**
         * Searches recursively for an ISBN.
         *
         * @param currentNode subtree root
         * @param isbn key to locate
         * @return matching node or null
         */
        private BookCatalogueNode searchRecursively(BookCatalogueNode currentNode, long isbn) {
            // Time: O(h) — follows one path based on comparisons.
            if (currentNode == null || currentNode.isbn == isbn) {
                return currentNode;
            }
            if (isbn < currentNode.isbn) {
                return searchRecursively(currentNode.leftChild, isbn);
            }
            return searchRecursively(currentNode.rightChild, isbn);
        }

        /**
         * Prints the catalogue in sorted order.
         *
         * @param currentNode subtree root
         */
        private void printInOrder(BookCatalogueNode currentNode) {
            // Time: O(n) — each node printed once.
            if (currentNode == null) {
                return;
            }
            printInOrder(currentNode.leftChild);
            System.out.println("  ISBN " + currentNode.isbn + " | " + currentNode.title
                    + " | copies: " + currentNode.copyCount);
            printInOrder(currentNode.rightChild);
        }

        /**
         * Prints the catalogue in root-left-right order.
         *
         * @param currentNode subtree root
         */
        private void printPreOrder(BookCatalogueNode currentNode) {
            // Time: O(n) — each node printed once.
            if (currentNode == null) {
                return;
            }
            System.out.println("  ISBN " + currentNode.isbn + " | " + currentNode.title
                    + " | copies: " + currentNode.copyCount);
            printPreOrder(currentNode.leftChild);
            printPreOrder(currentNode.rightChild);
        }

        /**
         * Prints the catalogue in left-right-root order.
         *
         * @param currentNode subtree root
         */
        private void printPostOrder(BookCatalogueNode currentNode) {
            // Time: O(n) — each node printed once.
            if (currentNode == null) {
                return;
            }
            printPostOrder(currentNode.leftChild);
            printPostOrder(currentNode.rightChild);
            System.out.println("  ISBN " + currentNode.isbn + " | " + currentNode.title
                    + " | copies: " + currentNode.copyCount);
        }
    }

    /**
     * Runs a story-driven catalogue demonstration for the National Library.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall for the demo outputs and traversals.
        LibraryCatalogueTree nationalLibrary = new LibraryCatalogueTree();

        System.out.println("=== National Library of South Africa Catalogue ===");

        // Edge case: empty tree search and print.
        nationalLibrary.printSortedCatalogue();
        System.out.println("Searching empty catalogue for ISBN 9780000000001: "
                + nationalLibrary.containsIsbn(9780000000001L));

        // Books arrive in random order.
        System.out.println("\n-- New digitised books are added --");
        nationalLibrary.addBook(9781868720001L, "Data Structures for Beginners");
        nationalLibrary.addBook(9781770100002L, "South African Wildlife Guide");
        nationalLibrary.addBook(9780798140003L, "Pretoria History and Heritage");
        nationalLibrary.addBook(9781868720005L, "Algorithms in Practice");
        nationalLibrary.addBook(9781868720001L, "Data Structures for Beginners");

        // Single element and not-found checks.
        System.out.println("\nSearching for ISBN 9780798140003: "
                + nationalLibrary.containsIsbn(9780798140003L));
        System.out.println("Searching for ISBN 9789999999999: "
                + nationalLibrary.containsIsbn(9789999999999L));

        BookCatalogueNode foundBook = nationalLibrary.findBook(9781868720001L);
        if (foundBook != null) {
            System.out.println("Found book: " + foundBook.isbn + " | " + foundBook.title
                    + " | copies: " + foundBook.copyCount);
        }

        nationalLibrary.printSortedCatalogue();
        nationalLibrary.printPreOrderCatalogue();
        nationalLibrary.printPostOrderCatalogue();

        System.out.println("\nUnique ISBN entries: " + nationalLibrary.getUniqueBookCount());
        System.out.println("Total copies held: " + nationalLibrary.getTotalCopyCount());
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - BST: A binary search tree keeps smaller ISBNs on the left and larger ISBNs on the right.
 - root: The libraryRoot node is the entry point to every search and traversal.
 - left/right child: Each book node branches into smaller and larger ISBN ranges.
 - in-order traversal: Visit left, node, right to print ISBNs in ascending order.
 - pre-order: Visit node, left, right; useful for copying or serialising the tree structure.
 - post-order: Visit left, right, node; useful for deletion and cleanup.
 - recursion: The tree methods call themselves on smaller subtrees until reaching null.
 - search O(log n): In a balanced BST, each comparison removes about half the remaining search space.

 Big-O for operations implemented:
 - isEmpty: O(1) because it checks whether the root is null.
 - addBook: O(h) because insertion follows one branch from root to leaf.
 - containsIsbn: O(h) because search follows one branch from root to leaf.
 - findBook: O(h) because it is the same path-based search logic.
 - printSortedCatalogue: O(n) because each node is visited once.
 - printPreOrderCatalogue: O(n) because each node is visited once.
 - printPostOrderCatalogue: O(n) because each node is visited once.
 - getUniqueBookCount: O(1) because it returns a stored counter.
 - getTotalCopyCount: O(1) because it returns a stored counter.
 Space complexity: O(h) for recursive calls, where h is tree height.

 Interview questions this code prepares you for:
 - Why does a BST help a library find books quickly?
 - How does in-order traversal produce sorted ISBN order?
 - What is the difference between searching in a BST and searching in an array?
 - How do duplicate ISBNs get handled in this implementation?
 - What happens to BST performance if the tree becomes skewed?

 Most common mistake and how this code avoids it:
 - Mistake: Forgetting to preserve the BST ordering rule when inserting recursively.
 - Avoided: Every insertion compares ISBNs and only goes left or right according to the BST rule.

 When to use this vs the common alternative:
 - Use a BST when you want ordered keys, efficient search, and sorted traversal.
 - Use a hash table when you only need very fast lookup and ordering does not matter.
 - Use a sorted array when the data changes rarely and binary search is enough.
*/
