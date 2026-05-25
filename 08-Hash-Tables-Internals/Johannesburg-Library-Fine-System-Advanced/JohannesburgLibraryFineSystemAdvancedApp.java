/*
 Johannesburg Public Library handles fine lookups for about 180,000 members, where each desk query
 must return a balance instantly by membership number. Nandi's advanced redesign uses a custom hash
 table with bucket arrays and chaining to avoid linear scans. Each member key is hashed to a bucket,
 collisions are stored in linked nodes, and lookups are O(1) on average while chains stay short.
 To keep chains short as the table grows, the implementation tracks load factor and automatically
 resizes/rehashes when it exceeds 0.75. The program demonstrates put/get/remove/contains operations,
 collision statistics, and performance behavior under load. For verification, Nandi also compares
 selected results against Java's built-in HashMap to show equivalent key-value behavior.
*/

import java.util.HashMap;

/**
 * Advanced hash-table internals demo for Johannesburg library fines.
 */
public class JohannesburgLibraryFineSystemAdvancedApp {

    /**
     * Member fine data.
     */
    static class MemberFineAccount {
        String membershipNumber;
        String memberName;
        int borrowedBookCount;
        double outstandingFine;

        /**
         * Creates one member account.
         *
         * @param membershipNumber unique key
         * @param memberName member full name
         * @param borrowedBookCount current borrowed books
         * @param outstandingFine current outstanding fine in Rand
         */
        MemberFineAccount(String membershipNumber, String memberName,
                          int borrowedBookCount, double outstandingFine) {
            // Time: O(1) - constant assignments.
            this.membershipNumber = membershipNumber;
            this.memberName = memberName;
            this.borrowedBookCount = borrowedBookCount;
            this.outstandingFine = outstandingFine;
        }

        /**
         * Hash code from membership key.
         *
         * @return integer hash
         */
        @Override
        public int hashCode() {
            // Time: O(k), k = key length.
            int hash = 23;
            for (int i = 0; i < membershipNumber.length(); i++) {
                hash = 31 * hash + membershipNumber.charAt(i);
            }
            return hash;
        }

        /**
         * Equality by membership number.
         *
         * @param obj compared object
         * @return true when keys match
         */
        @Override
        public boolean equals(Object obj) {
            // Time: O(k) worst case string compare.
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MemberFineAccount)) {
                return false;
            }
            MemberFineAccount other = (MemberFineAccount) obj;
            return this.membershipNumber.equals(other.membershipNumber);
        }
    }

    /**
     * Bucket node for chaining.
     */
    static class FineBucketNode {
        MemberFineAccount account;
        FineBucketNode next;

        /**
         * Creates one chain node.
         *
         * @param account member account payload
         */
        FineBucketNode(MemberFineAccount account) {
            // Time: O(1).
            this.account = account;
            this.next = null;
        }
    }

    /**
     * Mutable result object for lookup metrics.
     */
    static class LookupWithMetrics {
        MemberFineAccount found;
        int chainTraversals;

        /**
         * Creates lookup result.
         *
         * @param found account found or null
         * @param chainTraversals number of nodes traversed
         */
        LookupWithMetrics(MemberFineAccount found, int chainTraversals) {
            // Time: O(1).
            this.found = found;
            this.chainTraversals = chainTraversals;
        }
    }

    /**
     * Custom hash table implementation with resizing and chaining.
     */
    static class LibraryFineHashTableAdvanced {
        FineBucketNode[] buckets;
        int size;
        final double resizeLoadFactorThreshold;
        int resizeCount;

        /**
         * Creates hash table.
         *
         * @param initialBucketCount starting bucket count
         * @param resizeLoadFactorThreshold threshold to trigger resize
         */
        LibraryFineHashTableAdvanced(int initialBucketCount, double resizeLoadFactorThreshold) {
            // Time: O(b) for bucket array allocation.
            this.buckets = new FineBucketNode[initialBucketCount];
            this.size = 0;
            this.resizeLoadFactorThreshold = resizeLoadFactorThreshold;
            this.resizeCount = 0;
        }

        /**
         * Converts membership key to bucket index.
         *
         * @param membershipNumber key
         * @return bucket index
         */
        int indexFor(String membershipNumber) {
            // Time: O(k), key-length dependent hash computation.
            int hash = 23;
            for (int i = 0; i < membershipNumber.length(); i++) {
                hash = 31 * hash + membershipNumber.charAt(i);
            }
            return (hash & 0x7fffffff) % buckets.length;
        }

        /**
         * Inserts or updates an account by membership number.
         *
         * @param account account data to store
         */
        void put(MemberFineAccount account) {
            // Time: O(1) average, O(n) worst-case collision chain.
            int bucketIndex = indexFor(account.membershipNumber);
            FineBucketNode cursor = buckets[bucketIndex];

            while (cursor != null) {
                if (cursor.account.membershipNumber.equals(account.membershipNumber)) {
                    cursor.account.memberName = account.memberName;
                    cursor.account.borrowedBookCount = account.borrowedBookCount;
                    cursor.account.outstandingFine = account.outstandingFine;
                    return;
                }
                cursor = cursor.next;
            }

            FineBucketNode inserted = new FineBucketNode(account);
            inserted.next = buckets[bucketIndex];
            buckets[bucketIndex] = inserted;
            size++;

            if (loadFactor() > resizeLoadFactorThreshold) {
                resizeAndRehash();
            }
        }

        /**
         * Gets an account by key.
         *
         * @param membershipNumber key to search
         * @return account or null
         */
        MemberFineAccount get(String membershipNumber) {
            // Time: O(1) average, O(n) worst-case chain traversal.
            int bucketIndex = indexFor(membershipNumber);
            FineBucketNode cursor = buckets[bucketIndex];

            while (cursor != null) {
                if (cursor.account.membershipNumber.equals(membershipNumber)) {
                    return cursor.account;
                }
                cursor = cursor.next;
            }
            return null;
        }

        /**
         * Gets account plus chain traversal count for diagnostics.
         *
         * @param membershipNumber key
         * @return lookup result with metrics
         */
        LookupWithMetrics getWithMetrics(String membershipNumber) {
            // Time: O(1) average, O(n) worst-case.
            int bucketIndex = indexFor(membershipNumber);
            FineBucketNode cursor = buckets[bucketIndex];
            int traversals = 0;

            while (cursor != null) {
                traversals++;
                if (cursor.account.membershipNumber.equals(membershipNumber)) {
                    return new LookupWithMetrics(cursor.account, traversals);
                }
                cursor = cursor.next;
            }
            return new LookupWithMetrics(null, traversals);
        }

        /**
         * Checks key existence.
         *
         * @param membershipNumber key
         * @return true if present
         */
        boolean containsKey(String membershipNumber) {
            // Time: O(1) average, O(n) worst-case.
            return get(membershipNumber) != null;
        }

        /**
         * Removes account by key.
         *
         * @param membershipNumber key to delete
         * @return removed account, or null if not found
         */
        MemberFineAccount remove(String membershipNumber) {
            // Time: O(1) average, O(n) worst-case chain traversal.
            int bucketIndex = indexFor(membershipNumber);
            FineBucketNode cursor = buckets[bucketIndex];
            FineBucketNode previous = null;

            while (cursor != null) {
                if (cursor.account.membershipNumber.equals(membershipNumber)) {
                    if (previous == null) {
                        buckets[bucketIndex] = cursor.next;
                    } else {
                        previous.next = cursor.next;
                    }
                    size--;
                    return cursor.account;
                }
                previous = cursor;
                cursor = cursor.next;
            }

            return null;
        }

        /**
         * Returns current table load factor.
         *
         * @return load factor value
         */
        double loadFactor() {
            // Time: O(1).
            return (double) size / buckets.length;
        }

        /**
         * Returns current number of stored keys.
         *
         * @return size
         */
        int size() {
            // Time: O(1).
            return size;
        }

        /**
         * Internal resize + rehash operation when load factor exceeds threshold.
         */
        void resizeAndRehash() {
            // Time: O(n + b) - all entries are redistributed across new bucket array.
            FineBucketNode[] oldBuckets = buckets;
            FineBucketNode[] newBuckets = new FineBucketNode[oldBuckets.length * 2];

            buckets = newBuckets;
            int oldSize = size;
            size = 0;

            for (int i = 0; i < oldBuckets.length; i++) {
                FineBucketNode cursor = oldBuckets[i];
                while (cursor != null) {
                    // Reinsert nodes using current hash function and new bucket length.
                    putWithoutResize(cursor.account);
                    cursor = cursor.next;
                }
            }

            size = oldSize;
            resizeCount++;
        }

        /**
         * Internal insert used during rehash to avoid recursive resize triggering.
         *
         * @param account account to insert
         */
        void putWithoutResize(MemberFineAccount account) {
            // Time: O(1) average.
            int bucketIndex = indexFor(account.membershipNumber);
            FineBucketNode inserted = new FineBucketNode(account);
            inserted.next = buckets[bucketIndex];
            buckets[bucketIndex] = inserted;
        }

        /**
         * Prints bucket/chaining diagnostics.
         */
        void printDiagnostics() {
            // Time: O(b + n).
            int usedBuckets = 0;
            int maxChain = 0;
            int collisionEntries = 0;

            for (int i = 0; i < buckets.length; i++) {
                int chainLen = 0;
                FineBucketNode cursor = buckets[i];
                while (cursor != null) {
                    chainLen++;
                    cursor = cursor.next;
                }
                if (chainLen > 0) {
                    usedBuckets++;
                }
                if (chainLen > 1) {
                    collisionEntries += chainLen - 1;
                }
                if (chainLen > maxChain) {
                    maxChain = chainLen;
                }
            }

            System.out.printf("  Size=%d, Buckets=%d, LoadFactor=%.3f, Resizes=%d%n",
                    size, buckets.length, loadFactor(), resizeCount);
            System.out.printf("  UsedBuckets=%d, CollisionEntries=%d, MaxChain=%d%n",
                    usedBuckets, collisionEntries, maxChain);
        }
    }

    /**
     * Generates deterministic membership number.
     *
     * @param id numeric member id
     * @return formatted membership key
     */
    static String membershipKey(int id) {
        // Time: O(1) fixed-width formatting.
        return String.format("JPL-%06d", id);
    }

    /**
     * Populates library table with synthetic records.
     *
     * @param table custom table
     * @param builtInMap built-in map for comparison
     * @param count records to insert
     */
    static void populate(
            LibraryFineHashTableAdvanced table,
            HashMap<String, MemberFineAccount> builtInMap,
            int count) {
        // Time: O(n) average for batch insertion.
        for (int i = 1; i <= count; i++) {
            String key = membershipKey(i);
            MemberFineAccount account = new MemberFineAccount(
                    key,
                    "Member-" + i,
                    i % 6,
                    (i % 21) * 2.0);
            table.put(account);
            builtInMap.put(key, account);
        }
    }

    /**
     * Prints one lookup for desk scenario.
     *
     * @param table table instance
     * @param key membership key
     */
    static void printDeskLookup(LibraryFineHashTableAdvanced table, String key) {
        // Time: O(1) average hash lookup.
        LookupWithMetrics result = table.getWithMetrics(key);
        if (result.found == null) {
            System.out.printf("  %s -> NOT FOUND (chain traversals=%d)%n", key, result.chainTraversals);
            return;
        }
        System.out.printf("  %s -> %s fine=R%.2f books=%d (chain traversals=%d)%n",
                key,
                result.found.memberName,
                result.found.outstandingFine,
                result.found.borrowedBookCount,
                result.chainTraversals);
    }

    /**
     * Entry point with full advanced demonstration.
     *
     * @param args unused args
     */
    public static void main(String[] args) {
        System.out.println("=== Johannesburg Library Fine System (Advanced Hash Table Internals) ===");

        LibraryFineHashTableAdvanced customTable = new LibraryFineHashTableAdvanced(8, 0.75);
        HashMap<String, MemberFineAccount> builtInMap = new HashMap<>();

        System.out.println("\n-- Edge case: empty table lookup/remove --");
        printDeskLookup(customTable, membershipKey(1));
        System.out.println("  Remove missing key result: " + (customTable.remove(membershipKey(1)) == null));

        System.out.println("\n-- Single insert and duplicate update --");
        customTable.put(new MemberFineAccount(membershipKey(1), "Nomsa Dube", 2, 8.00));
        printDeskLookup(customTable, membershipKey(1));
        customTable.put(new MemberFineAccount(membershipKey(1), "Nomsa Dube", 3, 12.00));
        printDeskLookup(customTable, membershipKey(1));

        System.out.println("\n-- Bulk insert to trigger load-factor resize and rehash --");
        populate(customTable, builtInMap, 1500);
        customTable.printDiagnostics();

        System.out.println("\n-- Real librarian desk lookups --");
        printDeskLookup(customTable, membershipKey(2));
        printDeskLookup(customTable, membershipKey(617));
        printDeskLookup(customTable, membershipKey(1499));
        printDeskLookup(customTable, membershipKey(999999));

        System.out.println("\n-- containsKey and remove operations --");
        String removalKey = membershipKey(617);
        System.out.println("  contains before remove: " + customTable.containsKey(removalKey));
        MemberFineAccount removed = customTable.remove(removalKey);
        System.out.println("  removed present: " + (removed != null));
        System.out.println("  contains after remove: " + customTable.containsKey(removalKey));

        System.out.println("\n-- Compare selected values against Java HashMap built-in equivalent --");
        String[] compareKeys = {membershipKey(2), membershipKey(100), membershipKey(1300), membershipKey(888888)};
        for (int i = 0; i < compareKeys.length; i++) {
            String key = compareKeys[i];
            MemberFineAccount custom = customTable.get(key);
            MemberFineAccount builtin = builtInMap.get(key);

            boolean bothNull = custom == null && builtin == null;
            boolean bothPresentAndEqualFine = custom != null && builtin != null
                    && Math.abs(custom.outstandingFine - builtin.outstandingFine) < 0.0001;
            boolean equivalent = bothNull || bothPresentAndEqualFine;

            System.out.printf("  Key %s -> equivalent results: %b%n", key, equivalent);
        }

        System.out.println("\n-- Load factor performance note --");
        System.out.printf("  Current load factor: %.3f%n", customTable.loadFactor());
        System.out.println("  As load factor rises above 0.75 without resizing, average chain length grows,");
        System.out.println("  and lookup/insert drift from O(1) average toward O(n) worst-case behavior.");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - hashCode(): deterministic key hashing to spread membership keys across buckets.
 - equals(): exact key identity check inside collision chains.
 - collision: multiple keys landing in same bucket index.
 - chaining: linked-list nodes in each bucket to store collided entries.
 - load factor: size / bucketCount; key indicator for resize decision.
 - bucket: array slot that anchors a chain.
 - HashMap internals: bucket array, node chains, resize/rehash when threshold exceeded.
 - O(1) average: expected constant-time put/get with healthy distribution and resizing.

 Big-O complexity of operations:
 - indexFor: O(k), k = key length.
 - put: O(1) average, O(n) worst case (single long chain).
 - get / getWithMetrics / containsKey: O(1) average, O(n) worst case.
 - remove: O(1) average, O(n) worst case.
 - loadFactor / size: O(1).
 - resizeAndRehash: O(n + b), where n entries are reinserted and b old buckets scanned.
 - printDiagnostics: O(n + b).
 - populate: O(n) average batch insertion.

 Interview questions this code prepares you for:
 - Why does Java HashMap resize around load factor 0.75?
 - Why must equals() be checked even after bucket index match?
 - What is rehashing and why is it expensive but necessary?
 - What causes O(n) worst-case behavior in hash tables?
 - How does chaining compare with open addressing under high load?

 Common mistake and how this code avoids it:
 - Mistake: forgetting to rehash existing entries on resize, which makes lookups fail.
 - Avoided here: resizeAndRehash() reinserts all nodes into the new bucket array.

 Comparison guidance:
 - Use custom hash tables to understand internals, tune behavior, or meet learning goals.
 - Use Java HashMap in production for tested implementation and optimized edge-case handling.
 - Prefer TreeMap when sorted iteration/range queries are required.
*/