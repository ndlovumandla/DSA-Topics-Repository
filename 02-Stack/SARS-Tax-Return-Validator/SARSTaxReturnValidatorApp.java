/*
 The South African Revenue Service processes approximately 6 million individual tax returns each year
 through the eFiling platform, and each return has deeply nested section tags — income blocks inside
 employment sections inside personal profiles, deductions inside expense categories inside schedules.
 A common submission error is a mismatched or missing closing tag, which causes downstream processing
 failures that are expensive to diagnose manually after the fact. Developer Priya builds a real-time
 bracket-matching validator: as the return is parsed left to right, every opening tag is pushed onto
 a stack, and every closing tag pops the stack and checks whether the pair matches. A stack is the
 only correct structure for this problem because nested sections must be closed in reverse opening
 order — the innermost section closes first — which is exactly the LIFO contract a stack enforces.
*/

/**
 * Implements SARS eFiling's section-tag validator using a custom stack built from scratch.
 * Opening tags are pushed; closing tags pop and check for correct nesting and pairing.
 */
public class SARSTaxReturnValidatorApp {

    /**
     * Represents a single opening tag sitting in the validation stack.
     * Each node holds the tag text, its position in the return, and a link toward the stack base.
     */
    static class OpeningTagNode {
        String tagName;        // e.g. "<income>", "<deduction>"
        int    charPosition;   // character index where this tag appeared in the return
        OpeningTagNode lowerTag; // link to the tag opened before this one (stack-down direction)

        /**
         * Creates an opening-tag node ready to be pushed onto the validation stack.
         *
         * @param tagName      the full opening tag text
         * @param charPosition character offset where the tag starts in the return string
         */
        OpeningTagNode(String tagName, int charPosition) {
            // Time: O(1) — constant field assignments.
            this.tagName      = tagName;
            this.charPosition = charPosition;
            this.lowerTag     = null;
        }
    }

    /**
     * Wraps the result of a validation run with a pass/fail flag and a human-readable message.
     * Used by printValidationResult() to give taxpayers and auditors actionable feedback.
     */
    static class ValidationResult {
        boolean isValid;
        String  message;

        /**
         * Creates a result record for one validation run.
         *
         * @param isValid true when the return structure is correctly nested
         * @param message explanation suitable for display to the taxpayer
         */
        ValidationResult(boolean isValid, String message) {
            // Time: O(1) — two field assignments.
            this.isValid = isValid;
            this.message = message;
        }
    }

    /**
     * Custom LIFO stack for opening tags, backing the SARS bracket-matching engine.
     * Each push records an opening tag; each pop retrieves the most recent unclosed tag.
     */
    static class TagValidationStack {
        private OpeningTagNode stackTop;  // LIFO head — most recently opened unclosed tag
        private int openTagCount;

        /**
         * Initialises a fresh validation stack before parsing a tax return.
         */
        TagValidationStack() {
            // Time: O(1).
            this.stackTop    = null;
            this.openTagCount = 0;
        }

        /**
         * Reports whether all opened tags have been matched and closed.
         *
         * @return true when no unmatched opening tags remain
         */
        boolean isStackEmpty() {
            // Time: O(1) — one pointer comparison.
            return stackTop == null;
        }

        /**
         * Pushes an opening tag onto the validation stack when the parser encounters one.
         * LIFO: this tag will be the first one checked when the next closing tag appears.
         *
         * @param tagName      opening tag text, e.g. "<employment>"
         * @param charPosition position in the return where the tag was found
         */
        void pushOpeningTag(String tagName, int charPosition) {
            // Time: O(1) — constant pointer rewire; no traversal.
            OpeningTagNode discoveredTag = new OpeningTagNode(tagName, charPosition);

            // New node points to previous top; then becomes new top.
            discoveredTag.lowerTag = stackTop;
            stackTop               = discoveredTag;
            openTagCount++;
        }

        /**
         * Pops the most recently opened unclosed tag so it can be matched against a closing tag.
         *
         * @return the topmost opening-tag node, or null if the stack is empty
         */
        OpeningTagNode popOpeningTag() {
            // Time: O(1) — constant pointer update.
            if (isStackEmpty()) {
                return null;
            }
            OpeningTagNode poppedTag = stackTop;
            stackTop                = stackTop.lowerTag; // advance top toward base
            openTagCount--;
            return poppedTag;
        }

        /**
         * Peeks at the innermost unclosed tag without removing it from the stack.
         *
         * @return current stack-top node, or null if empty
         */
        OpeningTagNode peekInnermostTag() {
            // Time: O(1) — direct top-pointer access.
            return stackTop;
        }

        /**
         * Returns how many opening tags have not yet been matched.
         *
         * @return count of unclosed opening tags still on the stack
         */
        int getUnclosedTagCount() {
            // Time: O(1) — returns stored counter.
            return openTagCount;
        }
    }

    /**
     * Validates one complete tax return string by scanning all tags left to right.
     * Uses a TagValidationStack to enforce correct nesting via LIFO pairing.
     */
    static class EFilingValidator {

        /**
         * Maps a closing tag to its expected paired opening tag.
         * Built without java.util.HashMap — uses a parallel arrays approach.
         */
        private static final String[] CLOSING_TAGS = {
            "</personalProfile>", "</employment>", "</income>",
            "</businessSchedule>", "</expenses>", "</deduction>"
        };
        private static final String[] MATCHING_OPENS = {
            "<personalProfile>", "<employment>", "<income>",
            "<businessSchedule>", "<expenses>", "<deduction>"
        };

        /**
         * Parses a return string token by token, pushing opens and popping/checking closes.
         * Demonstrates the full bracket-matching algorithm driven by a stack.
         *
         * @param taxReturnContent the full return text to validate
         * @param returnId         reference number shown in taxpayer feedback
         * @return a ValidationResult describing pass or fail with reason
         */
        ValidationResult validate(String taxReturnContent, String returnId) {
            // Time: O(n * t) — n tokens in the return, t = number of known tag types (constant),
            // so effectively O(n) for practical purposes.
            TagValidationStack tagStack = new TagValidationStack();

            // Split return content into tokens on whitespace for simple demonstration parsing.
            String[] tokens = taxReturnContent.split("\\s+");

            for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
                String currentToken = tokens[tokenIndex].trim();

                // Skip blank tokens produced by extra whitespace.
                if (currentToken.isEmpty()) continue;

                if (isOpeningTag(currentToken)) {
                    // Push: record this opening tag at its approximate token position.
                    tagStack.pushOpeningTag(currentToken, tokenIndex);
                    System.out.println("  [PUSH] Found opening tag " + currentToken
                            + " at token " + tokenIndex);

                } else if (isClosingTag(currentToken)) {
                    String expectedOpen = findExpectedOpen(currentToken);

                    // Edge case: closing tag appears when no opening tags are on the stack.
                    if (tagStack.isStackEmpty()) {
                        return new ValidationResult(false,
                                "Return " + returnId + " INVALID: closing tag " + currentToken
                                + " at token " + tokenIndex + " has no matching opening tag.");
                    }

                    // Pop the most recently opened unclosed tag (LIFO).
                    OpeningTagNode lastOpenTag = tagStack.popOpeningTag();
                    System.out.println("  [POP]  Closing tag " + currentToken
                            + " matched against " + lastOpenTag.tagName);

                    // Check: the popped tag must match the current closing tag.
                    if (!lastOpenTag.tagName.equals(expectedOpen)) {
                        return new ValidationResult(false,
                                "Return " + returnId + " INVALID: closing tag " + currentToken
                                + " at token " + tokenIndex
                                + " does not match open tag " + lastOpenTag.tagName
                                + " opened at token " + lastOpenTag.charPosition + ".");
                    }
                }
            }

            // After full scan, unclosed tags remaining on the stack mean missing closes.
            if (!tagStack.isStackEmpty()) {
                OpeningTagNode unclosed = tagStack.peekInnermostTag();
                return new ValidationResult(false,
                        "Return " + returnId + " INVALID: "
                        + tagStack.getUnclosedTagCount() + " unclosed tag(s). "
                        + "Innermost unclosed: " + unclosed.tagName
                        + " opened at token " + unclosed.charPosition + ".");
            }

            return new ValidationResult(true,
                    "Return " + returnId + " VALID: all section tags correctly nested and closed.");
        }

        /**
         * Checks whether a token is a known opening tag.
         *
         * @param token token text to classify
         * @return true if the token matches any known opening tag
         */
        private boolean isOpeningTag(String token) {
            // Time: O(t) — t is the fixed number of known tag types, so effectively O(1).
            for (String open : MATCHING_OPENS) {
                if (token.equals(open)) return true;
            }
            return false;
        }

        /**
         * Checks whether a token is a known closing tag.
         *
         * @param token token text to classify
         * @return true if the token matches any known closing tag
         */
        private boolean isClosingTag(String token) {
            // Time: O(t) — linear scan over the fixed tag-type array.
            for (String close : CLOSING_TAGS) {
                if (token.equals(close)) return true;
            }
            return false;
        }

        /**
         * Returns the expected opening tag that should pair with the given closing tag.
         *
         * @param closingTag the closing tag whose pair is needed
         * @return the matching opening tag string, or empty string if unmapped
         */
        private String findExpectedOpen(String closingTag) {
            // Time: O(t) — parallel array scan over fixed-size tag list.
            for (int i = 0; i < CLOSING_TAGS.length; i++) {
                if (CLOSING_TAGS[i].equals(closingTag)) {
                    return MATCHING_OPENS[i];
                }
            }
            return "";
        }
    }

    /**
     * Prints a formatted validation result to the console.
     *
     * @param result the ValidationResult produced by the validator
     */
    static void printValidationResult(ValidationResult result) {
        // Time: O(1) — single print statement.
        String status = result.isValid ? "PASS" : "FAIL";
        System.out.println("[" + status + "] " + result.message);
    }

    /**
     * Story-driven demo: Priya runs the eFiling validator against four tax returns covering
     * a valid return, a mismatched close, a missing close, and a close-before-open scenario.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Time: O(n) per validation call where n is the number of tokens in each return.
        EFilingValidator eFilingValidator = new EFilingValidator();

        System.out.println("=== SARS eFiling — Tax Return Structure Validator ===");

        // --- Valid return: all tags correctly opened and closed. ---
        System.out.println("\n-- Test 1: Valid return (ITR12-2024-001) --");
        String validReturn =
            "<personalProfile> "
            + "<employment> <income> </income> </employment> "
            + "<businessSchedule> <expenses> <deduction> </deduction> </expenses> </businessSchedule> "
            + "</personalProfile>";
        printValidationResult(eFilingValidator.validate(validReturn, "ITR12-2024-001"));

        // --- Mismatched closing tag: inner tag closed with wrong pair. ---
        System.out.println("\n-- Test 2: Mismatched closing tag (ITR12-2024-002) --");
        String mismatchedReturn =
            "<personalProfile> "
            + "<employment> <income> </employment> </income> "
            + "</personalProfile>";
        printValidationResult(eFilingValidator.validate(mismatchedReturn, "ITR12-2024-002"));

        // --- Missing closing tag: <businessSchedule> never closed. ---
        System.out.println("\n-- Test 3: Missing closing tag (ITR12-2024-003) --");
        String missingCloseReturn =
            "<personalProfile> "
            + "<businessSchedule> <expenses> </expenses> "
            + "</personalProfile>";
        printValidationResult(eFilingValidator.validate(missingCloseReturn, "ITR12-2024-003"));

        // --- Edge case: closing tag fires with nothing on the stack. ---
        System.out.println("\n-- Test 4: Close before open (ITR12-2024-004) --");
        String closeBeforeOpenReturn = "</income> <income> </income>";
        printValidationResult(eFilingValidator.validate(closeBeforeOpenReturn, "ITR12-2024-004"));

        // --- Edge case: completely empty return body. ---
        System.out.println("\n-- Test 5: Empty return body (ITR12-2024-005) --");
        printValidationResult(eFilingValidator.validate("", "ITR12-2024-005"));
    }
}

/*
 ═══ STUDY NOTES ═══
 Concepts used:
 - LIFO:             Last In, First Out — the most recently opened tag is always checked first when a close arrives.
 - push:             Store an opening tag on the stack when the parser encounters it.
 - pop:              Retrieve the most recently pushed opening tag to compare with a closing tag.
 - peek:             Inspect the innermost unclosed tag without removing it, for error reporting.
 - call stack:       Just like the JVM call stack, nesting here must unwind in reverse open order.
 - bracket matching: The core algorithm — push on open, pop and compare on close, empty at end means valid.
 - undo mechanism:   Conceptually identical to undo: each close "undoes" (cancels) its matching open.

 Big-O for every operation:
 - pushOpeningTag:      O(1) — constant pointer update.
 - popOpeningTag:       O(1) — constant pointer update.
 - peekInnermostTag:    O(1) — direct top read.
 - isStackEmpty:        O(1) — one pointer check.
 - getUnclosedTagCount: O(1) — returns stored counter.
 - isOpeningTag:        O(t) — t = fixed number of tag types, practically O(1).
 - isClosingTag:        O(t) — same as above.
 - findExpectedOpen:    O(t) — parallel array scan over fixed tag set.
 - validate (full):     O(n) — each token is processed once; stack ops are O(1) each.
 Space complexity: O(d) — d = maximum nesting depth; worst case O(n) for a fully open return.

 Interview questions this code prepares you for:
 - Explain the bracket-matching algorithm using a stack.
 - Why is a stack the correct structure for validating nested tags?
 - What happens if a closing tag is encountered when the stack is empty?
 - What is the space complexity of the bracket-matching algorithm?
 - How would you report the exact position of a mismatch to the user?

 Most common mistake and how this code avoids it:
 - Mistake: Forgetting to check whether the stack is empty before calling pop,
   and not verifying that the stack is empty after all tokens are consumed.
 - Avoided: Both conditions are checked explicitly — empty-stack close is caught mid-parse,
   and remaining unclosed tags are detected after the final token.

 When to use a stack vs the common alternative:
 - Use a stack for any problem that requires reverse-order processing: bracket matching,
   depth-first search, expression evaluation, or call-frame unwinding.
 - Use a queue when you need to process items in arrival order (BFS, print spoolers, task scheduling).
*/
