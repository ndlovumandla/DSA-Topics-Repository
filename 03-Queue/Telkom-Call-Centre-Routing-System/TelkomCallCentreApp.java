/*
 Telkom's national call centre handles tens of thousands of inbound calls every day across technical
 faults, billing queries, and new connections. When every agent in a tier is busy, new calls wait in
 a line and must be answered strictly in the order they arrived so no customer can jump ahead. That
 waiting pattern is exactly what a queue models: the first caller to enter the line is the first one
 to be served. Developer Mpho builds one FIFO queue per tier from scratch so the routing engine can
 enqueue incoming calls, dequeue the next caller when an agent becomes free, and report queue depth
 and estimated wait time in real time. A linked queue is a strong fit here because it gives O(1)
 enqueue and dequeue without shifting any stored calls.
*/

/**
 * Simulates Telkom's national call centre routing system using custom FIFO queues.
 * Calls are routed into separate tier queues and served strictly in arrival order.
 */
public class TelkomCallCentreApp {

    /**
     * Describes the type of customer support request being handled.
     * Each tier has its own waiting queue and agent pool.
     */
    enum ServiceTier {
        TECHNICAL_FAULTS,
        BILLING_QUERIES,
        NEW_CONNECTIONS
    }

    /**
     * Represents one inbound call waiting in a queue.
     * The node stores call details plus a link to the next caller in line.
     */
    static class CallRecord {
        String callId;
        String callerName;
        String issueSummary;
        ServiceTier serviceTier;
        CallRecord nextCaller;

        /**
         * Creates a call record for the waiting line.
         *
         * @param callId unique call reference
         * @param callerName caller's name
         * @param issueSummary brief reason for the call
         * @param serviceTier queue tier assigned to this call
         */
        CallRecord(String callId, String callerName, String issueSummary, ServiceTier serviceTier) {
            // Time: O(1) — constant field assignments only.
            this.callId = callId;
            this.callerName = callerName;
            this.issueSummary = issueSummary;
            this.serviceTier = serviceTier;
            this.nextCaller = null;
        }
    }

    /**
     * One FIFO queue for a specific call-centre tier.
     * Calls join at the back and are answered from the front.
     */
    static class TierCallQueue {
        private CallRecord queueFront;
        private CallRecord queueBack;
        private int waitingCallCount;

        /**
         * Starts an empty queue for one support tier.
         */
        TierCallQueue() {
            // Time: O(1) — initialize references and counter.
            this.queueFront = null;
            this.queueBack = null;
            this.waitingCallCount = 0;
        }

        /**
         * Checks whether this tier has no waiting callers.
         *
         * @return true if the queue is empty
         */
        boolean isEmpty() {
            // Time: O(1) — single pointer check.
            return queueFront == null;
        }

        /**
         * Adds a call to the back of the tier queue.
         * FIFO means the caller stays behind all earlier arrivals.
         *
         * @param callId unique call reference
         * @param callerName caller's name
         * @param issueSummary short description of the request
         * @param tier support tier the call belongs to
         */
        void enqueueCall(String callId, String callerName, String issueSummary, ServiceTier tier) {
            // Time: O(1) — tail insertion with constant pointer updates.
            CallRecord incomingCall = new CallRecord(callId, callerName, issueSummary, tier);

            if (isEmpty()) {
                queueFront = incomingCall;
                queueBack = incomingCall;
            } else {
                // Linked queue concept: attach the new node to the existing tail.
                queueBack.nextCaller = incomingCall;
                queueBack = incomingCall;
            }

            waitingCallCount++;
            System.out.println("[ENQUEUE] " + callId + " | " + callerName + " | " + issueSummary);
        }

        /**
         * Removes the next caller from the front of the queue.
         * This is the FIFO rule in action: oldest waiting call is answered first.
         *
         * @return the answered call record, or null if the queue is empty
         */
        CallRecord dequeueNextCall() {
            // Time: O(1) — remove the head and advance front one node.
            if (isEmpty()) {
                System.out.println("[DEQUEUE FAILED] Tier queue is empty.");
                return null;
            }

            CallRecord answeredCall = queueFront;
            queueFront = queueFront.nextCaller;
            waitingCallCount--;

            if (waitingCallCount == 0) {
                queueBack = null;
            }

            System.out.println("[DEQUEUE] " + answeredCall.callId + " answered by an available agent.");
            return answeredCall;
        }

        /**
         * Looks at the next call to be answered without removing it.
         *
         * @return front call record or null if empty
         */
        CallRecord peekNextCall() {
            // Time: O(1) — direct access to the front pointer.
            return queueFront;
        }

        /**
         * Cancels a call by ID if the caller hangs up while waiting.
         *
         * @param callId call reference to remove
         * @return true when a matching call was removed
         */
        boolean cancelCallById(String callId) {
            // Time: O(n) — may scan the full queue to find the matching call.
            if (isEmpty()) {
                System.out.println("[CANCEL FAILED] Queue empty; cannot cancel " + callId + ".");
                return false;
            }

            if (queueFront.callId.equalsIgnoreCase(callId)) {
                queueFront = queueFront.nextCaller;
                waitingCallCount--;
                if (waitingCallCount == 0) {
                    queueBack = null;
                }
                System.out.println("[CANCELLED] Removed front call " + callId + ".");
                return true;
            }

            CallRecord previousCall = queueFront;
            CallRecord currentCall = queueFront.nextCaller;
            while (currentCall != null && !currentCall.callId.equalsIgnoreCase(callId)) {
                previousCall = currentCall;
                currentCall = currentCall.nextCaller;
            }

            if (currentCall == null) {
                System.out.println("[CANCEL FAILED] Call " + callId + " not found in queue.");
                return false;
            }

            previousCall.nextCaller = currentCall.nextCaller;
            if (currentCall == queueBack) {
                queueBack = previousCall;
            }
            waitingCallCount--;
            System.out.println("[CANCELLED] Removed waiting call " + callId + ".");
            return true;
        }

        /**
         * Returns the number of waiting calls in this tier.
         *
         * @return queue depth
         */
        int getWaitingCallCount() {
            // Time: O(1) — returns a stored counter.
            return waitingCallCount;
        }

        /**
         * Estimates the wait time in minutes for the next caller in this tier.
         *
         * @param averageHandleMinutesPerCall average minutes an agent spends per call
         * @param agentCount number of available agents in the tier
         * @return estimated minutes until the front caller is served
         */
        int estimateWaitMinutes(int averageHandleMinutesPerCall, int agentCount) {
            // Time: O(1) — simple arithmetic based on queue depth.
            if (agentCount <= 0) {
                return -1;
            }

            int callsAhead = waitingCallCount - agentCount;
            if (callsAhead <= 0) {
                return 0;
            }
            return (callsAhead * averageHandleMinutesPerCall) / agentCount;
        }

        /**
         * Prints the queue from front to back for dashboard monitoring.
         *
         * @param tierLabel display name for the support tier
         */
        void printQueueSnapshot(String tierLabel) {
            // Time: O(n) — each waiting call is printed once.
            System.out.println("\n" + tierLabel + " queue [" + waitingCallCount + " waiting]");

            if (isEmpty()) {
                System.out.println("  Queue is empty.");
                return;
            }

            int position = 1;
            CallRecord cursor = queueFront;
            while (cursor != null) {
                System.out.println("  " + position + ". " + cursor.callId
                        + " | " + cursor.callerName + " | " + cursor.issueSummary);
                cursor = cursor.nextCaller;
                position++;
            }
        }
    }

    /**
     * Combines the three tier queues into one routing engine.
     * The system can enqueue, dequeue, cancel, and report queue depth per tier.
     */
    static class CallRoutingSystem {
        private final TierCallQueue technicalFaultQueue;
        private final TierCallQueue billingQueue;
        private final TierCallQueue newConnectionQueue;
        private final int technicalAgents;
        private final int billingAgents;
        private final int newConnectionAgents;

        /**
         * Creates a routing system with fixed agent pools per tier.
         */
        CallRoutingSystem(int technicalAgents, int billingAgents, int newConnectionAgents) {
            // Time: O(1) — create three queues and store three counts.
            this.technicalFaultQueue = new TierCallQueue();
            this.billingQueue = new TierCallQueue();
            this.newConnectionQueue = new TierCallQueue();
            this.technicalAgents = technicalAgents;
            this.billingAgents = billingAgents;
            this.newConnectionAgents = newConnectionAgents;
        }

        /**
         * Routes a new call into the correct tier queue.
         *
         * @param tier support tier for the call
         * @param callId unique call reference
         * @param callerName caller name
         * @param issueSummary issue summary
         */
        void routeIncomingCall(ServiceTier tier, String callId, String callerName, String issueSummary) {
            // Time: O(1) — tier selection and one enqueue.
            queueForTier(tier).enqueueCall(callId, callerName, issueSummary, tier);
        }

        /**
         * Answers the next call in the chosen tier.
         *
         * @param tier support tier being served now
         */
        void answerNextCall(ServiceTier tier) {
            // Time: O(1) — direct dequeue from the selected queue.
            CallRecord answeredCall = queueForTier(tier).dequeueNextCall();
            if (answeredCall != null) {
                System.out.println("[ANSWERED] Agent handled " + answeredCall.callId
                        + " from " + tier + "");
            }
        }

        /**
         * Cancels a call across all tiers if a caller disconnects.
         *
         * @param callId call reference to remove
         */
        void cancelCallEverywhere(String callId) {
            // Time: O(n) — in the worst case, each tier may need to scan its queue.
            if (technicalFaultQueue.cancelCallById(callId)) return;
            if (billingQueue.cancelCallById(callId)) return;
            newConnectionQueue.cancelCallById(callId);
        }

        /**
         * Prints a dashboard with queue depth and estimated wait times.
         */
        void printRoutingDashboard() {
            // Time: O(n) overall — printing each tier snapshot visits all waiting calls.
            System.out.println("\n=== Telkom Routing Dashboard ===");
            technicalFaultQueue.printQueueSnapshot("Technical faults");
            billingQueue.printQueueSnapshot("Billing queries");
            newConnectionQueue.printQueueSnapshot("New connections");

            System.out.println("\nEstimated waits (minutes):");
            System.out.println("  Technical faults: " + technicalFaultQueue.estimateWaitMinutes(4, technicalAgents));
            System.out.println("  Billing queries: " + billingQueue.estimateWaitMinutes(3, billingAgents));
            System.out.println("  New connections: " + newConnectionQueue.estimateWaitMinutes(5, newConnectionAgents));
        }

        /**
         * Returns the queue that belongs to the requested tier.
         *
         * @param tier support tier requested
         * @return the matching tier queue
         */
        private TierCallQueue queueForTier(ServiceTier tier) {
            // Time: O(1) — only three fixed branches.
            if (tier == ServiceTier.TECHNICAL_FAULTS) {
                return technicalFaultQueue;
            }
            if (tier == ServiceTier.BILLING_QUERIES) {
                return billingQueue;
            }
            return newConnectionQueue;
        }
    }

    /**
     * Drives a story-based demonstration of Telkom's routing engine.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall — dominated by queue printing.
        CallRoutingSystem routingSystem = new CallRoutingSystem(3, 2, 1);

        System.out.println("=== Telkom Call Centre Routing System ===");

        // Edge case: empty queue and not-found cancellation.
        routingSystem.answerNextCall(ServiceTier.TECHNICAL_FAULTS);
        routingSystem.cancelCallEverywhere("TC-999");

        // Calls arrive across tiers.
        System.out.println("\n-- Incoming calls during the morning rush --");
        routingSystem.routeIncomingCall(ServiceTier.TECHNICAL_FAULTS, "TC-101", "Lebo", "No fibre connectivity");
        routingSystem.routeIncomingCall(ServiceTier.BILLING_QUERIES, "BL-201", "Thandi", "Incorrect invoice amount");
        routingSystem.routeIncomingCall(ServiceTier.NEW_CONNECTIONS, "NC-301", "Musa", "New line installation");
        routingSystem.routeIncomingCall(ServiceTier.TECHNICAL_FAULTS, "TC-102", "Lebo", "Router power issue");
        routingSystem.routeIncomingCall(ServiceTier.TECHNICAL_FAULTS, "TC-103", "Jabu", "Dropped call line");
        routingSystem.routeIncomingCall(ServiceTier.BILLING_QUERIES, "BL-202", "Nandi", "Data bundle dispute");

        // Duplicate caller name edge case: same name, different calls, both preserved by FIFO.
        routingSystem.routeIncomingCall(ServiceTier.BILLING_QUERIES, "BL-203", "Thandi", "Second billing follow-up");

        routingSystem.printRoutingDashboard();

        // Single element behavior in the new connections tier.
        routingSystem.answerNextCall(ServiceTier.NEW_CONNECTIONS);

        // Answer calls from busy technical and billing queues.
        routingSystem.answerNextCall(ServiceTier.TECHNICAL_FAULTS);
        routingSystem.answerNextCall(ServiceTier.BILLING_QUERIES);

        // Caller hangs up while waiting.
        routingSystem.cancelCallEverywhere("BL-203");

        routingSystem.printRoutingDashboard();

        // Drain remaining technical calls to show FIFO order.
        routingSystem.answerNextCall(ServiceTier.TECHNICAL_FAULTS);
        routingSystem.answerNextCall(ServiceTier.TECHNICAL_FAULTS);
        routingSystem.answerNextCall(ServiceTier.TECHNICAL_FAULTS);

        routingSystem.printRoutingDashboard();
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - FIFO: Calls are served in the same order they arrived.
 - enqueue: Add a call to the back of the waiting line.
 - dequeue: Remove the next call from the front of the waiting line.
 - linked queue: A queue built with nodes and head/tail pointers for O(1) ends.
 - queue depth: How many calls are waiting right now.
 - estimated wait time: A quick estimate based on depth and agent count.
 - BFS: Not the primary feature here, but the same FIFO principle underpins breadth-first search.

 Big-O for every operation implemented:
 - isEmpty: O(1) because it checks one pointer.
 - enqueueCall: O(1) because it updates the tail and count only.
 - dequeueNextCall: O(1) because it removes the front and updates the head.
 - peekNextCall: O(1) because it reads the front pointer.
 - cancelCallById: O(n) because it may scan the entire queue.
 - getWaitingCallCount: O(1) because it returns a stored counter.
 - estimateWaitMinutes: O(1) because it uses arithmetic on stored values.
 - printQueueSnapshot: O(n) because it prints every waiting call once.
 - routeIncomingCall: O(1) because it routes to one tier queue and enqueues.
 - answerNextCall: O(1) because it dequeues from one tier queue.
 - cancelCallEverywhere: O(n) overall because it may scan each tier queue.
 - printRoutingDashboard: O(n) overall because it prints every waiting call in all tiers.

 Interview questions this code prepares you for:
 - Why does FIFO matter in a customer call centre?
 - How do you build a queue from scratch using head and tail pointers?
 - Why are enqueue and dequeue O(1) in a linked queue?
 - How would you estimate waiting time from queue depth?
 - What happens when a customer hangs up while still waiting?

 Most common mistake and how this code avoids it:
 - Mistake: Forgetting to reset the tail pointer when the last element is dequeued.
 - Avoided: dequeueNextCall() explicitly sets queueBack to null when the queue becomes empty.

 When to use this vs the common alternative:
 - Use a queue when arrival order must be preserved, such as call centres, printer jobs, or BFS.
 - Use a stack when the newest item must be handled first.
 - Use a priority queue when urgency matters more than arrival time.
*/
