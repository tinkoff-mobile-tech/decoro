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

package ru.tinkoff.decoro;

import android.support.annotation.NonNull;

import ru.tinkoff.decoro.parser.SlotsParser;
import ru.tinkoff.decoro.slots.Slot;

/**
 * @author Mikhail Artemev
 */
public class MaskFactoryImpl implements MaskFactory {

    private final SlotsParser slotsParser;
    private final MaskDescriptor maskDescriptor;

    public MaskFactoryImpl(SlotsParser slotsParser, MaskDescriptor maskDescriptor) {
        this.slotsParser = slotsParser;
        this.maskDescriptor = maskDescriptor;
    }

    @NonNull
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
        mask.setHideHardcodedHead(maskDescriptor.isHideHardcodedHead());

        return mask;
    }
}
