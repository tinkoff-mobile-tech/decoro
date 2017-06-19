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

import android.annotation.SuppressLint;
import android.text.Editable;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

import static junit.framework.Assert.assertEquals;

/**
 * @author a.shishkin1
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MaskFormatWatcherTest {
    @Test
    public void getUnformattedString() {
        MaskFormatWatcher watcher =
                new MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER));

        assertEquals("+7", watcher.getMask().toUnformattedString());
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void cursorPosition() {
        MaskFormatWatcher watcher =
                new MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER));

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

    @Test
    public void replace() {
        MaskFormatWatcher watcher =
                new MaskFormatWatcher(MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER));

        TextView textView = new TextView(RuntimeEnvironment.application);
        watcher.installOn(textView);

        textView.setText("+79990000000");

        {
            Editable editable = (Editable) textView.getText();
            editable.replace(4, 18, "111");
            assertEquals("+7 (111) ", textView.getText().toString());
        }

        {
            Editable editable = (Editable) textView.getText();
            editable.replace(5, 6, "222");
            assertEquals("+7 (122) 21", textView.getText().toString());
        }

        textView.setText("+79991234567");

        {
            Editable editable = (Editable) textView.getText();
            editable.replace(6, 7, "");
            assertEquals("+7 (991) 234-56-7", textView.getText().toString());
        }

        textView.setText("+712345");

        {
            Editable editable = (Editable) textView.getText();
            editable.replace(4, 11, "123456");
            assertEquals("+7 (123) 456-", textView.getText().toString());
        }
    }

    @Test
    public void forbidCursorMoving() {
        MaskFormatWatcher watcher =
                new MaskFormatWatcher(new MaskImpl(new PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___-__-__"), true));

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