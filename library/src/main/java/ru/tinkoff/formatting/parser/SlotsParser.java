package ru.tinkoff.formatting.parser;

import android.support.annotation.NonNull;

import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemyev
 */
public interface SlotsParser {
    @NonNull
    Slot[] parseSlots(@NonNull CharSequence rawMask);
}
