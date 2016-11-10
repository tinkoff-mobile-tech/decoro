package ru.tinkoff.decoro.watchers;

import android.support.annotation.NonNull;

import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskImpl;

/**
 * @author Mikhail Artemev
 */

public class MaskFormatWatcher extends FormatWatcher {

    private MaskImpl maskOriginal;

    public MaskFormatWatcher(MaskImpl maskOriginal) {
        setMask(maskOriginal);
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
        refreshMask();
    }

    public void swapMask(MaskImpl newMask) {
        maskOriginal = new MaskImpl(newMask);
        maskOriginal.clear();

        refreshMask(newMask.toString());
    }
}
