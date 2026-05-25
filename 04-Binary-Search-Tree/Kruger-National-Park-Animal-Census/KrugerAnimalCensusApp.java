/*
 Every ten years, SANParks conducts a complete animal census of Kruger National Park, and rangers
 submit species sightings from all over the park in no predictable order. Each sighting carries a
 unique species code, so the park ecologist needs a clean sorted report at the end of the census.
 Developer Nomsa builds a binary search tree so each new sighting can be placed into the correct
 position as it arrives, with smaller codes going left and larger codes going right. That makes the
 tree a natural fit because the data stays partially ordered during insertion, instead of requiring a
 separate sorting pass later. An in-order traversal then visits the nodes in ascending order and
 produces a sorted species report directly. The same tree also makes search efficient in a balanced
 case, which is why BSTs are widely used for ordered data.
*/

/**
 * Demonstrates how Kruger National Park's animal census can be stored in a custom binary search tree.
 * The tree records species sightings, supports search, and prints sorted reports without sorting arrays.
 */
public class KrugerAnimalCensusApp {

    /**
     * Represents one species sighting in the census tree.
     * Each node stores the species code, how many times it was seen, and child links.
     */
    static class SpeciesSightingNode {
        int speciesCode;
        int sightingCount;
        SpeciesSightingNode leftChild;
        SpeciesSightingNode rightChild;

        /**
         * Creates a new tree node for one species code.
         *
         * @param speciesCode unique integer recorded by a ranger
         */
        SpeciesSightingNode(int speciesCode) {
            // Time: O(1) — constant field assignments only.
            this.speciesCode = speciesCode;
            this.sightingCount = 1;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    /**
     * Stores the full census as a binary search tree.
     * Species codes smaller than a node go left; larger codes go right.
     */
    static class SpeciesCensusTree {
        private SpeciesSightingNode parkRoot;
        private int uniqueSpeciesCount;
        private int totalSightings;

        /**
         * Starts the census tree empty before ranger reports begin arriving.
         */
        SpeciesCensusTree() {
            // Time: O(1) — simple initialization.
            this.parkRoot = null;
            this.uniqueSpeciesCount = 0;
            this.totalSightings = 0;
        }

        /**
         * Checks whether the census tree has no sightings yet.
         *
         * @return true when the tree is empty
         */
        boolean isEmpty() {
            // Time: O(1) — one pointer check.
            return parkRoot == null;
        }

        /**
         * Inserts a new species sighting into the correct BST position.
         * Duplicate codes are counted rather than stored as extra nodes.
         *
         * @param speciesCode sighting code reported by a ranger
         */
        void recordSighting(int speciesCode) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            parkRoot = insertRecursively(parkRoot, speciesCode);
            totalSightings++;
        }

        /**
         * Searches for a species code in the census tree.
         *
         * @param speciesCode code to locate
         * @return true when the code exists in the tree
         */
        boolean containsSpeciesCode(int speciesCode) {
            // Time: O(h) where h is tree height; average O(log n), worst O(n).
            return searchRecursively(parkRoot, speciesCode) != null;
        }

        /**
         * Prints the census in ascending order of species codes.
         * In-order traversal gives sorted output because of the BST property.
         */
        void printSortedSpeciesReport() {
            // Time: O(n) — every node is visited once.
            System.out.println("\nSorted species report (in-order traversal):");
            if (isEmpty()) {
                System.out.println("  No species sightings recorded.");
                return;
            }
            printInOrder(parkRoot);
        }

        /**
         * Prints the tree in root-left-right order.
         *
         * @return pre-order traversal for study purposes
         */
        void printPreOrderTraversal() {
            // Time: O(n) — every node is visited once.
            System.out.println("\nPre-order traversal (root-left-right):");
            if (isEmpty()) {
                System.out.println("  Tree is empty.");
                return;
            }
            printPreOrder(parkRoot);
        }

        /**
         * Prints the tree in left-right-root order.
         *
         * @return post-order traversal for study purposes
         */
        void printPostOrderTraversal() {
            // Time: O(n) — every node is visited once.
            System.out.println("\nPost-order traversal (left-right-root):");
            if (isEmpty()) {
                System.out.println("  Tree is empty.");
                return;
            }
            printPostOrder(parkRoot);
        }

        /**
         * Returns the number of unique species codes stored in the tree.
         *
         * @return unique node count
         */
        int getUniqueSpeciesCount() {
            // Time: O(1) — returns a tracked counter.
            return uniqueSpeciesCount;
        }

        /**
         * Returns the total number of sightings including duplicates.
         *
         * @return total sightings count
         */
        int getTotalSightings() {
            // Time: O(1) — returns a tracked counter.
            return totalSightings;
        }

        /**
         * Inserts recursively according to BST rules.
         *
         * @param currentNode subtree root being processed
         * @param speciesCode code to insert
         * @return updated subtree root
         */
        private SpeciesSightingNode insertRecursively(SpeciesSightingNode currentNode, int speciesCode) {
            // Time: O(h) — recursion follows one path down the tree.
            if (currentNode == null) {
                uniqueSpeciesCount++;
                return new SpeciesSightingNode(speciesCode);
            }

            // BST rule: smaller values go left, larger values go right.
            if (speciesCode < currentNode.speciesCode) {
                currentNode.leftChild = insertRecursively(currentNode.leftChild, speciesCode);
            } else if (speciesCode > currentNode.speciesCode) {
                currentNode.rightChild = insertRecursively(currentNode.rightChild, speciesCode);
            } else {
                // Duplicate edge case: same species code seen again, count it instead of creating another node.
                currentNode.sightingCount++;
                System.out.println("[DUPLICATE] Species code " + speciesCode + " seen again; count increased to "
                        + currentNode.sightingCount + ".");
            }

            return currentNode;
        }

        /**
         * Searches recursively for a species code.
         *
         * @param currentNode subtree root being searched
         * @param speciesCode code to locate
         * @return matching node or null if absent
         */
        private SpeciesSightingNode searchRecursively(SpeciesSightingNode currentNode, int speciesCode) {
            // Time: O(h) — follows a single branch based on comparisons.
            if (currentNode == null || currentNode.speciesCode == speciesCode) {
                return currentNode;
            }

            if (speciesCode < currentNode.speciesCode) {
                return searchRecursively(currentNode.leftChild, speciesCode);
            }
            return searchRecursively(currentNode.rightChild, speciesCode);
        }

        /**
         * Recursively prints nodes in ascending order.
         *
         * @param currentNode subtree root to print
         */
        private void printInOrder(SpeciesSightingNode currentNode) {
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
         * Recursively prints nodes in root-left-right order.
         *
         * @param currentNode subtree root to print
         */
        private void printPreOrder(SpeciesSightingNode currentNode) {
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
         * Recursively prints nodes in left-right-root order.
         *
         * @param currentNode subtree root to print
         */
        private void printPostOrder(SpeciesSightingNode currentNode) {
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
     * Runs a story-driven Kruger census demonstration showing insertion, search, and traversal.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall for the demo actions and traversals.
        SpeciesCensusTree krugerCensus = new SpeciesCensusTree();

        System.out.println("=== Kruger National Park Animal Census ===");

        // Edge case: empty tree.
        krugerCensus.printSortedSpeciesReport();
        System.out.println("Searching empty tree for 450: " + krugerCensus.containsSpeciesCode(450));

        // Insert sightings arriving in random order.
        System.out.println("\n-- Ranger sightings arrive from across the park --");
        krugerCensus.recordSighting(620);
        krugerCensus.recordSighting(250);
        krugerCensus.recordSighting(800);
        krugerCensus.recordSighting(180);
        krugerCensus.recordSighting(400);
        krugerCensus.recordSighting(700);
        krugerCensus.recordSighting(900);

        // Single-element / duplicate edge case.
        krugerCensus.recordSighting(400);

        // Search examples.
        System.out.println("\nSearching for species code 700: " + krugerCensus.containsSpeciesCode(700));
        System.out.println("Searching for species code 999: " + krugerCensus.containsSpeciesCode(999));

        // Traversals.
        krugerCensus.printSortedSpeciesReport();
        krugerCensus.printPreOrderTraversal();
        krugerCensus.printPostOrderTraversal();

        System.out.println("\nUnique species codes recorded: " + krugerCensus.getUniqueSpeciesCount());
        System.out.println("Total sightings recorded: " + krugerCensus.getTotalSightings());
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - BST: A binary search tree keeps smaller values on the left and larger values on the right.
 - root: The top node of the tree; every search and traversal starts here.
 - left/right child: Each node can have up to two children that split the data into ordered halves.
 - in-order traversal: Visit left subtree, node, then right subtree to get sorted output.
 - pre-order: Visit node first, then left, then right; useful for copying structures.
 - post-order: Visit left, right, then node; useful for deleting or evaluating trees.
 - recursion: The method calls itself on smaller subtrees until it reaches a base case.
 - search O(log n): In a balanced tree, each comparison cuts the search space roughly in half.

 Big-O for operations implemented:
 - isEmpty: O(1) because it checks whether the root pointer is null.
 - recordSighting: O(h) because insertion follows one path from root to a leaf, where h is height.
 - containsSpeciesCode: O(h) because search follows one path based on comparisons.
 - printSortedSpeciesReport: O(n) because every node is visited once in order.
 - printPreOrderTraversal: O(n) because every node is visited once.
 - printPostOrderTraversal: O(n) because every node is visited once.
 - getUniqueSpeciesCount: O(1) because it returns a stored counter.
 - getTotalSightings: O(1) because it returns a stored counter.
 Space complexity: O(h) for recursion depth during insert/search/traversal, which is O(log n) when balanced and O(n) in the worst case.

 Interview questions this code prepares you for:
 - Why does an in-order traversal of a BST return sorted data?
 - What is the difference between pre-order, in-order, and post-order traversal?
 - How does BST search achieve O(log n) in the average balanced case?
 - What happens to BST performance if the tree becomes skewed?
 - How can duplicate values be handled in a BST?

 Most common mistake and how this code avoids it:
 - Mistake: Forgetting the base case in recursive insert/search methods, which causes infinite recursion.
 - Avoided: Every recursive helper stops immediately when the current node is null.

 When to use this vs the common alternative:
 - Use a BST when you need ordered storage with fast search, insertion, and traversal in sorted order.
 - Use a hash table when you only need fast lookup and do not care about order.
 - Use a sorted array when the data is mostly static and binary search is enough, but insertions are rare.
*/
