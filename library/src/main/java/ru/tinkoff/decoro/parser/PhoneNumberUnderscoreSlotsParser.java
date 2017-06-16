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

package ru.tinkoff.decoro.parser;

import android.support.annotation.NonNull;

import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.slots.SlotValidatorSet;
import ru.tinkoff.decoro.slots.SlotValidators;

/**
 * @author Mikhail Artemev
 */
public class PhoneNumberUnderscoreSlotsParser extends UnderscoreDigitSlotsParser {

    private static final char PLUS_SIGN = '+';

    private int rule;

    @NonNull
    @Override
    public Slot[] parseSlots(@NonNull CharSequence rawMask) {
        rule = Slot.RULE_INPUT_MOVES_INPUT | Slot.RULE_INPUT_REPLACE;
        return super.parseSlots(rawMask);
    }

    @Override
    protected Slot slotFromNonUnderscoredChar(char character) {
        if (!Character.isDigit(character)) {
            rule = Slot.RULE_INPUT_MOVES_INPUT | Slot.RULE_INPUT_REPLACE;
            final Slot hardcoded = PredefinedSlots.hardcodedSlot(character);
            return character == PLUS_SIGN ? hardcoded : hardcoded.withTags(Slot.TAG_DECORATION);
        }

        final Slot slot = new Slot(rule, character, SlotValidatorSet.setOf(new SlotValidators.DigitValidator()));
        rule = Slot.RULE_INPUT_MOVES_INPUT;

        return slot;
    }
}
