package ru.tinkoff.decoro.watchers;

import android.support.annotation.NonNull;

import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskImpl;

/**
 * @author Mikhail Artemyev
 */

public class MaskFormatWatcher extends FormatWatcher {

    private MaskImpl maskOriginal;

    public MaskFormatWatcher(MaskImpl maskOriginal) {
        this.maskOriginal = maskOriginal;
    }

    @NonNull
    @Override
    public Mask createMask() {
        return new MaskImpl(maskOriginal);
    }

    public Mask getMaskOriginal() {
        return maskOriginal;
    }

    public void setMask(MaskImpl maskOriginal) {
        this.maskOriginal = maskOriginal;

        if (isInstalled()) {
            refreshMask();
        }
    }
}
