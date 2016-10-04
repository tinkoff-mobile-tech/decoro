package ru.tinkoff.formatting.slots;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mikhail Artemyev
 */
public class SlotValidatorSet extends HashSet<Slot.SlotValidator> implements Slot.SlotValidator {

    public static SlotValidatorSet setOf(Slot.SlotValidator... validators) {
        if (validators == null) {
            return new SlotValidatorSet();
        }

        SlotValidatorSet result = new SlotValidatorSet(validators.length);
        for (Slot.SlotValidator one : validators) {
            if (one instanceof SlotValidatorSet) {
                result.addAll((SlotValidatorSet) one);
            } else {
                result.add(one);
            }
        }

        return result;
    }

    public SlotValidatorSet() {
        super();
    }

    public SlotValidatorSet(int capacity) {
        super(capacity);
    }

    public int countValidIn(Collection<Character> chars) {
        if (chars == null) {
            throw new IllegalArgumentException("String to validate cannot be null");
        }

        int result = 0;
        for (Character c : chars) {
            if (c == null) {
                throw new NullPointerException("We don't support collections with null elements");
            }

            if (validate(c)) {
                result++;
            }
        }

        return result;
    }

    @Override
    public boolean validate(char value) {
        for (Slot.SlotValidator validator : this) {
            if (validator.validate(value)) {
                return true;
            }
        }

        return false;
    }
}
