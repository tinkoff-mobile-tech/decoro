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

package ru.tinkoff.formatting;

import android.annotation.SuppressLint;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.watchers.FormatWatcherImpl;

import static junit.framework.Assert.assertEquals;

/**
 * @author Mikhail Artemev
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FormatWatcherImplTest {

    @Test
    public void getUnformattedString() {
        FormatWatcherImpl watcher =
                new FormatWatcherImpl(MaskDescriptor.ofSlots(PredefinedSlots.RUS_PHONE_NUMBER));

        assertEquals("+7", watcher.getMask().toUnformattedString());
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void cursorPosition() {
        FormatWatcherImpl watcher =
                new FormatWatcherImpl(MaskDescriptor.ofSlots(PredefinedSlots.RUS_PHONE_NUMBER));

        assertEquals(0, watcher.getCursorPosition());

        TextView textView = new TextView(RuntimeEnvironment.application);
        watcher.installOn(textView);

        textView.setText("9"); // +7_(9
        assertEquals(5, watcher.getCursorPosition());

        textView.setText(null);
        assertEquals(0, watcher.getCursorPosition());

        textView.setText("999"); // +7_(999)_
        assertEquals(9, watcher.getCursorPosition());

        textView.setText("999876"); // +7_(999)_876-
        assertEquals(13, watcher.getCursorPosition());

        textView.setText("9998765432111111111"); // +7_(999)_876-54-32
        assertEquals(18, watcher.getCursorPosition());

        textView.setText("7"); // +7_(
        assertEquals(4, watcher.getCursorPosition());
    }

}
