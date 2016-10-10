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

package ru.tinkoff.formatting.parser;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemev
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
