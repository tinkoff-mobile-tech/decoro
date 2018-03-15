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

import android.os.Parcel;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.slots.SlotValidatorSet;
import ru.tinkoff.decoro.slots.SlotValidators;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Artemev
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SlotTest {

    @Test
    public void parcelable() {
        Slot before = new Slot(Slot.RULES_DEFAULT,
                'x',
                SlotValidatorSet.setOf(new SlotValidators.MaskedDigitValidator()));

        Parcel parcel = Parcel.obtain();
        before.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Slot after = Slot.CREATOR.createFromParcel(parcel);

        assertEquals(before, after);

        parcel.recycle();
    }

    @Test
    public void arrayEquals() {
        Slot[] s1 = new Slot[]{PredefinedSlots.digit()};
        Slot[] s2 = new Slot[]{PredefinedSlots.digit()};

        assertEquals(s1[0], s2[0]);
        assertTrue(Arrays.equals(s1, s2));
    }

    @Test
    public void setValueOffset(){
        Slot b = new Slot(Slot.RULES_DEFAULT, null, null);
        assertEquals(1, b.setValue(' '));
        assertEquals(1, b.setValue('2'));

        Slot c = new Slot(Slot.RULES_DEFAULT, null, null);
        c.setPrevSlot(b);
        b.setNextSlot(c);

        assertEquals(1, c.setValue('3'));

        Slot a = new Slot(Slot.RULE_INPUT_MOVES_INPUT, 'a', null);
        b.setPrevSlot(a);
        a.setNextSlot(b);
        assertEquals(1, a.setValue('a'));
        assertEquals(2, a.setValue('3'));

        Slot a0 = new Slot(Slot.RULE_INPUT_MOVES_INPUT, 'a', null);
        a.setPrevSlot(a);
        a0.setNextSlot(a);
        assertEquals(1, a0.setValue('a'));
        assertEquals(3, a0.setValue(' '));
    }

    @Test
    public void invalidValueNotCopiedFromNext() {
        Slot first = new Slot('0', new SlotValidators.DigitValidator());
        Slot second = new Slot('a', new SlotValidators.LetterValidator());
        first.setNextSlot(second);
        second.setPrevSlot(first);

        first.setValue(null);

        Assert.assertNull(first.getValue());
    }

    @Test
    public void invalidValueNotRemovedFromNext() {
        Slot first = new Slot('0', new SlotValidators.DigitValidator());
        Slot second = new Slot('a', new SlotValidators.LetterValidator());
        first.setNextSlot(second);
        second.setPrevSlot(first);

        first.setValue(null);

        Assert.assertEquals(new Character('a'), second.getValue());
    }

    @Test
    public void validValueCopiedFromNext() {
        Slot first = new Slot('1', new SlotValidators.DigitValidator());
        Slot second = new Slot('2', new SlotValidators.DigitValidator());
        first.setNextSlot(second);
        second.setPrevSlot(first);

        first.setValue(null);

        Assert.assertEquals(new Character('2'), first.getValue());
    }

    @Test
    public void validValueRemovedFromNext() {
        Slot first = new Slot('1', new SlotValidators.DigitValidator());
        Slot second = new Slot('2', new SlotValidators.DigitValidator());
        first.setNextSlot(second);
        second.setPrevSlot(first);

        first.setValue(null);

        Assert.assertNull(second.getValue());
    }

}
