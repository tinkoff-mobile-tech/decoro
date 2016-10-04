package ru.tinkoff.formatting.watchers;

import android.support.annotation.NonNull;

import java.util.Iterator;

import ru.tinkoff.formatting.Mask;
import ru.tinkoff.formatting.slots.Slot;

/**
 * @author Mikhail Artemyev
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

    @Override
    public Character getPlaceholder() {
        return delegate == null ? null : delegate.getPlaceholder();
    }

    @Override
    public void setPlaceholder(Character placeholder) {
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
    public Iterator<Slot> iterator() {
        return delegate == null ? null : delegate.iterator();
    }
}
