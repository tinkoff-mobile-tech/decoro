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

import org.junit.Before;
import org.junit.Test;

import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Artemev
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
    public void toUnformattedString() {

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
