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

package ru.tinkoff.decoro.slots;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mikhail Artemev
 */
public final class Slot implements Serializable, Parcelable {

    /**
     * On input current value of a slot will be replaced.
     * Other slots won't be affected.
     * This is the same as text input on a keyboard with INSERT mode on.
     */
    public static final int RULE_INPUT_REPLACE = 1;

    /**
     * On input slot moves new value to the next slot keeping current value.
     * This rule makes slot 'non-modifiable' (hardcoded)
     */
    public static final int RULE_INPUT_MOVES_INPUT = 1 << 1;


    public static final int MASK_INPUT_RULES = 3;
    /**
     * On input slot moves it's current value to the nextSlot
     */
    public static final int RULES_DEFAULT = 0;

    public static final int RULES_HARDCODED = RULE_INPUT_MOVES_INPUT | RULE_INPUT_REPLACE;

    public static final int RULE_FORBID_CURSOR_MOVE_LEFT = 1 << 2;
    public static final int RULE_FORBID_CURSOR_MOVE_RIGHT = 1 << 3;

    /**
     * Tag that marks a slot as a "decoration" slot. This kind of slots are only needed for
     * visual representation of a text. "Unformatted" text should not contain decoration slots.
     * The number itself is <i>magical</i> and does make no sense.
     */
    public static final int TAG_DECORATION = 14779;

    public static final char PLACEHOLDER_DEFAULT = '_';

    private int rulesFlags = RULES_DEFAULT;

    private Character value;

    private ValueInterpreter valueInterpreter;

    private final Set<Integer> tags = new HashSet<>();

    private SlotValidatorSet validators;

    transient private Slot nextSlot;
    transient private Slot prevSlot;

    public Slot(int rules, @Nullable Character value, @Nullable SlotValidatorSet validators) {
        this.rulesFlags = rules;
        this.value = value;
        this.validators = validators == null ? new SlotValidatorSet() : validators;
    }

    public Slot(@Nullable Character value, @Nullable SlotValidator... validators) {
        this(RULES_DEFAULT, value, SlotValidatorSet.setOf(validators));
    }

    public Slot(char value) {
        this(RULES_DEFAULT, value, null);
    }

    public Slot() {
        this(RULES_DEFAULT, null, null);
    }

    public Slot(@NonNull Slot slotToCopy) {
        this(
                slotToCopy.rulesFlags,
                slotToCopy.value,
                slotToCopy.getValidators()
        );
        this.valueInterpreter = slotToCopy.valueInterpreter;
        this.tags.addAll(slotToCopy.tags);
    }

    public boolean anyInputToTheRight() {
        if (value != null && !hardcoded()) {
            return true;
        }

        if (nextSlot != null) {
            return nextSlot.anyInputToTheRight();
        }

        return false;
    }

    public int setValue(@Nullable Character newValue) {
        return setValue(newValue, false);
    }

    public int setValue(@Nullable Character newValue, boolean fromLeft) {
        return setValueInner(0, newValue, fromLeft);
    }

    public void setFlags(int rulesFlags) {
        this.rulesFlags = rulesFlags;
    }

    public int getFlags() {
        return rulesFlags;
    }

    public void setValueInterpreter(ValueInterpreter valueInterpreter) {
        this.valueInterpreter = valueInterpreter;
    }

    public Slot withValueInterpreter(ValueInterpreter valueInterpreter) {
        this.valueInterpreter = valueInterpreter;
        return this;
    }

    private int setValueInner(int offset, @Nullable Character newValue, boolean fromLeft) {
        newValue = valueInterpreter == null ? newValue : valueInterpreter.interpret(newValue);
        if (newValue == null) {
            removeCurrentValue();
            return checkRule(RULE_FORBID_CURSOR_MOVE_LEFT) ? 1 : 0;
        }
        return setNewValue(offset, newValue, fromLeft);
    }

    @Nullable
    public Character getValue() {
        return value;
    }

    public boolean canInsertHere(char newValue) {
        newValue = valueInterpreter == null ? newValue : valueInterpreter.interpret(newValue);

        if (hardcoded()) {
            return value.equals(newValue);
        }

        return validate(newValue);
    }

    private boolean validate(char val) {
        return validators == null || validators.validate(val);
    }

    public boolean hardcoded() {
        return value != null && checkRule(RULE_INPUT_MOVES_INPUT);
    }

    public int hardcodedSequenceEndIndex() {
        return hardcodedSequenceEndIndex(0);
    }

    public int hardcodedSequenceEndIndex(int fromIndex) {

        if (hardcoded() && (nextSlot == null || !nextSlot.hardcoded())) {
            // I'm last hardcoded slot
            return fromIndex + 1;
        }

        if (hardcoded() && nextSlot.hardcoded()) {
            // me and my next neighbour are hardcoded
            return nextSlot.hardcodedSequenceEndIndex(++fromIndex);
        }

        // i'm not even hardcoded
        return -1;
    }

    private int setNewValue(int offset, @NonNull Character newValue, boolean fromLeft) {
        boolean changeCurrent = true;

        final boolean forbiddenInputFromLeft = fromLeft
                && checkRule(RULE_INPUT_MOVES_INPUT)
                && !checkRule(RULE_INPUT_REPLACE);

        if (hardcoded() && !forbiddenInputFromLeft && value.equals(newValue)) {
            return checkRule(RULE_FORBID_CURSOR_MOVE_RIGHT) ? offset : offset + 1;
        }

        int newOffset = 0;

        if (checkRule(RULE_INPUT_MOVES_INPUT) || forbiddenInputFromLeft) {
            // we should push new value further without replacing the current one
            newOffset = pushValueToSlot(offset + 1, newValue, nextSlot);
            changeCurrent = false;
        }

        if (value != null && ((rulesFlags & MASK_INPUT_RULES) == RULES_DEFAULT)) {
            // we should push current value further without
            pushValueToSlot(0, value, nextSlot);
        }

        if (changeCurrent) {
            value = newValue;
            newOffset = checkRule(RULE_FORBID_CURSOR_MOVE_RIGHT) ? offset : offset + 1;
        }

        return newOffset;
    }

    private boolean checkRule(final int rule) {
        return (rulesFlags & rule) == rule;
    }

    private void removeCurrentValue() {
        if (!hardcoded()) {
            value = pullValueFromSlot(nextSlot);
        } else if (prevSlot != null) {
            prevSlot.removeCurrentValue();
        }
    }

    private int pushValueToSlot(int offset, Character newValue, Slot slot) {
        if (slot == null) {
            return 0;
        }

        return nextSlot.setValueInner(offset, newValue, true);
    }

    private Character pullValueFromSlot(Slot slot) {
        if (slot == null) {
            return null;
        }

        Character result = null;

        if (!slot.hardcoded()) {
            result = slot.getValue();
            slot.removeCurrentValue();
        } else if (slot.getNextSlot() != null) {
            result = pullValueFromSlot(slot.getNextSlot());
        }

        return result;
    }

    public Slot getNextSlot() {
        return nextSlot;
    }

    public void setNextSlot(Slot nextSlot) {
        this.nextSlot = nextSlot;
    }

    public Slot getPrevSlot() {
        return prevSlot;
    }

    public void setPrevSlot(Slot prevSlot) {
        this.prevSlot = prevSlot;
    }

    public SlotValidatorSet getValidators() {
        return validators;
    }

    public void setValidators(SlotValidatorSet validators) {
        this.validators = validators;
    }

    public Set<Integer> getTags() {
        return tags;
    }

    public Slot withTags(Integer... tags) {
        if (tags == null) {
            return this;
        }

        for (Integer tag : tags) {
            if (tag != null) {
                this.tags.add(tag);
            }
        }
        return this;
    }


    public boolean hasTag(Integer tag) {
        if (tag == null) {
            return false;
        }

        return tags.contains(tag);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "value=" + value +
                '}';
    }

    public interface SlotValidator extends Serializable {
        boolean validate(final char value);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.rulesFlags);
        dest.writeSerializable(this.value);
        dest.writeSerializable(this.validators);
        dest.writeSerializable(this.valueInterpreter);
        dest.writeInt(tags.size());
        for (Integer theTag : tags) {
            dest.writeInt(theTag);
        }
    }

    protected Slot(Parcel in) {
        this.rulesFlags = in.readInt();
        this.value = (Character) in.readSerializable();
        this.validators = (SlotValidatorSet) in.readSerializable();
        this.valueInterpreter = (ValueInterpreter) in.readSerializable();
        final int tagsCount = in.readInt();
        for (int i = 0; i < tagsCount; i++) {
            tags.add(in.readInt());
        }
    }

    public static final Creator<Slot> CREATOR = new Creator<Slot>() {
        @Override
        public Slot createFromParcel(Parcel source) {
            return new Slot(source);
        }

        @Override
        public Slot[] newArray(int size) {
            return new Slot[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;

        if (rulesFlags != slot.rulesFlags) return false;
        if (value != null ? !value.equals(slot.value) : slot.value != null) return false;
        if (tags != null ? !tags.equals(slot.tags) : slot.tags != null) return false;
        return validators != null ? validators.equals(slot.validators) : slot.validators == null;

    }

    @Override
    public int hashCode() {
        int result = rulesFlags;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (validators != null ? validators.hashCode() : 0);
        return result;
    }

    public static Slot[] copySlotArray(Slot[] arr) {
        Slot[] result = new Slot[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = new Slot(arr[i]);
        }
        return result;
    }
}
