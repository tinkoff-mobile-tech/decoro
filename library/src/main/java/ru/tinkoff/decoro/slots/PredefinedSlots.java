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

/**
 * @author Mikhail Artemev
 */
public final class PredefinedSlots {

    public static final Slot[] SINGLE_SLOT = new Slot[]{PredefinedSlots.any()};

    public static final Slot[] RUS_PHONE_NUMBER = {
            PredefinedSlots.hardcodedSlot('+'),
            PredefinedSlots.hardcodedSlot('7'),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.hardcodedSlot('(').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(')').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot('-').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot('-').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
    };

    public static final Slot[] RUS_PASSPORT = {
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' '),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
    };

    public static final Slot[] CARD_NUMBER_STANDARD = {
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
    };

    public static final Slot[] CARD_NUMBER_MAESTRO = {
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
    };

    public static final Slot[] CARD_NUMBER_STANDARD_MASKABLE = {
            PredefinedSlots.digit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
    };

    public static final Slot[] CARD_NUMBER_MAESTRO_MASKABLE = {
            PredefinedSlots.digit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
            maskableDigit(),
    };

    public static Slot hardcodedSlot(char value) {
        return new Slot(Slot.RULES_HARDCODED, value, null);
    }

    public static Slot digit() {
        return new Slot(null, new SlotValidators.DigitValidator());
    }

    public static Slot any() {
        return new Slot(null, new SlotValidators.GenerousValidator());
    }

    public static Slot maskableDigit() {
        return new Slot(null, new SlotValidators.MaskedDigitValidator());
    }

    private PredefinedSlots() {

    }

}
