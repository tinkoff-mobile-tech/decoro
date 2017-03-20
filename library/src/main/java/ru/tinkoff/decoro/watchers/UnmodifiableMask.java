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

package ru.tinkoff.decoro.watchers;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Iterator;

import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.slots.Slot;

/**
 * @author Mikhail Artemev
 */
class UnmodifiableMask implements Mask {

    private final Mask delegate;

    public UnmodifiableMask(@NonNull Mask delegate) {
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        return delegate == null ? "" : delegate.toString();
    }

    @NonNull
    @Override
    public String toUnformattedString() {
        return delegate == null ? "" : delegate.toUnformattedString();
    }

    @Override
    public int getInitialInputPosition() {
        return delegate == null ? -1 : delegate.getInitialInputPosition();
    }

    @Override
    public boolean hasUserInput() {
        return delegate != null && delegate.hasUserInput();
    }

    @Override
    public boolean filled() {
        return delegate != null && delegate.filled();
    }

    @Override
    public void clear() {
        if (delegate != null) {
            clear();
        }
    }

    @Override
    public int insertAt(int position, CharSequence input, boolean cursorAfterTrailingHardcoded) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int insertAt(int position, CharSequence input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int insertFront(CharSequence input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeBackwards(int position, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeBackwardsWithoutHardcoded(int position, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSize() {
        return delegate == null ? 0 : delegate.getSize();
    }

    @Override
    public boolean isShowingEmptySlots() {
        return delegate != null && delegate.isShowingEmptySlots();
    }

    @Override
    public void setShowingEmptySlots(boolean showingEmptySlots) {
        if (delegate != null) {
            delegate.setShowingEmptySlots(showingEmptySlots);
        }
    }

    @NonNull
    @Override
    public Character getPlaceholder() {
        return delegate == null ? null : delegate.getPlaceholder();
    }

    @Override
    public void setPlaceholder(@NonNull Character placeholder) {
        if (delegate != null) {
            delegate.setPlaceholder(placeholder);
        }
    }

    @Override
    public boolean isHideHardcodedHead() {
        return delegate != null && delegate.isHideHardcodedHead();
    }

    @Override
    public void setHideHardcodedHead(boolean shouldHideHardcodedHead) {
        if (delegate != null) {
            delegate.setHideHardcodedHead(shouldHideHardcodedHead);
        }
    }

    @Override
    public boolean isForbidInputWhenFilled() {
        return delegate != null && delegate.isForbidInputWhenFilled();
    }

    @Override
    public void setForbidInputWhenFilled(boolean forbidInputWhenFilled) {
        if (delegate != null) {
            delegate.setForbidInputWhenFilled(forbidInputWhenFilled);
        }
    }

    @Override
    public int findCursorPositionInUnformattedString(int cursorPosition) {
        return delegate == null ? cursorPosition : delegate.findCursorPositionInUnformattedString(cursorPosition);
    }

    @Override
    public Iterator<Slot> iterator() {
        return delegate == null ? null : delegate.iterator();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.delegate, flags);
    }

    protected UnmodifiableMask(Parcel in) {
        this.delegate = in.readParcelable(Mask.class.getClassLoader());
    }

    public static final Creator<UnmodifiableMask> CREATOR = new Creator<UnmodifiableMask>() {
        @Override
        public UnmodifiableMask createFromParcel(Parcel source) {
            return new UnmodifiableMask(source);
        }

        @Override
        public UnmodifiableMask[] newArray(int size) {
            return new UnmodifiableMask[size];
        }
    };
}
