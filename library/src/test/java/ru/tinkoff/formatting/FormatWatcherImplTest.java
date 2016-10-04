package ru.tinkoff.formatting;

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
 * @author Mikhail Artemyev
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
