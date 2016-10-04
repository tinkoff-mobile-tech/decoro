package ru.tinkoff.formatting;

import ru.tinkoff.formatting.parser.SlotsParser;
import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemyev
 */
public class MaskFactoryImpl implements MaskFactory {

    private final SlotsParser slotsParser;
    private final MaskDescriptor maskDescriptor;

    public MaskFactoryImpl(SlotsParser slotsParser, MaskDescriptor maskDescriptor) {
        this.slotsParser = slotsParser;
        this.maskDescriptor = maskDescriptor;
    }

    @Override
    public Mask createMask() {
        if (maskDescriptor == null) {
            throw new IllegalArgumentException("MaskDescriptor cannot be null");
        }

        maskDescriptor.validateOrThrow();

        if (maskDescriptor.getSlots() == null && slotsParser == null) {
            throw new IllegalStateException("Cannot create mask: neither slots nor slots parser and raw-mask are set");
        }

        final Slot[] slots = maskDescriptor.getSlots() != null ?
                maskDescriptor.getSlots() :
                slotsParser.parseSlots(maskDescriptor.getRawMask());

        final Mask mask = new MaskImpl(slots, maskDescriptor.isTerminated());
        mask.setForbidInputWhenFilled(maskDescriptor.isForbidInputWhenFilled());
        mask.setShowingEmptySlots(maskDescriptor.isShowEmptySlots());
        mask.setHideHardcodedHead(maskDescriptor.isHideHardcodedHead());
        if (maskDescriptor.getEmptySlotPlaceholder() != null) {
            mask.setPlaceholder(mask.getPlaceholder());
        }

        return mask;
    }
}
