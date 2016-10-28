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

package ru.tinkoff.decoro.watchers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskDescriptor;
import ru.tinkoff.decoro.MaskFactoryImpl;
import ru.tinkoff.decoro.parser.SlotsParser;

/**
 * @author Mikhail Artemev
 */
public class DescriptorFormatWatcher extends FormatWatcher {

    private SlotsParser slotsParser;
    private MaskDescriptor maskDescriptor;

    public DescriptorFormatWatcher(@Nullable SlotsParser slotsParser) {
        this.slotsParser = slotsParser;
    }

    public DescriptorFormatWatcher(@Nullable SlotsParser slotsParser, @Nullable MaskDescriptor maskDescriptor) {
        this.slotsParser = slotsParser;
        this.maskDescriptor = maskDescriptor;
        if (maskDescriptor != null) {
            changeMask(maskDescriptor);
        }
    }

    public DescriptorFormatWatcher() {
        this(null, MaskDescriptor.emptyMask());
    }

    public DescriptorFormatWatcher(@Nullable MaskDescriptor maskDescriptor) {
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
