package ru.tinkoff.formatting.slots;

/**
 * @author Mikhail Artemyev
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

    public static final Slot[] CARD_NUMBER_STANDART = {
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

    public static final Slot[] CARD_NUMBER_STANDART_MASKABLE = {
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
        return new Slot(null,
                new SlotValidators.DigitValidator(),
                new SlotValidators.MaskedDigitValidator());
    }

    private PredefinedSlots() {

    }

}
