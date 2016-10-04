package ru.tinkoff.formatting.parser;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;
import ru.tinkoff.formatting.slots.SlotValidatorSet;
import ru.tinkoff.formatting.slots.SlotValidators;

/**
 * @author Mikhail Artemyev
 */
public class PhoneNumberUnderscoreSlotsParser extends UnderscoreDigitSlotsParser {

    private int rule;

    @Override
    public Slot[] parseSlots(CharSequence rawMask) {
        rule = Slot.RULE_INPUT_MOVES_INPUT;
        return super.parseSlots(rawMask);
    }

    @Override
    protected Slot slotFromNonUnderscoredChar(char character) {
        if (!Character.isDigit(character)) {
            return PredefinedSlots.hardcodedSlot(character);
        }

        final Slot slot = new Slot(rule, character, SlotValidatorSet.setOf(new SlotValidators.DigitValidator()));
        rule |= Slot.RULE_FORBID_LEFT_OVERWRITE;

        return slot;
    }

}
