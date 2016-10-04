package ru.tinkoff.formatting;

import android.os.Parcel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemyev
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MaskDescriptorTest {

    @Test
    public void parcelable() {
        MaskDescriptor before = new MaskDescriptor()
                .withEmptySlotPalceholder('+')
                .withForbiddingInputWhenFilled(true)
                .withHideHardcodedHead(true)
                .withRawMask("___-___")
                .withShowingEmptySlots(true)
                .withSlots(new Slot[]{PredefinedSlots.digit()})
                .withTermination(false);

        Parcel parcel = Parcel.obtain();
        before.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        MaskDescriptor after = MaskDescriptor.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(before, after);

        parcel.recycle();
    }

}
