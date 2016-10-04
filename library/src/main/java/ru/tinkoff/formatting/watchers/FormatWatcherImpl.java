package ru.tinkoff.formatting.watchers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.tinkoff.formatting.Mask;
import ru.tinkoff.formatting.MaskDescriptor;
import ru.tinkoff.formatting.MaskFactoryImpl;
import ru.tinkoff.formatting.parser.SlotsParser;

/**
 * @author Mikhail Artemyev
 */
public class FormatWatcherImpl extends FormatWatcher {

    private SlotsParser slotsParser;
    private MaskDescriptor maskDescriptor;

    public FormatWatcherImpl(@Nullable SlotsParser slotsParser) {
        this.slotsParser = slotsParser;
    }

    public FormatWatcherImpl(@Nullable SlotsParser slotsParser, @Nullable MaskDescriptor maskDescriptor) {
        this.slotsParser = slotsParser;
        this.maskDescriptor = maskDescriptor;
        if (maskDescriptor != null) {
            changeMask(maskDescriptor);
        }
    }

    public FormatWatcherImpl() {
        this(null, MaskDescriptor.emptyMask());
    }

    public FormatWatcherImpl(@Nullable MaskDescriptor maskDescriptor) {
        this(null, maskDescriptor);
    }

    public void changeMask(@NonNull final MaskDescriptor maskDescriptor) {
        this.maskDescriptor = maskDescriptor;
        refreshMask(maskDescriptor.getInitialValue());
    }

    @NonNull
    @Override
    public Mask createMask() {
        return new MaskFactoryImpl(slotsParser, maskDescriptor).createMask();
    }

    public void setSlotsParser(@Nullable SlotsParser slotsParser) {
        this.slotsParser = slotsParser;
    }

    @Nullable
    public SlotsParser getSlotsParser() {
        return slotsParser;
    }
}
