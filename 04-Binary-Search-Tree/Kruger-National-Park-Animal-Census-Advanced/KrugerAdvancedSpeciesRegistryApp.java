/*
 Every ten years, SANParks conducts a full animal census of Kruger National Park, and rangers submit
 species sightings from across millions of hectares in no predictable order. Each sighting is tied to
 a unique species code, but the ecologist also needs to handle duplicate sightings, search quickly,
 delete codes when records are corrected, and print a sorted report at the end. Developer Nomsa uses a
 binary search tree because the BST keeps the data ordered as it is inserted: smaller codes go left,
 larger codes go right, and equal codes can be counted without adding duplicate nodes. That makes
 search, insertion, and sorted reporting natural fits for the structure, and recursion matches the
 tree's branching shape perfectly. This implementation goes further by adding deletion, min/max, and
 height analysis so students can see the full lifecycle of a BST.
*/

/**
 * Demonstrates an advanced Kruger species registry built with a custom binary search tree.
 * The tree supports insertion, search, deletion, traversal, min/max lookup, and height analysis.
 */
public class KrugerAdvancedSpeciesRegistryApp {

    /**
     * Represents one species code stored in the census registry.
     * Each node tracks duplicate sightings through a counter instead of adding extra nodes.
     */
    static class SpeciesRegistryNode {
        int speciesCode;
        int sightingCount;
        SpeciesRegistryNode leftChild;
        SpeciesRegistryNode rightChild;

        /**
         * Creates a new node for one species code.
         *
         * @param speciesCode census code recorded by rangers
         */
        SpeciesRegistryNode(int speciesCode) {
            // Time: O(1) — constant field assignments.
            this.speciesCode = speciesCode;
            this.sightingCount = 1;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    /**
     * Stores the full Kruger species registry as a binary search tree.
     * The root represents the first branch in the park's sorted census structure.
     */
    static class SpeciesRegistryTree {
        private SpeciesRegistryNode parkRoot;
        private int uniqueSpeciesCount;
        private int totalSightings;

        /**
         * Starts with an empty registry before ranger reports arrive.
         */
        SpeciesRegistryTree() {
            // Time: O(1) — simple initialization.
            this.parkRoot = null;
            this.uniqueSpeciesCount = 0;
            this.totalSightings = 0;
        }

        /**
         * Checks whether the registry has any sightings yet.
         *
         * @return true when no nodes exist
         */
        boolean isEmpty() {
            // Time: O(1) — one pointer check.
            return parkRoot == null;
        }

        /**
         * Records a species sighting in BST order.
         * Duplicate codes increment the existing node's sighting counter.
         *
         * @param speciesCode unique integer species code
         */
        void recordSpecies(int speciesCode) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            parkRoot = insertRecursively(parkRoot, speciesCode);
            totalSightings++;
        }

        /**
         * Searches for a species code.
         *
         * @param speciesCode code to find
         * @return true if the code exists in the tree
         */
        boolean containsSpecies(int speciesCode) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            return searchRecursively(parkRoot, speciesCode) != null;
        }

        /**
         * Removes one sighting of a species code.
         * If the node's count is greater than one, only the count is decremented.
         *
         * @param speciesCode code to remove
         * @return true if a sighting was removed
         */
        boolean removeSpecies(int speciesCode) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            RemovalOutcome outcome = new RemovalOutcome(false);
            parkRoot = removeRecursively(parkRoot, speciesCode, outcome);
            if (outcome.wasRemoved) {
                totalSightings--;
            }
            return outcome.wasRemoved;
        }

        /**
         * Prints the species report in ascending order.
         */
        void printInOrderReport() {
            // Time: O(n) — each node is visited once.
            System.out.println("\nSorted species report (in-order traversal):");
            if (isEmpty()) {
                System.out.println("  No species sightings recorded.");
                return;
            }
            printInOrder(parkRoot);
        }

        /**
         * Prints the registry in root-left-right order.
         */
        void printPreOrderReport() {
            // Time: O(n) — each node is visited once.
            System.out.println("\nPre-order traversal (root-left-right):");
            if (isEmpty()) {
                System.out.println("  Registry is empty.");
                return;
            }
            printPreOrder(parkRoot);
        }

        /**
         * Prints the registry in left-right-root order.
         */
        void printPostOrderReport() {
            // Time: O(n) — each node is visited once.
            System.out.println("\nPost-order traversal (left-right-root):");
            if (isEmpty()) {
                System.out.println("  Registry is empty.");
                return;
            }
            printPostOrder(parkRoot);
        }

        /**
         * Returns the smallest species code in the tree.
         *
         * @return minimum code or -1 if tree is empty
         */
        int findSmallestSpeciesCode() {
            // Time: O(h) — follows left links to the minimum.
            if (isEmpty()) {
                return -1;
            }
            SpeciesRegistryNode cursor = parkRoot;
            while (cursor.leftChild != null) {
                cursor = cursor.leftChild;
            }
            return cursor.speciesCode;
        }

        /**
         * Returns the largest species code in the tree.
         *
         * @return maximum code or -1 if tree is empty
         */
        int findLargestSpeciesCode() {
            // Time: O(h) — follows right links to the maximum.
            if (isEmpty()) {
                return -1;
            }
            SpeciesRegistryNode cursor = parkRoot;
            while (cursor.rightChild != null) {
                cursor = cursor.rightChild;
            }
            return cursor.speciesCode;
        }

        /**
         * Returns the height of the tree.
         *
         * @return tree height measured in nodes on the longest path
         */
        int getTreeHeight() {
            // Time: O(n) — every node must be visited to compute height.
            return heightRecursively(parkRoot);
        }

        /**
         * Returns the number of unique species codes.
         *
         * @return unique node count
         */
        int getUniqueSpeciesCount() {
            // Time: O(1) — returns stored counter.
            return uniqueSpeciesCount;
        }

        /**
         * Returns the total number of sightings including duplicates.
         *
         * @return total sightings count
         */
        int getTotalSightings() {
            // Time: O(1) — returns stored counter.
            return totalSightings;
        }

        /**
         * Compares this custom BST to Java's built-in ordered set/map structures.
         *
         * @return readable comparison summary for students
         */
        String compareWithJavaBuiltInEquivalent() {
            // Time: O(1) — returns a fixed explanatory string.
            return "Java's TreeSet/TreeMap already provide a balanced BST-like structure with guaranteed"
                    + " logarithmic operations, but this custom tree shows the raw node/link mechanics,"
                    + " recursion, and deletion cases that those collections hide from you.";
        }

        /**
         * Recursive BST insertion.
         *
         * @param currentNode subtree root
         * @param speciesCode code to insert
         * @return updated subtree root
         */
        private SpeciesRegistryNode insertRecursively(SpeciesRegistryNode currentNode, int speciesCode) {
            // Time: O(h) — follows one branch.
            if (currentNode == null) {
                uniqueSpeciesCount++;
                return new SpeciesRegistryNode(speciesCode);
            }

            if (speciesCode < currentNode.speciesCode) {
                currentNode.leftChild = insertRecursively(currentNode.leftChild, speciesCode);
            } else if (speciesCode > currentNode.speciesCode) {
                currentNode.rightChild = insertRecursively(currentNode.rightChild, speciesCode);
            } else {
                currentNode.sightingCount++;
                System.out.println("[DUPLICATE] Species code " + speciesCode + " seen again; count now "
                        + currentNode.sightingCount + ".");
            }

            return currentNode;
        }

        /**
         * Recursive BST search.
         *
         * @param currentNode subtree root
         * @param speciesCode code to find
         * @return matching node or null
         */
        private SpeciesRegistryNode searchRecursively(SpeciesRegistryNode currentNode, int speciesCode) {
            // Time: O(h) — follows a single branch.
            if (currentNode == null || currentNode.speciesCode == speciesCode) {
                return currentNode;
            }
            if (speciesCode < currentNode.speciesCode) {
                return searchRecursively(currentNode.leftChild, speciesCode);
            }
            return searchRecursively(currentNode.rightChild, speciesCode);
        }

        /**
         * Recursive BST deletion.
         *
         * @param currentNode subtree root
         * @param speciesCode code to remove
         * @param outcome shared removal flag
         * @return updated subtree root
         */
        private SpeciesRegistryNode removeRecursively(SpeciesRegistryNode currentNode, int speciesCode,
                                                      RemovalOutcome outcome) {
            // Time: O(h) — follows one branch and may rebalance local links.
            if (currentNode == null) {
                return null;
            }

            if (speciesCode < currentNode.speciesCode) {
                currentNode.leftChild = removeRecursively(currentNode.leftChild, speciesCode, outcome);
                return currentNode;
            }

            if (speciesCode > currentNode.speciesCode) {
                currentNode.rightChild = removeRecursively(currentNode.rightChild, speciesCode, outcome);
                return currentNode;
            }

            // Match found.
            outcome.wasRemoved = true;

            if (currentNode.sightingCount > 1) {
                // Duplicate edge case: only decrement the counter.
                currentNode.sightingCount--;
                return currentNode;
            }

            // Node with one or zero children.
            if (currentNode.leftChild == null) {
                uniqueSpeciesCount--;
                return currentNode.rightChild;
            }
            if (currentNode.rightChild == null) {
                uniqueSpeciesCount--;
                return currentNode.leftChild;
            }

            // Node with two children: replace with inorder successor.
            SpeciesRegistryNode successor = findSmallestNode(currentNode.rightChild);
            currentNode.speciesCode = successor.speciesCode;
            currentNode.sightingCount = successor.sightingCount;
            successor.sightingCount = 1;
            currentNode.rightChild = removeRecursively(currentNode.rightChild, successor.speciesCode, new RemovalOutcome(false));
            return currentNode;
        }

        /**
         * Finds the smallest node in a subtree.
         *
         * @param currentNode subtree root
         * @return leftmost node
         */
        private SpeciesRegistryNode findSmallestNode(SpeciesRegistryNode currentNode) {
            // Time: O(h) — follows left links only.
            SpeciesRegistryNode cursor = currentNode;
            while (cursor.leftChild != null) {
                cursor = cursor.leftChild;
            }
            return cursor;
        }

        /**
         * Returns the height of a subtree recursively.
         *
         * @param currentNode subtree root
         * @return subtree height
         */
        private int heightRecursively(SpeciesRegistryNode currentNode) {
            // Time: O(n) — must inspect each node once.
            if (currentNode == null) {
                return 0;
            }
            int leftHeight = heightRecursively(currentNode.leftChild);
            int rightHeight = heightRecursively(currentNode.rightChild);
            return 1 + Math.max(leftHeight, rightHeight);
        }

        /**
         * Prints a subtree in ascending order.
         *
         * @param currentNode subtree root
         */
        private void printInOrder(SpeciesRegistryNode currentNode) {
            // Time: O(n) — each node is printed once.
            if (currentNode == null) {
                return;
            }
            printInOrder(currentNode.leftChild);
            System.out.println("  Species code " + currentNode.speciesCode
                    + " (sightings: " + currentNode.sightingCount + ")");
            printInOrder(currentNode.rightChild);
        }

        /**
         * Prints a subtree in root-left-right order.
         *
         * @param currentNode subtree root
         */
        private void printPreOrder(SpeciesRegistryNode currentNode) {
            // Time: O(n) — each node is printed once.
            if (currentNode == null) {
                return;
            }
            System.out.println("  Species code " + currentNode.speciesCode
                    + " (sightings: " + currentNode.sightingCount + ")");
            printPreOrder(currentNode.leftChild);
            printPreOrder(currentNode.rightChild);
        }

        /**
         * Prints a subtree in left-right-root order.
         *
         * @param currentNode subtree root
         */
        private void printPostOrder(SpeciesRegistryNode currentNode) {
            // Time: O(n) — each node is printed once.
            if (currentNode == null) {
                return;
            }
            printPostOrder(currentNode.leftChild);
            printPostOrder(currentNode.rightChild);
            System.out.println("  Species code " + currentNode.speciesCode
                    + " (sightings: " + currentNode.sightingCount + ")");
        }
    }

    /**
     * Holds the outcome of a delete operation.
     * Used so recursive removal can report success back to the caller.
     */
    static class RemovalOutcome {
        boolean wasRemoved;

        /**
         * Creates a removal outcome flag.
         *
         * @param wasRemoved whether a matching species was removed
         */
        RemovalOutcome(boolean wasRemoved) {
            // Time: O(1) — one assignment.
            this.wasRemoved = wasRemoved;
        }
    }

    /**
     * Runs a full advanced Kruger registry demonstration.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall for the demo operations and traversals.
        SpeciesRegistryTree krugerRegistry = new SpeciesRegistryTree();

        System.out.println("=== Kruger National Park Advanced Species Registry ===");

        // Edge case: empty tree operations.
        krugerRegistry.printInOrderReport();
        System.out.println("Searching empty registry for 500: " + krugerRegistry.containsSpecies(500));
        System.out.println("Removing from empty registry: " + krugerRegistry.removeSpecies(500));

        // Insert sightings in random order.
        System.out.println("\n-- Ranger sightings arrive --");
        krugerRegistry.recordSpecies(620);
        krugerRegistry.recordSpecies(250);
        krugerRegistry.recordSpecies(800);
        krugerRegistry.recordSpecies(180);
        krugerRegistry.recordSpecies(400);
        krugerRegistry.recordSpecies(700);
        krugerRegistry.recordSpecies(900);
        krugerRegistry.recordSpecies(400);

        // Search and single-element style checks.
        System.out.println("\nSearching for species 700: " + krugerRegistry.containsSpecies(700));
        System.out.println("Searching for species 999: " + krugerRegistry.containsSpecies(999));

        // Delete duplicate count first, then actual node.
        System.out.println("\n-- Deleting one recorded sighting of 400 --");
        System.out.println("Removed? " + krugerRegistry.removeSpecies(400));

        // Remove not-found and then existing value.
        System.out.println("Removed species 999? " + krugerRegistry.removeSpecies(999));
        System.out.println("Removed species 180? " + krugerRegistry.removeSpecies(180));

        krugerRegistry.printInOrderReport();
        krugerRegistry.printPreOrderReport();
        krugerRegistry.printPostOrderReport();

        System.out.println("\nSmallest species code: " + krugerRegistry.findSmallestSpeciesCode());
        System.out.println("Largest species code: " + krugerRegistry.findLargestSpeciesCode());
        System.out.println("Tree height: " + krugerRegistry.getTreeHeight());
        System.out.println("Unique species codes: " + krugerRegistry.getUniqueSpeciesCount());
        System.out.println("Total sightings: " + krugerRegistry.getTotalSightings());

        System.out.println("\nJava comparison: " + krugerRegistry.compareWithJavaBuiltInEquivalent());
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - BST: A binary search tree keeps smaller codes on the left and larger codes on the right.
 - root: The root is the top entry point to the registry tree.
 - left/right child: Each node branches into smaller and larger subtrees.
 - in-order traversal: Visit left, node, right to print species codes in ascending order.
 - pre-order: Visit node, left, right; useful for tree copying and structural inspection.
 - post-order: Visit left, right, node; useful for cleanup and deletion work.
 - recursion: Methods call themselves on smaller subtrees until a base case is reached.
 - search O(log n): In a balanced BST, each comparison removes about half the remaining space.

 Big-O for operations implemented:
 - isEmpty: O(1) because it checks whether the root is null.
 - recordSpecies: O(h) because insertion follows one root-to-leaf path.
 - containsSpecies: O(h) because search follows one path through the tree.
 - removeSpecies: O(h) because deletion also follows one path and rewires locally.
 - printInOrderReport: O(n) because every node is visited once.
 - printPreOrderReport: O(n) because every node is visited once.
 - printPostOrderReport: O(n) because every node is visited once.
 - findSmallestSpeciesCode: O(h) because it follows left links only.
 - findLargestSpeciesCode: O(h) because it follows right links only.
 - getTreeHeight: O(n) because every node contributes to the height calculation.
 - getUniqueSpeciesCount: O(1) because it returns a stored counter.
 - getTotalSightings: O(1) because it returns a stored counter.
 - compareWithJavaBuiltInEquivalent: O(1) because it returns a fixed explanatory string.
 Space complexity: O(h) for recursion depth during insert/search/delete/traversal, which is O(log n) when balanced and O(n) in the worst case.

 Interview questions this code prepares you for:
 - How do you delete a node from a BST?
 - What is the inorder successor and why is it useful?
 - How do duplicates affect a BST implementation?
 - Why can BST search be O(log n) in a balanced tree but O(n) in a skewed tree?
 - What is the difference between BST traversals and what are they used for?

 Most common mistake and how this code avoids it:
 - Mistake: Mishandling the two-child delete case and breaking the BST ordering rule.
 - Avoided: The delete logic replaces the node with its inorder successor and then removes that successor correctly.

 When to use this vs the common alternative:
 - Use a BST when you need ordered data, fast search, and sorted traversal with direct node control.
 - Use a hash table when you only need lookup and ordering is not important.
 - Use a balanced library tree such as TreeMap or TreeSet when you want production-ready guarantees without writing your own recursion and deletion logic.
*/
