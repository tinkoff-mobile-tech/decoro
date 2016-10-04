package ru.tinkoff.formatting.parser;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemyev
 */
public class UnderscoreDigitSlotsParser implements SlotsParser {

    public static final char SLOT_STUB = '_';

    @NonNull
    @Override
    public Slot[] parseSlots(@NonNull CharSequence rawMask) {
        if (TextUtils.isEmpty(rawMask)) {
            throw new IllegalArgumentException("String representation of the mask's slots is empty");
        }

        final Slot[] result = new Slot[rawMask.length()];
        for (int index = 0; index < rawMask.length(); index++) {
            final char currentChar = rawMask.charAt(index);
            result[index] = slotFromChar(currentChar);
        }

        return result;
    }

    protected Slot slotFromChar(final char character) {
        if (character == SLOT_STUB) {
            return PredefinedSlots.digit();
        }

        return slotFromNonUnderscoredChar(character);
    }

    protected Slot slotFromNonUnderscoredChar(char character) {
        return PredefinedSlots.hardcodedSlot(character);
    }
}
