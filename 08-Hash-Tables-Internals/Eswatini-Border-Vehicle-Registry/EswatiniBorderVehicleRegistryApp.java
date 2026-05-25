/*
 The Eswatini Department of Transport stores 280,000 registered vehicles by licence plate.
 At the Ngwenya border post, officers must verify registrations quickly while queues move at up to
 600 vehicles per hour. A linear scan through every record is too slow for this operational target.
 Developer Precious implements a hash map from scratch: each plate string is converted into an
 integer hash, reduced to a bucket index, and stored in that bucket chain. Lookups are O(1) on
 average when the hash spreads keys well. She also demonstrates a deliberately poor hash function
 that creates many collisions and pushes lookup performance toward O(n) along long bucket chains.
*/

/**
 * Intermediate hash-map internals demo for Eswatini border vehicle verification.
 */
public class EswatiniBorderVehicleRegistryApp {

    /**
     * Represents one registered vehicle.
     */
    static class VehicleRegistrationRecord {
        String licencePlate;
        String ownerName;
        String vehicleType;
        boolean active;

        /**
         * Creates one vehicle registration record.
         *
         * @param licencePlate vehicle licence plate (key)
         * @param ownerName owner full name
         * @param vehicleType sedan, truck, taxi, etc.
         * @param active whether registration is currently valid
         */
        VehicleRegistrationRecord(String licencePlate, String ownerName, String vehicleType, boolean active) {
            // Time: O(1) - constant assignments.
            this.licencePlate = licencePlate;
            this.ownerName = ownerName;
            this.vehicleType = vehicleType;
            this.active = active;
        }

        /**
         * Hash-style code from plate characters.
         *
         * @return hash value
         */
        @Override
        public int hashCode() {
            // Time: O(k) where k is plate length.
            int hash = 7;
            for (int i = 0; i < licencePlate.length(); i++) {
                hash = hash * 37 + licencePlate.charAt(i);
            }
            return hash;
        }

        /**
         * Key equality by licence plate identity.
         *
         * @param obj object to compare
         * @return true if same plate key
         */
        @Override
        public boolean equals(Object obj) {
            // Time: O(k) worst case string comparison.
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof VehicleRegistrationRecord)) {
                return false;
            }
            VehicleRegistrationRecord other = (VehicleRegistrationRecord) obj;
            return this.licencePlate.equals(other.licencePlate);
        }
    }

    /**
     * Node in bucket chain.
     */
    static class VehicleBucketNode {
        VehicleRegistrationRecord record;
        VehicleBucketNode next;

        /**
         * Creates chain node.
         *
         * @param record vehicle record
         */
        VehicleBucketNode(VehicleRegistrationRecord record) {
            // Time: O(1).
            this.record = record;
            this.next = null;
        }
    }

    /**
     * Lookup response with metrics for demonstration.
     */
    static class VehicleLookupResult {
        VehicleRegistrationRecord found;
        int chainSteps;

        /**
         * Creates lookup result.
         *
         * @param found found record or null
         * @param chainSteps nodes traversed in the chain
         */
        VehicleLookupResult(VehicleRegistrationRecord found, int chainSteps) {
            // Time: O(1).
            this.found = found;
            this.chainSteps = chainSteps;
        }
    }

    /**
     * Custom hash table for vehicle registry with optional poor hash mode.
     */
    static class VehicleRegistryHashMap {
        VehicleBucketNode[] buckets;
        int count;
        boolean usePoorHash;

        /**
         * Creates registry table.
         *
         * @param bucketCount number of buckets
         * @param usePoorHash if true, uses intentionally weak hash function
         */
        VehicleRegistryHashMap(int bucketCount, boolean usePoorHash) {
            // Time: O(b) for bucket-array allocation.
            this.buckets = new VehicleBucketNode[bucketCount];
            this.count = 0;
            this.usePoorHash = usePoorHash;
        }

        /**
         * Converts plate into bucket index.
         *
         * @param licencePlate key plate
         * @return bucket index
         */
        int indexFor(String licencePlate) {
            // Time: O(k) where k is plate length.
            int hash;

            if (usePoorHash) {
                // Poor hash: only first character drives index, causing heavy clustering.
                hash = licencePlate.charAt(0);
            } else {
                // Better hash: polynomial rolling hash over full plate string.
                hash = 11;
                for (int i = 0; i < licencePlate.length(); i++) {
                    hash = hash * 41 + licencePlate.charAt(i);
                }
            }

            return (hash & 0x7fffffff) % buckets.length;
        }

        /**
         * Inserts or updates a vehicle registration.
         *
         * @param record record to store
         */
        void put(VehicleRegistrationRecord record) {
            // Time: O(1) average, O(n) worst collision chain.
            int index = indexFor(record.licencePlate);
            VehicleBucketNode cursor = buckets[index];

            while (cursor != null) {
                if (cursor.record.licencePlate.equals(record.licencePlate)) {
                    cursor.record.ownerName = record.ownerName;
                    cursor.record.vehicleType = record.vehicleType;
                    cursor.record.active = record.active;
                    return;
                }
                cursor = cursor.next;
            }

            VehicleBucketNode inserted = new VehicleBucketNode(record);
            inserted.next = buckets[index];
            buckets[index] = inserted;
            count++;
        }

        /**
         * Looks up a registration by licence plate.
         *
         * @param licencePlate key to search
         * @return result containing record and traversal steps
         */
        VehicleLookupResult get(String licencePlate) {
            // Time: O(1) average, O(n) worst case chain traversal.
            int index = indexFor(licencePlate);
            VehicleBucketNode cursor = buckets[index];
            int steps = 0;

            while (cursor != null) {
                steps++;
                if (cursor.record.licencePlate.equals(licencePlate)) {
                    return new VehicleLookupResult(cursor.record, steps);
                }
                cursor = cursor.next;
            }
            return new VehicleLookupResult(null, steps);
        }

        /**
         * Returns current load factor.
         *
         * @return count / bucket count
         */
        double loadFactor() {
            // Time: O(1).
            return (double) count / buckets.length;
        }

        /**
         * Prints distribution quality metrics.
         */
        void printDistributionStats() {
            // Time: O(b + n) - full scan of buckets and nodes.
            int usedBuckets = 0;
            int maxChain = 0;
            int collisionEntries = 0;

            for (int i = 0; i < buckets.length; i++) {
                int chainLength = 0;
                VehicleBucketNode cursor = buckets[i];
                while (cursor != null) {
                    chainLength++;
                    cursor = cursor.next;
                }
                if (chainLength > 0) {
                    usedBuckets++;
                }
                if (chainLength > 1) {
                    collisionEntries += (chainLength - 1);
                }
                if (chainLength > maxChain) {
                    maxChain = chainLength;
                }
            }

            System.out.printf("  Table mode: %s%n", usePoorHash ? "POOR HASH" : "GOOD HASH");
            System.out.printf("  Records: %d, Buckets: %d, Load factor: %.2f%n", count, buckets.length, loadFactor());
            System.out.printf("  Used buckets: %d, Collision entries: %d, Max chain: %d%n",
                    usedBuckets, collisionEntries, maxChain);
        }
    }

    /**
     * Prints one border verification query.
     *
     * @param map table to query
     * @param plate plate to verify
     */
    static void printBorderCheck(VehicleRegistryHashMap map, String plate) {
        // Time: O(1) average hash lookup.
        VehicleLookupResult result = map.get(plate);
        if (result.found == null) {
            System.out.printf("  Plate %s not found (steps=%d) -> HOLD FOR MANUAL CHECK%n", plate, result.chainSteps);
            return;
        }

        String status = result.found.active ? "ACTIVE" : "EXPIRED";
        System.out.printf("  Plate %s owner=%s type=%s status=%s (steps=%d)%n",
                result.found.licencePlate,
                result.found.ownerName,
                result.found.vehicleType,
                status,
                result.chainSteps);
    }

    /**
     * Builds deterministic demo data.
     *
     * @param map destination hash table
     * @param recordCount records to add
     */
    static void populateDemoRegistry(VehicleRegistryHashMap map, int recordCount) {
        // Time: O(n) inserts, each O(1) average.
        for (int i = 0; i < recordCount; i++) {
            String province = (i % 2 == 0) ? "GP" : "MP";
            String plate = String.format("NSD %03d %s", i % 1000, province);
            map.put(new VehicleRegistrationRecord(
                    plate,
                    "Owner-" + i,
                    (i % 3 == 0) ? "TRUCK" : "SEDAN",
                    i % 11 != 0));
        }
    }

    /**
     * Entry point with story-driven checks.
     *
     * @param args unused args
     */
    public static void main(String[] args) {
        System.out.println("=== Eswatini Border Vehicle Registry (Intermediate Hash Map Internals) ===");

        VehicleRegistryHashMap goodHashMap = new VehicleRegistryHashMap(103, false);
        VehicleRegistryHashMap poorHashMap = new VehicleRegistryHashMap(103, true);

        System.out.println("\n-- Edge case: lookup in empty registry --");
        printBorderCheck(goodHashMap, "NSD 247 GP");

        System.out.println("\n-- Single record insert and lookup --");
        goodHashMap.put(new VehicleRegistrationRecord("NSD 247 GP", "Lwazi Dlamini", "SUV", true));
        printBorderCheck(goodHashMap, "NSD 247 GP");

        System.out.println("\n-- Duplicate key update --");
        goodHashMap.put(new VehicleRegistrationRecord("NSD 247 GP", "Lwazi Dlamini", "SUV", false));
        printBorderCheck(goodHashMap, "NSD 247 GP");

        System.out.println("\n-- Populate both maps with same records for comparison --");
        int demoSize = 600;
        populateDemoRegistry(goodHashMap, demoSize);
        populateDemoRegistry(poorHashMap, demoSize);

        goodHashMap.printDistributionStats();
        poorHashMap.printDistributionStats();

        System.out.println("\n-- Hash mapping demonstration for plate NSD 247 GP --");
        int goodIndex = goodHashMap.indexFor("NSD 247 GP");
        int poorIndex = poorHashMap.indexFor("NSD 247 GP");
        System.out.println("  Good hash bucket index: " + goodIndex);
        System.out.println("  Poor hash bucket index: " + poorIndex);

        System.out.println("\n-- Peak-hour border checks (good hash vs poor hash) --");
        String[] queries = {"NSD 247 GP", "NSD 500 MP", "NSD 999 GP", "NSD 001 MP", "ZZZ 000 GP"};

        long goodStart = System.nanoTime();
        for (int i = 0; i < queries.length; i++) {
            printBorderCheck(goodHashMap, queries[i]);
        }
        long goodTimeNs = System.nanoTime() - goodStart;

        System.out.println("  --- same queries with poor hash ---");
        long poorStart = System.nanoTime();
        for (int i = 0; i < queries.length; i++) {
            printBorderCheck(poorHashMap, queries[i]);
        }
        long poorTimeNs = System.nanoTime() - poorStart;

        System.out.printf("\n  Good hash query time: %d ns%n", goodTimeNs);
        System.out.printf("  Poor hash query time: %d ns%n", poorTimeNs);
        System.out.println("  Higher collision chains in poor hashing push lookup toward O(n). ");
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - hashCode(): transforms key text into an integer distribution space.
 - equals(): disambiguates keys that collide into same bucket.
 - collision: different plates share one bucket index.
 - chaining: linked-list nodes per bucket store collided records.
 - load factor: entries / buckets; higher values usually mean longer chains.
 - bucket: one array slot that points to a collision chain.
 - HashMap internals: buckets + hash spreading + key equality checks.
 - O(1) average: expected constant-time get/put with good hashing.

 Big-O of implemented operations:
 - indexFor: O(k) where k = plate length.
 - put: O(1) average, O(n) worst case with severe collisions.
 - get: O(1) average, O(n) worst case chain traversal.
 - loadFactor: O(1).
 - printDistributionStats: O(b + n).
 - populateDemoRegistry: O(n) average overall.

 Interview questions:
 - Why can two different keys have the same hash bucket?
 - Why does poor hashing degrade performance toward O(n)?
 - What role does load factor play in hash-table performance?
 - Why must equals() be checked after bucket index match?
 - How would resizing and rehashing improve performance over time?

 Common mistake and prevention:
 - Mistake: assuming hash collisions are rare enough to ignore chain traversal logic.
 - Avoided here: every get/put traverses bucket chains and checks exact key equality.

 Comparison guidance:
 - Use hash maps for exact key lookup by plate/membership ID at high transaction rates.
 - Use tree maps when ordered iteration/range queries are required instead of raw lookup speed.
*/