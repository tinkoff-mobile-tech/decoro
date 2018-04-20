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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.slots.SlotValidatorSet;
import ru.tinkoff.decoro.slots.SlotValidators;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Artemev
 */
@RunWith(JUnit4.class)
public class MaskImplTest {

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

    @Test
    public void replace(){
        Mask mask = new MaskImpl(
                new Slot[]{
                        new Slot(Slot.RULE_INPUT_REPLACE, '1', SlotValidatorSet.setOf(new SlotValidators.GenerousValidator())),
                        new Slot(Slot.RULE_INPUT_REPLACE, '2', SlotValidatorSet.setOf(new SlotValidators.GenerousValidator())),
                        new Slot(Slot.RULE_INPUT_REPLACE, '3', SlotValidatorSet.setOf(new SlotValidators.GenerousValidator())),
                        new Slot(Slot.RULE_INPUT_REPLACE, null, SlotValidatorSet.setOf(new SlotValidators.GenerousValidator()))
                }, true
        );

        assertEquals("123", mask.toString());

        assertEquals(1,mask.insertFront("0"));
        assertEquals(2,mask.insertAt(1, "1"));

        assertEquals("013", mask.toString());
    }

    @Test
    public void cursorPosition() {
        assertEquals(0, mask.insertFront("")); // (empty)
        assertEquals(2, mask.insertFront("1")); // 1-|
        assertEquals(1, mask.removeBackwards(1, 1)); // 1|-
        assertEquals(0, mask.removeBackwards(0, 1)); // (empty)

        assertEquals(4, mask.insertFront("12345656")); // 1-23|

        mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setHideHardcodedHead(true);
        assertEquals(4, mask.removeBackwards(4, 1));
        assertEquals(6, mask.insertFront("99"));
        assertEquals(4, mask.removeBackwards(4, 1));
        assertEquals(3, mask.removeBackwards(3, 1));
    }

    @Test
    public void phoneCursorPosition() {
        Mask phoneMask = new MaskImpl(new PhoneNumberUnderscoreSlotsParser().parseSlots("+359 ___ __ __"), true);
        phoneMask.setHideHardcodedHead(false);

        assertEquals(5, phoneMask.insertFront("3")); // +359 |
        assertEquals(4, phoneMask.removeBackwards(4, 1)); // +359|
        assertEquals(6, phoneMask.insertAt(3, "3")); // +359 3|

        assertEquals(0, phoneMask.removeBackwards(6, 8)); // |+359
        assertEquals(5, phoneMask.insertAt(0, "3")); // +359 |

        assertEquals(6, phoneMask.insertAt(1, "9")); // +359 9|
        assertEquals(6, phoneMask.insertAt(1, "5")); // +359 5|9
        assertEquals(5, phoneMask.insertAt(1, "3")); // +359 |59
        assertEquals(6, phoneMask.insertAt(2, "3")); // +359 3|59

        assertEquals(6, phoneMask.insertFront("5")); // +359 5|35 9
        assertEquals(10, phoneMask.insertAt(8, "4")); // +359 535 4|9

        assertEquals("+359 535 49", phoneMask.toString());

        assertEquals(1, phoneMask.removeBackwards(11, 11)); // +|359
        assertEquals("+359 ", phoneMask.toString());
    }

    @Test
    public void clear() {
        mask.insertFront("123");
        assertEquals("1-23", mask.toString());
        mask.clear();
        assertEquals("", mask.toString());
        mask.clear();
        assertEquals("", mask.toString());
    }

    @Test
    public void findCursorPositionInUnformattedString_correct() throws Exception {
        final Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        assertEquals(1, mask.findCursorPositionInUnformattedString(1));
        assertEquals(1, mask.findCursorPositionInUnformattedString(2));
        assertEquals(1, mask.findCursorPositionInUnformattedString(3));
        assertEquals(2, mask.findCursorPositionInUnformattedString(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void findCursorPositionInUnformattedString_lower() throws Exception {
        final Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.findCursorPositionInUnformattedString(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void findCursorPositionInUnformattedString_upper() throws Exception {
        final Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        assertEquals(mask.getSize(), mask.findCursorPositionInUnformattedString(100500));
    }

}
