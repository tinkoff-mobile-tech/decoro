package ru.tinkoff.formatting;

import org.junit.Before;
import org.junit.Test;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Artemyev
 */
public class MaskTest {

    Mask mask;

    @Before
    public void init() {
        mask = new MaskImpl(new Slot[]{
                PredefinedSlots.any(),
                PredefinedSlots.hardcodedSlot('-'),
                PredefinedSlots.any(),
                PredefinedSlots.any()
        }, true);
    }

    @Test
    public void filled() {
        assertFalse(mask.filled());

        mask.insertFront("1");
        assertFalse(mask.filled());

        mask.insertFront("1");
        assertFalse(mask.filled());

        mask.insertFront("1");
        assertTrue(mask.filled());

        mask.removeBackwards(0, 1);
        assertFalse(mask.filled());
    }

    @Test
    public void insertAtFilledMask() {
        mask.insertFront("111");
        assertEquals("1-11", mask.toString());

        mask.insertFront("2");
        assertEquals("2-11", mask.toString());

        mask.setForbidInputWhenFilled(true);
        mask.insertFront("3");
        assertEquals("2-11", mask.toString());

        mask.insertAt(2, "4");
        assertEquals("2-11", mask.toString());

        mask.setForbidInputWhenFilled(false);
        mask.insertAt(2, "3");
        assertEquals("2-31", mask.toString());
    }

    @Test
    public void hasUserInput() {
        assertFalse(mask.hasUserInput());

        mask.insertFront("1");
        assertTrue(mask.hasUserInput());

        mask.removeBackwards(0, 1);
        assertFalse(mask.hasUserInput());

        Mask nullMask = new MaskImpl(new Slot[]{}, true);
        assertFalse(nullMask.hasUserInput());
    }

    @Test
    public void toStringWithoutParams() {
        mask = new MaskImpl(new Slot[]{
                PredefinedSlots.hardcodedSlot('+'),
                PredefinedSlots.any(),
                PredefinedSlots.hardcodedSlot('-'),
                PredefinedSlots.any(),
                PredefinedSlots.any()
        }, true);

        assertEquals("+", mask.toString());

        mask.setHideHardcodedHead(true);
        assertEquals("", mask.toString());

        mask.setShowingEmptySlots(true);
        assertEquals("+_-__", mask.toString());

        mask.insertFront("123");
        assertEquals("+1-23", mask.toString());
    }

    @Test
    public void toUnformattedString(){

        mask = new MaskImpl(new Slot[]{
                PredefinedSlots.hardcodedSlot('+'),
                PredefinedSlots.any(),
                PredefinedSlots.hardcodedSlot('-').withTags(Slot.TAG_DECORATION),
                PredefinedSlots.any(),
                PredefinedSlots.any()
        }, true);

        assertEquals("+", mask.toUnformattedString());

        mask.setHideHardcodedHead(true);
        assertEquals("", mask.toUnformattedString());

        mask.setShowingEmptySlots(true);
        assertEquals("+___", mask.toUnformattedString());

        mask.insertFront("123");
        assertEquals("+123", mask.toUnformattedString());

    }

}
