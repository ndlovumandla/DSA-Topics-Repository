/*
 Johannesburg Public Library has 180,000 registered members, and each return transaction requires an
 immediate fine lookup by membership number. A linear scan through all members is too slow for front-desk
 service because each query may check thousands of unrelated records first. Developer Nandi rebuilds the
 system using a hash table so membership numbers map directly to bucket indices. When two members map to
 the same bucket, she resolves collisions using chaining, where each bucket stores a linked list of nodes.
 This design gives O(1) average-time lookups when keys are well distributed. She also tracks load factor
 to explain why performance degrades toward O(n) when the table becomes too full and chains grow longer.
*/

/**
 * Beginner hash-table internals demo for Johannesburg library fine lookups.
 */
public class JohannesburgLibraryFineSystemBeginnerApp {

    /**
     * Represents one library member account.
     */
    static class LibraryMemberFineRecord {
        String membershipNumber;
        String memberName;
        double outstandingFineRand;

        /**
         * Creates one member fine record.
         *
         * @param membershipNumber unique membership key
         * @param memberName member full name
         * @param outstandingFineRand outstanding fine amount in Rand
         */
        LibraryMemberFineRecord(String membershipNumber, String memberName, double outstandingFineRand) {
            // Time: O(1) - constant-time field assignment.
            this.membershipNumber = membershipNumber;
            this.memberName = memberName;
            this.outstandingFineRand = outstandingFineRand;
        }

        /**
         * Hash code based on membership number.
         *
         * @return integer hash value
         */
        @Override
        public int hashCode() {
            // Time: O(k) where k is key length, because each character is processed once.
            int hash = 17;
            for (int i = 0; i < membershipNumber.length(); i++) {
                hash = 31 * hash + membershipNumber.charAt(i);
            }
            return hash;
        }

        /**
         * Equality based on membership number identity.
         *
         * @param obj compared object
         * @return true if keys are equal
         */
        @Override
        public boolean equals(Object obj) {
            // Time: O(k) for string comparison in worst case.
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof LibraryMemberFineRecord)) {
                return false;
            }
            LibraryMemberFineRecord other = (LibraryMemberFineRecord) obj;
            return this.membershipNumber.equals(other.membershipNumber);
        }
    }

    /**
     * Node used in bucket chains (linked list per bucket).
     */
    static class BucketChainNode {
        LibraryMemberFineRecord record;
        BucketChainNode next;

        /**
         * Creates a node for chaining.
         *
         * @param record member record for this node
         */
        BucketChainNode(LibraryMemberFineRecord record) {
            // Time: O(1) - constant-time node creation.
            this.record = record;
            this.next = null;
        }
    }

    /**
     * Custom hash table with chaining for collision handling.
     */
    static class LibraryFineHashTable {
        BucketChainNode[] buckets;
        int memberCount;

        /**
         * Creates hash table with fixed bucket count.
         *
         * @param bucketCount number of buckets
         */
        LibraryFineHashTable(int bucketCount) {
            // Time: O(b) for array allocation, where b = bucketCount.
            this.buckets = new BucketChainNode[bucketCount];
            this.memberCount = 0;
        }

        /**
         * Maps a membership key to a valid bucket index.
         *
         * @param membershipNumber key to hash
         * @return bucket index in range [0, buckets.length - 1]
         */
        int indexFor(String membershipNumber) {
            // Time: O(k) where k is key length due to hash calculation.
            int hash = 17;
            for (int i = 0; i < membershipNumber.length(); i++) {
                hash = 31 * hash + membershipNumber.charAt(i);
            }
            // Use floorMod-style logic to avoid negative index from negative hash.
            return (hash & 0x7fffffff) % buckets.length;
        }

        /**
         * Inserts a new record or updates an existing member by key.
         *
         * @param newRecord member record to put
         */
        void put(LibraryMemberFineRecord newRecord) {
            // Time: O(1) average, O(n) worst if one chain becomes very long.
            int bucketIndex = indexFor(newRecord.membershipNumber);
            BucketChainNode head = buckets[bucketIndex];

            // Traverse chain to update existing key (equals semantics by membership number).
            BucketChainNode cursor = head;
            while (cursor != null) {
                if (cursor.record.membershipNumber.equals(newRecord.membershipNumber)) {
                    cursor.record.memberName = newRecord.memberName;
                    cursor.record.outstandingFineRand = newRecord.outstandingFineRand;
                    return;
                }
                cursor = cursor.next;
            }

            // Collision handling by chaining: prepend new node to this bucket linked list.
            BucketChainNode inserted = new BucketChainNode(newRecord);
            inserted.next = head;
            buckets[bucketIndex] = inserted;
            memberCount++;
        }

        /**
         * Looks up a member fine record by membership number.
         *
         * @param membershipNumber key to find
         * @return found record, or null if not found
         */
        LibraryMemberFineRecord get(String membershipNumber) {
            // Time: O(1) average, O(n) worst when collisions create long chain.
            int bucketIndex = indexFor(membershipNumber);
            BucketChainNode cursor = buckets[bucketIndex];

            while (cursor != null) {
                if (cursor.record.membershipNumber.equals(membershipNumber)) {
                    return cursor.record;
                }
                cursor = cursor.next;
            }
            return null;
        }

        /**
         * Current load factor = members / bucket count.
         *
         * @return load factor value
         */
        double loadFactor() {
            // Time: O(1) - simple arithmetic.
            return (double) memberCount / buckets.length;
        }

        /**
         * Prints collision stats by counting used buckets and maximum chain length.
         */
        void printBucketStats() {
            // Time: O(b + n) - scans all buckets and all chain nodes once.
            int usedBuckets = 0;
            int maxChainLength = 0;

            for (int i = 0; i < buckets.length; i++) {
                int chainLength = 0;
                BucketChainNode cursor = buckets[i];
                while (cursor != null) {
                    chainLength++;
                    cursor = cursor.next;
                }
                if (chainLength > 0) {
                    usedBuckets++;
                }
                if (chainLength > maxChainLength) {
                    maxChainLength = chainLength;
                }
            }

            System.out.printf("  Members: %d, Buckets: %d, Load factor: %.2f%n",
                    memberCount, buckets.length, loadFactor());
            System.out.printf("  Used buckets: %d, Max chain length: %d%n", usedBuckets, maxChainLength);
            if (loadFactor() > 0.75) {
                System.out.println("  Warning: load factor > 0.75, collision chains may reduce lookup speed.");
            }
        }
    }

    /**
     * Prints lookup result narrative.
     *
     * @param table fine hash table
     * @param membershipNumber key to search
     */
    static void printLookup(LibraryFineHashTable table, String membershipNumber) {
        // Time: O(1) average due to hash-table lookup.
        LibraryMemberFineRecord found = table.get(membershipNumber);
        if (found == null) {
            System.out.println("  Membership " + membershipNumber + " not found.");
        } else {
            System.out.printf("  %s (%s) outstanding fine: R%.2f%n",
                    found.memberName, found.membershipNumber, found.outstandingFineRand);
        }
    }

    /**
     * Program entry point: demonstrates custom hash table internals.
     *
     * @param args unused CLI args
     */
    public static void main(String[] args) {
        System.out.println("=== Johannesburg Public Library Fine Hash Table (Beginner) ===");

        LibraryFineHashTable fineTable = new LibraryFineHashTable(7);

        System.out.println("\n-- Edge case: lookup on empty table --");
        printLookup(fineTable, "JPL-0001");

        System.out.println("\n-- Insert a single member --");
        fineTable.put(new LibraryMemberFineRecord("JPL-0001", "Nomsa Dube", 6.00));
        printLookup(fineTable, "JPL-0001");

        System.out.println("\n-- Insert multiple members (including collisions by design) --");
        fineTable.put(new LibraryMemberFineRecord("JPL-0008", "Thabo Nkosi", 14.00));
        fineTable.put(new LibraryMemberFineRecord("JPL-0015", "Anele Mthembu", 0.00));
        fineTable.put(new LibraryMemberFineRecord("JPL-0022", "Lerato Pillay", 28.00));
        fineTable.put(new LibraryMemberFineRecord("JPL-0029", "Sipho Ndlovu", 4.00));
        fineTable.put(new LibraryMemberFineRecord("JPL-0036", "Mpho Khumalo", 2.00));
        fineTable.printBucketStats();

        System.out.println("\n-- Duplicate key update (same member, new fine amount) --");
        fineTable.put(new LibraryMemberFineRecord("JPL-0001", "Nomsa Dube", 10.00));
        printLookup(fineTable, "JPL-0001");

        System.out.println("\n-- Not-found edge case --");
        printLookup(fineTable, "JPL-9999");

        System.out.println("\n-- Real desk scenario: librarian checks immediate balance --");
        printLookup(fineTable, "JPL-0022");
        printLookup(fineTable, "JPL-0036");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - hashCode(): converts key characters into an integer hash for indexing.
 - equals(): confirms true key identity when multiple records share a bucket.
 - collision: two different keys map to the same bucket index.
 - chaining: each bucket stores a linked list so collisions can coexist.
 - load factor: memberCount / bucketCount; higher values mean longer average chains.
 - bucket: one slot in the hash table array holding a chain head.
 - HashMap internals: array of buckets + per-bucket node chains + key comparison.
 - O(1) average: expected constant-time insert/get with good hash distribution.

 Big-O of operations implemented:
 - indexFor: O(k) where k is key length.
 - put: O(1) average, O(n) worst-case with severe collisions.
 - get: O(1) average, O(n) worst-case with severe collisions.
 - loadFactor: O(1).
 - printBucketStats: O(b + n), scans all buckets and all nodes.

 Interview questions:
 - Why does a hash table still need equals() after hashCode()?
 - What happens when load factor grows above 0.75?
 - Why is chaining O(1) average but O(n) worst case?
 - How does bucket count influence collision frequency?
 - What is the difference between collision handling by chaining vs open addressing?

 Common mistake and prevention:
 - Mistake: relying only on hash value equality and not checking key equality.
 - Avoided here: get() and put() compare membershipNumber with equals() in chain traversal.

 Comparison guidance:
 - Use hash tables for exact-key lookup/update by ID in average O(1) time.
 - Use arrays/lists with linear search only when dataset is tiny or iteration order dominates.
*/