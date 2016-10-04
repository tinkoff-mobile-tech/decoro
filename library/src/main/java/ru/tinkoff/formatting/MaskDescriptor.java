package ru.tinkoff.formatting;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;

import ru.tinkoff.formatting.slots.PredefinedSlots;
import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemyev
 */
public class MaskDescriptor implements Serializable, Parcelable {

    @NonNull
    public static MaskDescriptor ofRawMask(@Nullable final String rawMask) {
        return new MaskDescriptor()
                .withRawMask(rawMask);
    }

    @NonNull
    public static MaskDescriptor ofRawMask(@Nullable final String rawMask, final boolean terminated) {
        return new MaskDescriptor()
                .withRawMask(rawMask)
                .withTermination(terminated);
    }

    @NonNull
    public static MaskDescriptor ofSlots(@Nullable Slot[] slots) {
        return new MaskDescriptor().withSlots(slots);
    }

    @NonNull
    public static MaskDescriptor emptyMask() {
        return new MaskDescriptor()
                .withSlots(new Slot[]{PredefinedSlots.any()})
                .withTermination(false);
    }

    private Slot[] slots;

    private String rawMask;
    private String initialValue;

    private boolean terminated = true;
    private boolean showEmptySlots = false;
    private boolean forbidInputWhenFilled = false;
    private boolean hideHardcodedHead = false;

    private Character emptySlotPlaceholder;

    public MaskDescriptor() {
    }

    public MaskDescriptor(@NonNull MaskDescriptor copy) {
        if (copy.slots != null) {
            this.slots = new Slot[copy.slots.length];
            System.arraycopy(copy.slots, 0, this.slots, 0, copy.slots.length);
        }

        this.rawMask = copy.rawMask;
        this.initialValue = copy.initialValue;

        this.terminated = copy.terminated;
        this.showEmptySlots = copy.showEmptySlots;
        this.hideHardcodedHead = copy.hideHardcodedHead;
        this.forbidInputWhenFilled = copy.forbidInputWhenFilled;

        this.emptySlotPlaceholder = copy.emptySlotPlaceholder;
    }

    protected MaskDescriptor(Parcel in) {
        this.slots = in.createTypedArray(Slot.CREATOR);
        this.rawMask = in.readString();
        this.initialValue = in.readString();
        this.terminated = in.readByte() != 0;
        this.showEmptySlots = in.readByte() != 0;
        this.forbidInputWhenFilled = in.readByte() != 0;
        this.hideHardcodedHead = in.readByte() != 0;
        this.emptySlotPlaceholder = (Character) in.readSerializable();
    }

    public boolean isValid() {
        return slots != null || !TextUtils.isEmpty(rawMask);
    }

    public void validateOrThrow() {
        if (!isValid()) {
            throw new IllegalStateException("Mask descriptor is malformed. Should have at least slots array or raw mask (string representation)");
        }
    }

    public MaskDescriptor withSlots(@Nullable Slot[] value) {
        slots = value;
        return this;
    }

    public MaskDescriptor withRawMask(@Nullable String value) {
        rawMask = value;
        return this;
    }

    public MaskDescriptor withInitialValue(@Nullable String value) {
        initialValue = value;
        return this;
    }

    public MaskDescriptor withTermination(boolean value) {
        terminated = value;
        return this;
    }

    public MaskDescriptor withShowingEmptySlots(boolean value) {
        showEmptySlots = value;
        return this;
    }

    public MaskDescriptor withEmptySlotPalceholder(@Nullable Character value) {
        emptySlotPlaceholder = value;
        return this;
    }

    public MaskDescriptor withForbiddingInputWhenFilled(boolean value) {
        forbidInputWhenFilled = value;
        return this;
    }

    public MaskDescriptor withHideHardcodedHead(boolean value) {
        hideHardcodedHead = value;
        return this;
    }

    @Nullable
    public Slot[] getSlots() {
        return slots;
    }

    public void setSlots(@Nullable Slot[] slots) {
        this.slots = slots;
    }

    @Nullable
    public String getRawMask() {
        return rawMask;
    }

    public void setRawMask(@Nullable String rawMask) {
        this.rawMask = rawMask;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    @Nullable
    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(@Nullable String initialValue) {
        this.initialValue = initialValue;
    }

    public boolean isShowEmptySlots() {
        return showEmptySlots;
    }

    public void setShowEmptySlots(boolean showEmptySlots) {
        this.showEmptySlots = showEmptySlots;
    }

    @Nullable
    public Character getEmptySlotPlaceholder() {
        return emptySlotPlaceholder;
    }

    public void setEmptySlotPlaceholder(@Nullable Character emptySlotPlaceholder) {
        this.emptySlotPlaceholder = emptySlotPlaceholder;
    }

    public boolean isForbidInputWhenFilled() {
        return forbidInputWhenFilled;
    }

    public void setForbidInputWhenFilled(boolean forbidInputWhenFilled) {
        this.forbidInputWhenFilled = forbidInputWhenFilled;
    }

    public boolean isHideHardcodedHead() {
        return hideHardcodedHead;
    }

    public void setHideHardcodedHead(boolean hideHardcodedHead) {
        this.hideHardcodedHead = hideHardcodedHead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.slots, flags);
        dest.writeString(this.rawMask);
        dest.writeString(this.initialValue);
        dest.writeByte(this.terminated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showEmptySlots ? (byte) 1 : (byte) 0);
        dest.writeByte(this.forbidInputWhenFilled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hideHardcodedHead ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.emptySlotPlaceholder);
    }

    public static final Creator<MaskDescriptor> CREATOR = new Creator<MaskDescriptor>() {
        @Override
        public MaskDescriptor createFromParcel(Parcel source) {
            return new MaskDescriptor(source);
        }

        @Override
        public MaskDescriptor[] newArray(int size) {
            return new MaskDescriptor[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaskDescriptor that = (MaskDescriptor) o;

        if (terminated != that.terminated) return false;
        if (showEmptySlots != that.showEmptySlots) return false;
        if (forbidInputWhenFilled != that.forbidInputWhenFilled) return false;
        if (hideHardcodedHead != that.hideHardcodedHead) return false;
        if (!Arrays.equals(slots, that.slots)) return false;
        if (rawMask != null ? !rawMask.equals(that.rawMask) : that.rawMask != null) return false;
        if (initialValue != null ? !initialValue.equals(that.initialValue) : that.initialValue != null)
            return false;
        return emptySlotPlaceholder != null ? emptySlotPlaceholder.equals(that.emptySlotPlaceholder) : that.emptySlotPlaceholder == null;

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(slots);
        result = 31 * result + (rawMask != null ? rawMask.hashCode() : 0);
        result = 31 * result + (initialValue != null ? initialValue.hashCode() : 0);
        result = 31 * result + (terminated ? 1 : 0);
        result = 31 * result + (showEmptySlots ? 1 : 0);
        result = 31 * result + (forbidInputWhenFilled ? 1 : 0);
        result = 31 * result + (hideHardcodedHead ? 1 : 0);
        result = 31 * result + (emptySlotPlaceholder != null ? emptySlotPlaceholder.hashCode() : 0);
        return result;
    }
}
