package ru.tinkoff.formatting.slots;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mikhail Artemyev
 */
public class Slot implements Serializable, Parcelable {

    /**
     * On input slot moves it's current value to the nextSlot
     * This is default behavior
     */
    public static final int RULE_INPUT_MOVES_CURRENT = 1;

    /**
     * On input slot moves new value to the next slot keeping current value
     */
    public static final int RULE_INPUT_MOVES_INPUT = 1 << 1;

    /**
     * This flag makes sense only in disjunction with RULE_INPUT_MOVES_INPUT.
     * It makes "hardcoded" slot to overwrite it's value with the same character only when
     * value not come from the neighbour slot.
     */
    public static final int RULE_FORBID_LEFT_OVERWRITE = 1 << 2;

    public static final int RULES_DEFAULT = RULE_INPUT_MOVES_CURRENT;
    public static final int RULES_HARDCODED = RULE_INPUT_MOVES_INPUT;

    /**
     * Tag that marks a slot as a "decoration" slot. This kind of slots are only needed for
     * visual representation of a text. "Unformatted" text should not contain decoration slots.
     * The number itself is <i>magical</i> and does make no sense.
     */
    public static final int TAG_DECORATION = 14779;

    public static final char PLACEHOLDER_DEFAULT = '_';

    private int rulesFlags = RULES_DEFAULT;

    private Character value;

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

    public boolean anyInputToTheLeft() {
        if (value != null && !hardcoded()) {
            return true;
        }

        if (prevSlot != null) {
            return prevSlot.anyInputToTheLeft();
        }

        return false;
    }

    public int setValue(@Nullable Character newValue) {
        return setValue(newValue, false);
    }

    public int setValue(@Nullable Character newValue, boolean fromLeft) {
        return setValueInner(0, newValue, fromLeft);
    }

    private int setValueInner(int offset, @Nullable Character newValue, boolean fromLeft) {
        if (newValue == null) {
            removeCurrentValue();
            return 0;
        }

        return setNewValue(offset, newValue, fromLeft);
    }

    @Nullable
    public Character getValue() {
        return value;
    }

    public boolean canInsertHere(char newValue) {
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

        final boolean forbiddenInputFromLeft = fromLeft && checkRule(RULE_FORBID_LEFT_OVERWRITE);
        if (hardcoded() && !forbiddenInputFromLeft && value.equals(newValue)) {
            return ++offset;
        }

        int newOffset = 0;

        if (checkRule(RULE_INPUT_MOVES_INPUT) || forbiddenInputFromLeft) {
            // we should push new value further without replacing the current one
            newOffset = pushValueToSlot(offset + 1, newValue, nextSlot);
            changeCurrent = false;
        }

        if (value != null && checkRule(RULE_INPUT_MOVES_CURRENT)) {
            // we should push current value further without
            pushValueToSlot(0, value, nextSlot);
        }

        if (changeCurrent) {
            value = newValue;
            newOffset = offset + 1;
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

        dest.writeInt(tags.size());
        for (Integer theTag : tags) {
            dest.writeInt(theTag);
        }
    }

    protected Slot(Parcel in) {
        this.rulesFlags = in.readInt();
        this.value = (Character) in.readSerializable();
        this.validators = (SlotValidatorSet) in.readSerializable();

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
}
