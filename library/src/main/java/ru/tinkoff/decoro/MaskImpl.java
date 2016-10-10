/*
 * Copyright © 2016 Tinkoff Bank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.tinkoff.decoro;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import ru.tinkoff.decoro.slots.Slot;

/**
 * @author Mikhail Artemev
 */
public class MaskImpl implements Mask {

    public static Mask createTerminated(@NonNull final Slot[] slots) {
        return new MaskImpl(slots, true);
    }

    public static Mask createNonTerminated(@NonNull final Slot[] slots) {
        return new MaskImpl(slots, false);
    }

    private static final int TAG_EXTENSION = -149635;

    // Members available outside the Mask
    private int size = 0;
    private boolean terminated = true;
    private Character placeholder;
    private boolean showingEmptySlots = false;
    private boolean forbidInputWhenFilled = false;
    private boolean hideHardcodedHead = false;

    // Inner use only
    private Slot firstSlot;
    private Slot lastSlot;
    private boolean showHardcodedTail = true;

    public MaskImpl(@NonNull Slot[] slots, boolean terminated) {
        this.terminated = terminated;
        this.size = slots.length;

        if (this.size == 0) {
            return;
        }

        this.firstSlot = new Slot(slots[0]);
        Slot prev = this.firstSlot;

        if (this.size == 1) {
            lastSlot = this.firstSlot;
            if (!terminated) {
                extendTail(1);
            }
        }

        // link slots
        for (int i = 1; i < slots.length; i++) {
            Slot next = new Slot(slots[i]);
            prev.setNextSlot(next);
            next.setPrevSlot(prev);

            prev = next;

            if (i == slots.length - 1) {
                this.lastSlot = next;
            }
        }

    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Returns text without decoration characters. Method works with respect to all the
     * mask parameters. Just as {@link #toString()} does;
     * <p>
     * <br/><br/> <em>Example</em>: assume that filled mask contains "+7 999
     * 111-22-33" where spaces and dashes are decoration slots. Then this
     * method will return "+79991112233".
     *
     * @return content of the mask without characters in a decoration slots.
     */
    @NonNull
    @Override
    public String toUnformattedString() {
        return toString(false);
    }

    @NonNull
    private String toString(boolean allowDecoration) {
        Slot initialSlot = firstSlot;
        if (!showingEmptySlots && hideHardcodedHead && !initialSlot.anyInputToTheRight()) {
            while (initialSlot != null && initialSlot.hardcoded()) {
                initialSlot = initialSlot.getNextSlot();
            }
        }

        return initialSlot != null ? toStringFrom(initialSlot, allowDecoration) : "";
    }

    @Override
    public Iterator<Slot> iterator() {
        return new MaskIterator(firstSlot);
    }

    private String toStringFrom(final Slot startSlot, final boolean allowDecoration) {
        final StringBuilder result = new StringBuilder();

        // create a string out of slots values
        Slot slot = startSlot;
        int index = 0;
        while (slot != null) {
            Character c = slot.getValue();

            if (allowDecoration || !slot.hasTag(Slot.TAG_DECORATION)) {
                boolean anyInputFromHere = slot.anyInputToTheRight();

                if (!anyInputFromHere && !showingEmptySlots) {
                    // user input nothing to the right from this point
                    if (!showHardcodedTail || !checkIsIndex(slot.hardcodedSequenceEndIndex() - 1 + index)) {
                        break;
                    }
                }

                // if we've met slot with no value we got two options:
                // 1) Stop further output. This option should apply when we have no further input value
                //                         (except hardcoded)
                // 2) Continue with showing placeholder. This option is for a case when there's some
                //                                       after current slot or Mask#showingEmptySlots is true
                if (c == null && (showingEmptySlots || anyInputFromHere)) {
                    c = getPlaceholder();
                } else if (c == null) {
                    break;
                }

                result.append(c);
            }

            slot = slot.getNextSlot();
            index++;
        }

        return result.toString();
    }

    @Override
    public int getInitialInputPosition() {
        int cursorPosition = 0;

        Slot slot = getSlot(cursorPosition);
        while (slot != null && slot.getValue() != null) {
            cursorPosition++;
            slot = slot.getNextSlot();
        }

        return cursorPosition;
    }

    @Override
    public boolean hasUserInput() {
        if (firstSlot == null) {
            return false;
        }

        return firstSlot.anyInputToTheRight();
    }

    @Override
    public boolean filled() {
        return filledFrom(firstSlot);
    }


    private boolean filledFrom(final Slot initialSlot) {
        if (initialSlot == null) {
            throw new IllegalArgumentException("first slot is null");
        }

        Slot nextSlot = initialSlot;
        do {
            if (!nextSlot.hasTag(TAG_EXTENSION)) {
                if (!nextSlot.hardcoded() && nextSlot.getValue() == null) {
                    return false;
                }
            }

            nextSlot = nextSlot.getNextSlot();
        } while (nextSlot != null);

        return true;
    }

    @Override
    public int insertAt(final int position, @Nullable final CharSequence input, boolean cursorAfterTrailingHardcoded) {
        showHardcodedTail = true;
        if (!checkIsIndex(position) || input == null || input.length() == 0) {
            return position;
        }

        int cursorPosition = position;
        Slot slotCandidate = getSlot(position);

        if (forbidInputWhenFilled && filledFrom(slotCandidate)) {
            return position;
        }

        Deque<Character> inStack = dequeFrom(input);

        while (!inStack.isEmpty()) {

            char newValue = inStack.pop();

            // find index offset to the next slot we can input current character to
            final SlotIndexOffset slotForInputIndex = validSlotIndexOffset(slotCandidate, newValue);

            // if there were any non-hardcoded slots skipped while looking for next slot offset
            // and we don't allow 'spots' in the input - we should stop inserting right now
            if (!showingEmptySlots && slotForInputIndex.nonHarcodedSlotSkipped) {
                break;
            }

            cursorPosition += slotForInputIndex.indexOffset;
            final Slot slotForInput = getSlot(cursorPosition);
            if (slotForInput != null && slotForInput == lastSlot && !terminated) {
                // extend mask to fit all VALID input characters (if mask non-terminated)
                extendTail(slotForInput.getValidators().countValidIn(inStack) + 1);
            }

            if (slotForInput != null) {
                slotCandidate = slotForInput;
                final int insertOffset = slotCandidate.setValue(newValue, slotForInputIndex.indexOffset > 0);

                slotCandidate = slotCandidate.getNextSlot();
                cursorPosition += insertOffset;
            }

        }

        if (cursorAfterTrailingHardcoded) {
            int hardcodedTailLength = 0;
            if (slotCandidate != null) {
                hardcodedTailLength = slotCandidate.hardcodedSequenceEndIndex();
            }

            if (hardcodedTailLength > 0) {
                cursorPosition += hardcodedTailLength;
            }
        }

        // allow hardcoded tail be visible only if we've inserted at the end of the input
        final Slot nextSlot = getSlot(cursorPosition);
        showHardcodedTail = nextSlot == null || !nextSlot.anyInputToTheRight();

        return cursorPosition;
    }

    @Override
    public int insertAt(final int position, @Nullable final CharSequence input) {
        return insertAt(position, input, true);
    }

    @Override
    public int insertFront(final @Nullable CharSequence input) {
        return insertAt(0, input, true);
    }


    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isShowingEmptySlots() {
        return showingEmptySlots;
    }

    @Override
    public void setShowingEmptySlots(boolean showingEmptySlots) {
        this.showingEmptySlots = showingEmptySlots;
    }

    /**
     * Removes available symbols from the buffer. This method should be called on deletion event of
     * user's input. Symbols are deleting backwards (just as backspace key). Hardcoded symbols
     * would not be deleted, only cursor will be moved over them.
     * <p>
     * Method also updates {@code showHardcodedTail} flag that defines whether tail of hardcoded
     * symbols (at the end of user's input) should be shown. In most cases it should not. The only
     * case when they are visible - buffer starts with them and deletion was inside them.
     *
     * @param position from where to start deletion
     * @param count    number of  symbols to delete.
     * @return new cursor position after deletion
     */
    @Override
    public int removeBackwards(int position, int count) {
        // go back fom position and remove any non-hardcoded characters
        for (int i = 0; i < count; i++) {
            if (checkIsIndex(position)) {
                final Slot s = getSlot(position);
                if (s != null && !s.hardcoded()) {
                    s.setValue(null);
                }
            }

            position--;
        }

        trimTail();

        int cursorPosition = position;

        Slot slot = getSlot(cursorPosition);
        // all the following code will only occur if we're not trying to remove "hardcoded head"
        if (slot != null && (slot.anyInputToTheLeft() || !slot.anyInputToTheRight())) {
            // We could remove a symbol before a sequence of hardcoded characters
            // that are now tail. It this case our cursor index will point at non printable
            // character. To avoid this find next not-hardcoded symbol to the left
            while (slot != null && slot.hardcoded() && cursorPosition > 0) {
                slot = getSlot(--cursorPosition);
                if (slot != null) {
                    showHardcodedTail = !slot.anyInputToTheRight();
                }
            }

            // check if we've reached begin of the string
            // this can happen not only because we've been 'deleting' hardcoded characters
            // at he begin of the string.
            showHardcodedTail &= cursorPosition <= 0 && !hideHardcodedHead;
            if (showHardcodedTail) {
                cursorPosition = position;
            }
        }

        cursorPosition++;

        return (0 <= cursorPosition && cursorPosition <= size) ? cursorPosition : 0;
    }

    @NonNull
    @Override
    public Character getPlaceholder() {
        return placeholder != null ? placeholder : Slot.PLACEHOLDER_DEFAULT;
    }

    @Override
    public void setPlaceholder(Character placeholder) {
        if (placeholder == null) {
            throw new IllegalArgumentException("Placeholder is null");
        }

        this.placeholder = placeholder;
    }

    @Override
    public boolean isHideHardcodedHead() {
        return hideHardcodedHead;
    }

    @Override
    public void setHideHardcodedHead(boolean shouldHideHardcodedHead) {
        this.hideHardcodedHead = shouldHideHardcodedHead;
    }

    @Override
    public boolean isForbidInputWhenFilled() {
        return forbidInputWhenFilled;
    }

    @Override
    public void setForbidInputWhenFilled(boolean forbidInputWhenFilled) {
        this.forbidInputWhenFilled = forbidInputWhenFilled;
    }

    private void removeSlotAt(int position) {
        removeSlot(getSlot(position));
    }

    private void removeSlot(Slot slotToRemove) {
        if (slotToRemove == null) {
            return;
        }

        Slot left = slotToRemove.getPrevSlot();
        Slot right = slotToRemove.getNextSlot();

        if (left != null) {
            left.setNextSlot(right);
        } else {
            firstSlot = right;
        }

        if (right != null) {
            right.setPrevSlot(left);
        } else {
            lastSlot = left;
        }

        size--;

    }

    private Slot getSlot(int index) {
        if (!checkIsIndex(index)) {
            return null;
        }

        Slot result;

        if (index < (size >> 1)) {
            // first half of a list
            result = firstSlot;
            for (int i = 0; i < index; i++) {
                result = result.getNextSlot();
            }
        } else {
            // second half of a list
            result = lastSlot;
            for (int i = size - 1; i > index; i--) {
                result = result.getPrevSlot();
            }
        }

        if (result == null) {
            throw new IllegalStateException("Slot inside the mask should not be null. But it is.");
        }

        return result;
    }

    /**
     * Looks for a slot to insert {@code value}. Search moves to the right from the specified one
     * (including it). While searching it checks whether the're any non-hardcoded slots that cannot
     * accept pending input if such slots are found it is marked in a resulting object.
     *
     * @param slot  slot from where to start
     * @param value value to be inserted to slot
     * @return wrapper around index offset to the found slot and flag showing did search skip any
     * non-hardcoded slots
     */
    private SlotIndexOffset validSlotIndexOffset(Slot slot, final char value) {
        final SlotIndexOffset result = new SlotIndexOffset();

        while (slot != null && !slot.canInsertHere(value)) {
            if (!result.nonHarcodedSlotSkipped && !slot.hardcoded()) {
                result.nonHarcodedSlotSkipped = true;
            }
            slot = slot.getNextSlot();
            result.indexOffset++;
        }

        return result;
    }

    private boolean checkIsIndex(int position) {
        return 0 <= position && position < size;
    }

    /**
     * Inserts slots at the and of the mask and mark newly inserted slot as 'extension' (extended
     * tail of non-terminated mask). 'Extended' slots will be removed when their values are cleared
     */
    private void extendTail(int count) {
        if (terminated || count < 1) {
            return;
        }

        while (--count >= 0) {
            // create a copy of the last slot and make it the last one
            final Slot inserted = insertSlotAt(size, lastSlot);
            inserted.withTags(TAG_EXTENSION);
        }
    }

    /**
     * Inserts a slot on a specified position
     *
     * @param position index where new slot weill be placed should be >= 0 and <= size.
     * @param slot     slot ot insert. IMPORTANT: a copy of this slot will be inserted!
     * @return newly inserted slot (copy of the passed one)
     */
    private Slot insertSlotAt(final int position, @NonNull final Slot slot) {

        if (position < 0 || size < position) {
            throw new IndexOutOfBoundsException("New slot position should be inside the mask. Or on the tail (position = size)");
        }

        final Slot toInsert = new Slot(slot);

        Slot currentSlot = getSlot(position);
        Slot leftNeighbour;
        Slot rightNeighbour = null;
        if (currentSlot == null) {
            // this can happen only when position == size.
            // it means we want to add the slot on the tail
            leftNeighbour = lastSlot;
        } else {
            leftNeighbour = currentSlot.getPrevSlot();
            rightNeighbour = currentSlot;
        }

        toInsert.setNextSlot(rightNeighbour);
        toInsert.setPrevSlot(leftNeighbour);

        if (rightNeighbour != null) {
            // right neighbour is only available for non-last slots
            rightNeighbour.setPrevSlot(toInsert);
        }

        if (leftNeighbour != null) {
            // left neighbour is only available for not-first slots
            leftNeighbour.setNextSlot(toInsert);
        }

        if (position == 0) {
            firstSlot = toInsert;
        } else if (position == size) {
            lastSlot = toInsert;
        }

        size++;

        return toInsert;
    }

    private void trimTail() {
        if (terminated || lastSlot == null) {
            return;
        }

        Slot currentSlot = lastSlot;
        Slot prevSlot = currentSlot.getPrevSlot();
        while (isAllowedToRemoveSlot(currentSlot, prevSlot)) {
            removeSlotAt(size - 1);
            currentSlot = prevSlot;
            prevSlot = prevSlot.getPrevSlot();
        }
    }

    private boolean isAllowedToRemoveSlot(Slot removalCandidate, Slot previousSlot) {
        return removalCandidate.hasTag(TAG_EXTENSION) &&
                previousSlot.hasTag(TAG_EXTENSION) &&
                removalCandidate.getValue() == null &&
                previousSlot.getValue() == null;
    }

    /**
     * Creates deque (double-side queue) of CharSequence
     *
     * @param in char sequence to be converted to deque
     * @return characters deque
     */
    private Deque<Character> dequeFrom(CharSequence in) {
        if (in == null) {
            return null;
        }

        final Deque<Character> out = new ArrayDeque<>(in.length());

        for (int i = in.length() - 1; i >= 0; i--) {
            out.push(in.charAt(i));
        }

        return out;
    }

    private static class MaskIterator implements Iterator<Slot> {

        Slot nextSlot;

        public MaskIterator(Slot currentSlot) {
            if (currentSlot == null) {
                throw new IllegalArgumentException("Initial slot for iterator cannot be null");
            }

            this.nextSlot = currentSlot;
        }

        @Override
        public boolean hasNext() {
            return nextSlot != null;
        }

        @Override
        public Slot next() {
            Slot current = nextSlot;
            nextSlot = nextSlot.getNextSlot();
            return current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Mask cannot be modified from outside!");
        }
    }

    private static class SlotIndexOffset {

        // offset from current slot to any other slot
        int indexOffset = 0;

        // flag showing is there any input-available slots that cannot accept desired character
        boolean nonHarcodedSlotSkipped;
    }
}
