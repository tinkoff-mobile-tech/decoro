package ru.tinkoff.formatting;

import android.support.annotation.NonNull;

/**
 * @author Mikhail Artemyev
 */
public interface MaskFactory {
    @NonNull
    Mask createMask();
}
