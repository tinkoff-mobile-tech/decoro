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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;

import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;

/**
 * @author Mikhail Artemev
 */
public class MaskDescriptor implements Serializable, Parcelable {

    @NonNull
    public static MaskDescriptor ofRawMask(@Nullable final String rawMask) {
        if (TextUtils.isEmpty(rawMask)) {
            return emptyMask();
        }

        return new MaskDescriptor()
                .setRawMask(rawMask);
    }

    @NonNull
    public static MaskDescriptor ofRawMask(@Nullable final String rawMask, final boolean terminated) {
        return new MaskDescriptor()
                .setRawMask(rawMask)
                .setTerminated(terminated);
    }

    @NonNull
    public static MaskDescriptor ofSlots(@Nullable Slot[] slots) {
        return new MaskDescriptor().setSlots(slots);
    }

    @NonNull
    public static MaskDescriptor emptyMask() {
        return new MaskDescriptor()
                .setSlots(new Slot[]{PredefinedSlots.any()})
                .setTerminated(false);
    }

    private Slot[] slots;

    private String rawMask;
    private String initialValue;

    private boolean terminated = true;
    private boolean forbidInputWhenFilled = false;
    private boolean hideHardcodedHead = false;

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
        this.hideHardcodedHead = copy.hideHardcodedHead;
        this.forbidInputWhenFilled = copy.forbidInputWhenFilled;
    }

    protected MaskDescriptor(Parcel in) {
        this.slots = in.createTypedArray(Slot.CREATOR);
        this.rawMask = in.readString();
        this.initialValue = in.readString();
        this.terminated = in.readByte() != 0;
        this.forbidInputWhenFilled = in.readByte() != 0;
        this.hideHardcodedHead = in.readByte() != 0;
    }

    public boolean isValid() {
        return slots != null || !TextUtils.isEmpty(rawMask);
    }

    public void validateOrThrow() {
        if (!isValid()) {
            throw new IllegalStateException("Mask descriptor is malformed. Should have at least slots array or raw mask (string representation)");
        }
    }

    /**
     * @deprecated use {link {@link #setSlots(Slot[])}} instead
     */
    @Deprecated
    public MaskDescriptor withSlots(@Nullable Slot[] value) {
        slots = value;
        return this;
    }

    /**
     * @deprecated use {link {@link #setRawMask(String)}} instead
     */
    public MaskDescriptor withRawMask(@Nullable String value) {
        rawMask = value;
        return this;
    }

    /**
     * @deprecated use {link {@link #setInitialValue(String)}} instead
     */
    public MaskDescriptor withInitialValue(@Nullable String value) {
        initialValue = value;
        return this;
    }

    /**
     * @deprecated use {link {@link #setTerminated(boolean)}} instead
     */
    public MaskDescriptor withTermination(boolean value) {
        terminated = value;
        return this;
    }

    /**
     * @deprecated Feature unavailable
     */
    public MaskDescriptor withShowingEmptySlots(boolean value) {
        return this;
    }

    /**
     * @deprecated use {link {@link #setEmptySlotPlaceholder(Character)}} instead
     */
    public MaskDescriptor withEmptySlotPalceholder(@Nullable Character value) {
        return this;
    }

    /**
     * @deprecated use {link {@link #setForbidInputWhenFilled(boolean)}} instead
     */
    public MaskDescriptor withForbiddingInputWhenFilled(boolean value) {
        forbidInputWhenFilled = value;
        return this;
    }

    /**
     * @deprecated use {link {@link #setHideHardcodedHead(boolean)}} instead
     */
    public MaskDescriptor withHideHardcodedHead(boolean value) {
        hideHardcodedHead = value;
        return this;
    }

    @Nullable
    public Slot[] getSlots() {
        return slots;
    }

    public MaskDescriptor setSlots(@Nullable Slot[] slots) {
        this.slots = slots;
        return this;
    }

    @Nullable
    public String getRawMask() {
        return rawMask;
    }

    public MaskDescriptor setRawMask(@Nullable String rawMask) {
        this.rawMask = rawMask;
        return this;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public MaskDescriptor setTerminated(boolean terminated) {
        this.terminated = terminated;
        return this;
    }

    @Nullable
    public String getInitialValue() {
        return initialValue;
    }

    public MaskDescriptor setInitialValue(@Nullable String initialValue) {
        this.initialValue = initialValue;
        return this;
    }

    /**
     * @deprecated Feature unavailable
     */
    @Deprecated
    public boolean isShowEmptySlots() {
        return false;
    }

    /**
     * @deprecated Feature unavailable
     */
    @Deprecated
    public MaskDescriptor setShowEmptySlots(boolean showEmptySlots) {
        return this;
    }

    /**
     * @deprecated Feature unavailable
     */
    @Deprecated
    @Nullable
    public Character getEmptySlotPlaceholder() {
        return '_';
    }

    /**
     * @deprecated Feature unavailable
     */
    @Deprecated
    public MaskDescriptor setEmptySlotPlaceholder(@Nullable Character emptySlotPlaceholder) {
        return this;
    }

    public boolean isForbidInputWhenFilled() {
        return forbidInputWhenFilled;
    }

    public MaskDescriptor setForbidInputWhenFilled(boolean forbidInputWhenFilled) {
        this.forbidInputWhenFilled = forbidInputWhenFilled;
        return this;
    }

    public boolean isHideHardcodedHead() {
        return hideHardcodedHead;
    }

    public MaskDescriptor setHideHardcodedHead(boolean hideHardcodedHead) {
        this.hideHardcodedHead = hideHardcodedHead;
        return this;
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
        dest.writeByte(this.forbidInputWhenFilled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hideHardcodedHead ? (byte) 1 : (byte) 0);
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
        if (forbidInputWhenFilled != that.forbidInputWhenFilled) return false;
        if (hideHardcodedHead != that.hideHardcodedHead) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(slots, that.slots)) return false;
        if (rawMask != null ? !rawMask.equals(that.rawMask) : that.rawMask != null) return false;
        return initialValue != null ? initialValue.equals(that.initialValue) : that.initialValue == null;

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(slots);
        result = 31 * result + (rawMask != null ? rawMask.hashCode() : 0);
        result = 31 * result + (initialValue != null ? initialValue.hashCode() : 0);
        result = 31 * result + (terminated ? 1 : 0);
        result = 31 * result + (forbidInputWhenFilled ? 1 : 0);
        result = 31 * result + (hideHardcodedHead ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        if (!TextUtils.isEmpty(rawMask)) {
            return rawMask;
        } else if (slots != null && slots.length > 0) {
            return slotsToString();
        }

        return "(empty)";
    }

    private String slotsToString() {
        final StringBuilder result = new StringBuilder(slots.length);
        for (Slot slot : slots) {
            Character value = slot.getValue();
            if (value == null) {
                value = Slot.PLACEHOLDER_DEFAULT;
            }

            result.append(value);
        }

        return result.toString();
    }
}
