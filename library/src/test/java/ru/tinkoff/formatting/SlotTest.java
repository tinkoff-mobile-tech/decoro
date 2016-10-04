package ru.tinkoff.formatting;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;
import ru.tinkoff.formatting.slots.SlotValidatorSet;
import ru.tinkoff.formatting.slots.SlotValidators;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Artemyev
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SlotTest {

    @Test
    public void parcelable() {
        Slot before = new Slot(Slot.RULE_INPUT_MOVES_CURRENT,
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
        Slot b = new Slot(Slot.RULE_INPUT_MOVES_CURRENT, null, null);
        assertEquals(1, b.setValue(' '));
        assertEquals(1, b.setValue('2'));

        Slot c = new Slot(Slot.RULE_INPUT_MOVES_CURRENT, null, null);
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

}
