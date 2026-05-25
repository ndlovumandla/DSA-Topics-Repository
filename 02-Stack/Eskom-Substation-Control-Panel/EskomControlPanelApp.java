/*
 Eskom's Megawatt Park control room manages the South African national electricity grid, where every
 operator action — isolating a substation, rerouting load, increasing generation — is a command that
 changes the state of the grid. During a 2019 emergency, an operator triggered a sequence of commands
 that cascaded into a wide-area fault; the recovery team had to manually trace and reverse each action
 in exact reverse order. This incident exposed the need for a formal undo engine built on a stack.
 A stack is the correct structure because it enforces LIFO (Last In, First Out) order: the most recent
 command is always the first to be undone, which is the only safe recovery sequence for interdependent
 grid operations. Developer Ayanda implements the command stack from scratch — no java.util.Stack —
 to make the LIFO property and its cost visible to every engineer on the team.
*/

/**
 * Simulates Eskom's control-panel undo engine using a custom LIFO stack of grid commands.
 * Every command executed is pushed; every undo pops and reverses the last command.
 */
public class EskomControlPanelApp {

    /**
     * Represents one operator action logged to the grid command stack.
     * Each node holds command data and a link to the command executed before it.
     */
    static class GridCommandNode {
        String commandCode;       // short identifier, e.g. "ISO-SOWETO-01"
        String commandDescription;
        String reversalDescription;
        GridCommandNode previousCommand; // link toward the bottom of the stack

        /**
         * Creates a new command ready to be pushed onto the control-panel stack.
         *
         * @param commandCode        unique short code for this grid action
         * @param commandDescription what the command does to the grid
         * @param reversalDescription what reversing this command does
         */
        GridCommandNode(String commandCode, String commandDescription, String reversalDescription) {
            // Time: O(1) — constant field assignments.
            this.commandCode         = commandCode;
            this.commandDescription  = commandDescription;
            this.reversalDescription = reversalDescription;
            this.previousCommand     = null;
        }
    }

    /**
     * LIFO stack of grid commands backing Eskom's undo engine.
     * The stackTop always points to the most recently executed command.
     */
    static class GridCommandStack {
        private GridCommandNode stackTop;  // head of the internal linked list — top of stack
        private int commandCount;

        /**
         * Initialises an empty command log at the start of an operator shift.
         */
        GridCommandStack() {
            // Time: O(1) — two fields initialised.
            this.stackTop     = null;
            this.commandCount = 0;
        }

        /**
         * Reports whether no commands have been executed yet this session.
         *
         * @return true when the stack holds no nodes
         */
        boolean isStackEmpty() {
            // Time: O(1) — single pointer check.
            return stackTop == null;
        }

        /**
         * Executes and records a grid command by pushing it onto the stack.
         * LIFO concept: the newest command lands on top and is first to be undone.
         *
         * @param commandCode        unique identifier for this action
         * @param commandDescription forward description of the grid change
         * @param reversalDescription description of how to reverse this action
         */
        void executeCommand(String commandCode, String commandDescription, String reversalDescription) {
            // Time: O(1) — constant pointer updates regardless of stack depth.
            GridCommandNode newCommand = new GridCommandNode(commandCode, commandDescription, reversalDescription);

            // Push: new node points back to old top, then becomes new top.
            newCommand.previousCommand = stackTop;
            stackTop = newCommand;
            commandCount++;

            System.out.println("[EXECUTE] " + commandCode + " — " + commandDescription);
        }

        /**
         * Returns the top command without removing it, so operators can review the last action.
         * Peek is a read-only inspection that does not change the stack state.
         *
         * @return the topmost GridCommandNode, or null if empty
         */
        GridCommandNode peekLastCommand() {
            // Time: O(1) — direct access to stack-top pointer.
            return stackTop;
        }

        /**
         * Undoes the most recently executed command by popping it from the stack.
         * Pop removes the top node and moves the top pointer down to the previous command.
         */
        void undoLastCommand() {
            // Time: O(1) — constant pointer update; no traversal.
            if (isStackEmpty()) {
                // Edge case: undo attempted when no commands have been logged.
                System.out.println("[UNDO FAILED] No commands on stack — nothing to undo.");
                return;
            }

            GridCommandNode commandToReverse = stackTop;

            // Pop: move top pointer down to the command beneath the removed node.
            stackTop = stackTop.previousCommand;
            commandCount--;

            System.out.println("[UNDO] Reversing " + commandToReverse.commandCode
                    + " — " + commandToReverse.reversalDescription);
        }

        /**
         * Performs an emergency full rollback by popping and reversing every logged command.
         * Demonstrates that LIFO order naturally produces the correct reversal sequence.
         */
        void emergencyRollbackAll() {
            // Time: O(n) — pops every node once.
            System.out.println("\n[EMERGENCY ROLLBACK] Reversing all " + commandCount + " commands in LIFO order:");

            if (isStackEmpty()) {
                // Edge case: rollback triggered on an already-empty stack.
                System.out.println("  Stack is empty — grid is already at baseline state.");
                return;
            }

            // Iterative pop: each call to undoLastCommand is O(1); the loop runs n times.
            while (!isStackEmpty()) {
                undoLastCommand();
            }

            System.out.println("[ROLLBACK COMPLETE] Grid restored to pre-emergency state.");
        }

        /**
         * Traverses the stack from top to bottom to print a full command audit trail.
         * Uses the previousCommand links — equivalent to traversing a singly linked list.
         */
        void printCommandAuditTrail() {
            // Time: O(n) — visits every node once.
            System.out.println("\n--- Command Audit Trail (most recent first) [" + commandCount + " commands] ---");

            if (isStackEmpty()) {
                System.out.println("  No commands recorded.");
                return;
            }

            GridCommandNode cursor = stackTop;
            int position = 1;
            while (cursor != null) {
                System.out.println("  " + position + ". " + cursor.commandCode
                        + " | " + cursor.commandDescription);
                cursor = cursor.previousCommand;  // traverse via next pointer toward base
                position++;
            }
        }

        /**
         * Returns how many commands are currently on the stack.
         *
         * @return current stack depth
         */
        int getCommandCount() {
            // Time: O(1) — returns stored counter.
            return commandCount;
        }
    }

    /**
     * Demonstrates the Eskom undo engine: operator executes commands, peeks, undoes, and
     * triggers a full emergency rollback, with edge cases tested throughout.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) overall — dominated by the rollback traversal.
        GridCommandStack controlPanelStack = new GridCommandStack();

        System.out.println("=== Eskom Megawatt Park — Control Panel Undo Engine ===");

        // Edge case: undo and peek on an empty stack.
        System.out.println("\n-- Edge case: operations on empty stack --");
        controlPanelStack.undoLastCommand();
        System.out.println("[PEEK] Last command: "
                + (controlPanelStack.peekLastCommand() == null ? "none" : controlPanelStack.peekLastCommand().commandCode));
        controlPanelStack.printCommandAuditTrail();

        // Operator executes a sequence of grid commands.
        System.out.println("\n-- Operator executes grid commands --");
        controlPanelStack.executeCommand("LOAD-INC-01",  "Increase load on Johannesburg North feeder",   "Decrease load on Johannesburg North feeder");
        controlPanelStack.executeCommand("REROUTE-02",   "Reroute Soweto supply via Lenasia substation",  "Restore Soweto supply to direct route");
        controlPanelStack.executeCommand("ISO-VANDERBIJL", "Isolate Vanderbijlpark substation",           "Re-energise Vanderbijlpark substation");
        controlPanelStack.executeCommand("GEN-BOOST-03", "Boost Kusile generation unit 3 by 200 MW",      "Reduce Kusile generation unit 3 by 200 MW");

        // Peek at the most recent command without removing it.
        GridCommandNode latestCommand = controlPanelStack.peekLastCommand();
        System.out.println("\n[PEEK] Most recent command on stack: "
                + latestCommand.commandCode + " — " + latestCommand.commandDescription);

        controlPanelStack.printCommandAuditTrail();

        // Single undo — reverses only the last action.
        System.out.println("\n-- Operator realises GEN-BOOST-03 was wrong; single undo --");
        controlPanelStack.undoLastCommand();
        controlPanelStack.printCommandAuditTrail();

        // Emergency cascade: undo everything remaining in reverse order.
        controlPanelStack.emergencyRollbackAll();

        // Edge case: second rollback on now-empty stack.
        controlPanelStack.emergencyRollbackAll();

        System.out.println("\nFinal stack depth: " + controlPanelStack.getCommandCount());
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - LIFO:             Last In, First Out — the most recently pushed item is always retrieved first.
 - push:             Add a new command to the top of the stack in O(1) time.
 - pop:              Remove and return the top command in O(1) time.
 - peek:             Inspect the top command without removing it, in O(1) time.
 - call stack:       The JVM uses an implicit stack just like this to track active method calls.
 - undo mechanism:   Pushing actions and popping them reverses work in the exact safe order.
 - bracket matching: A stack can validate nested paired symbols by pushing opens and popping on closes.

 Big-O for every operation:
 - isStackEmpty:         O(1) — checks one pointer; independent of stack size.
 - executeCommand (push):O(1) — rewires a constant number of pointers.
 - peekLastCommand:      O(1) — direct read of the top pointer.
 - undoLastCommand (pop):O(1) — moves top pointer one step; no traversal.
 - emergencyRollbackAll: O(n) — calls pop n times, each O(1).
 - printCommandAuditTrail:O(n)— visits every node once via previousCommand links.
 - getCommandCount:      O(1) — returns a stored integer.
 Space complexity: O(n) — one node per command pushed onto the stack.

 Interview questions this code prepares you for:
 - Why is a stack the natural choice for an undo system?
 - What is the difference between push, pop, and peek?
 - How is a stack implemented with a linked list vs an array?
 - What does LIFO mean and where else does it appear in computer science?
 - How does the JVM call stack relate to the stack data structure?

 Most common mistake and how this code avoids it:
 - Mistake: Calling pop on an empty stack, causing a NullPointerException.
 - Avoided: isStackEmpty() is checked in undoLastCommand() before accessing stackTop,
   and the edge case prints a clear message instead of crashing.

 When to use a stack vs the common alternative:
 - Use a stack when reverse-order access matters: undo systems, expression parsers,
   depth-first search, and call-frame tracking.
 - Use a queue when first-in first-out order is needed: task scheduling, BFS, print spoolers.
*/
