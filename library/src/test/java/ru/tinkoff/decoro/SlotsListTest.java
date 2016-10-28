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

import java.util.Arrays;

import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Mikhail Artemev
 */
public class SlotsListTest {

    private SlotsList list;
    final Slot first = PredefinedSlots.any();
    final Slot second = PredefinedSlots.digit();
    final Slot last = PredefinedSlots.hardcodedSlot('2');

    Slot[] bareSlots;

    @Before
    public void setUp() throws Exception {
        bareSlots = new Slot[]{
                first,
                second,
                PredefinedSlots.hardcodedSlot('1'),
                PredefinedSlots.hardcodedSlot('1'),
                last
        };
        list = SlotsList.ofArray(bareSlots);
    }

    @Test(expected = NullPointerException.class)
    public void copyFails() throws Exception {
        new SlotsList((SlotsList) null);
    }

    @Test
    public void ofArray() throws Exception {
        assertEquals(5, list.size());
        assertEquals(list.getFirstSlot(), first);
        assertEquals(list.getLastSlot(), last);
        assertEquals(list.getFirstSlot().getNextSlot(), second);
        assertEquals(list.getLastSlot().getPrevSlot().getPrevSlot().getPrevSlot(), second);
    }

    @Test(expected = NullPointerException.class)
    public void toArray() throws Exception {
        assertTrue(Arrays.equals(list.toArray(), bareSlots));
        assertTrue(Arrays.equals(list.toArray(new Slot[0]), bareSlots));
        list.toArray(null);
    }

    @Test(expected = NullPointerException.class)
    public void add() throws Exception {
        list.add(PredefinedSlots.hardcodedSlot('9'));
        assertEquals(6, list.size());
        assertEquals(list.getLastSlot(), PredefinedSlots.hardcodedSlot('9'));
        list.add(null);
    }

    @Test
    public void checkIsIndex() throws Exception {
        assertTrue(list.checkIsIndex(0));
        assertTrue(list.checkIsIndex(4));

        assertFalse(list.checkIsIndex(5));
        assertFalse(list.checkIsIndex(-1));
        assertFalse(list.checkIsIndex(Integer.MAX_VALUE));
    }

    @Test
    public void getSlot() throws Exception {
        assertEquals(list.getSlot(0), first);
        assertEquals(list.getSlot(1), second);
        assertEquals(list.getSlot(4), last);

        assertNull(list.getSlot(5));
        assertNull(list.getSlot(-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertSlotAt_failsTooBig() throws Exception {
        list.insertSlotAt(10, PredefinedSlots.any());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertSlotAt_failsTooSmall() throws Exception {
        list.insertSlotAt(-1, PredefinedSlots.any());
    }

    @Test
    public void insertSlotAt() throws Exception {
        assertEquals(PredefinedSlots.hardcodedSlot('0'), list.insertSlotAt(0, PredefinedSlots.hardcodedSlot('0')));

        assertEquals(6, list.size());
        assertEquals(list.getFirstSlot(), PredefinedSlots.hardcodedSlot('0'));

        list.insertSlotAt(6, PredefinedSlots.hardcodedSlot('5'));
        assertEquals(7, list.size());
        assertEquals(list.getLastSlot(), PredefinedSlots.hardcodedSlot('5'));

        list.insertSlotAt(1, PredefinedSlots.hardcodedSlot('1'));
        assertEquals(8, list.size());
        assertEquals(list.getFirstSlot().getNextSlot(), PredefinedSlots.hardcodedSlot('1'));

        list.insertSlotAt(7, PredefinedSlots.hardcodedSlot('6'));
        assertEquals(9, list.size());
        assertEquals(list.getLastSlot().getPrevSlot(), PredefinedSlots.hardcodedSlot('6'));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeSlotAt_failsTooBig() throws Exception {
        list.removeSlotAt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeSlotAt_failsTooSmall() throws Exception {
        list.removeSlotAt(-1);
    }

    @Test
    public void removeSlotAt() throws Exception {
        assertEquals(list.getFirstSlot(), list.removeSlotAt(0));
        assertEquals(4, list.size());

        while (list.size() > 1) {
            assertEquals(list.getLastSlot(), list.removeSlotAt(list.size() - 1));
        }

        assertEquals(list.getLastSlot(), list.getFirstSlot());
    }

    @Test
    public void removeSlot() throws Exception {

    }

    @Test
    public void equals() throws Exception {
        assertNotEquals(list, null);
        assertNotEquals(list, new SlotsList());

        assertEquals(list, new SlotsList(list));

        SlotsList list2 = SlotsList.ofArray(new Slot[]{
                PredefinedSlots.any(),
                PredefinedSlots.digit(),
                PredefinedSlots.hardcodedSlot('1'),
                PredefinedSlots.hardcodedSlot('1'),
                PredefinedSlots.hardcodedSlot('2'),
        });

        assertEquals(list, list2);
        assertEquals(list2, list);
    }

    @Test
    public void clear() throws Exception {
        SlotsList list2 = SlotsList.ofArray(new Slot[]{
                PredefinedSlots.any(),
                PredefinedSlots.any(),
                PredefinedSlots.any(),
                PredefinedSlots.digit(),
        });

        for (Slot slot : list2) {
            slot.setValue('0');
            assertNotNull(slot.getValue());
        }

        list2.clear();
        for (Slot slot : list2) {
            assertNull(slot.getValue());
        }
    }

}