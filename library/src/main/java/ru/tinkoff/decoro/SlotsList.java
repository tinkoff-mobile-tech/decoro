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

import java.lang.reflect.Array;
import java.util.Iterator;

import ru.tinkoff.decoro.slots.Slot;

/**
 * @author Mikhail Artemev
 */
class SlotsList implements Iterable<Slot>, Parcelable {

    private int size = 0;

    private Slot firstSlot;
    private Slot lastSlot;

    public static SlotsList ofArray(@NonNull Slot[] slots) {
        SlotsList list = new SlotsList();

        list.size = slots.length;

        if (list.size == 0) {
            return list;
        }

        linkSlots(slots, list);

        return list;
    }

    private static void linkSlots(@NonNull Slot[] slots, SlotsList list) {
        list.firstSlot = new Slot(slots[0]);
        Slot prev = list.firstSlot;

        if (list.size == 1) {
            list.lastSlot = list.firstSlot;
        }

        // link slots
        for (int i = 1; i < slots.length; i++) {
            Slot next = new Slot(slots[i]);
            prev.setNextSlot(next);
            next.setPrevSlot(prev);

            prev = next;

            if (i == slots.length - 1) {
                list.lastSlot = next;
            }
        }
    }

    public SlotsList() {
    }

    public SlotsList(@NonNull SlotsList list) {
        if (!list.isEmpty()) {
            Slot previous = null;
            for (Slot slot : list) {
                final Slot newSlot = new Slot(slot);
                if (size == 0) {
                    this.firstSlot = newSlot;
                } else {
                    previous.setNextSlot(newSlot);
                    newSlot.setPrevSlot(previous);
                }

                previous = newSlot;
                size++;
            }

            this.lastSlot = previous;
        }
    }

    public boolean checkIsIndex(int position) {
        return 0 <= position && position < size;
    }

    public Slot getSlot(int index) {
        if (!checkIsIndex(index)) {
            return null;
        }

        Slot result;

        if (index < (size >> 1)) {
            // first half of a list
            result = firstSlot;
            for (int i = 0; i < index; i++) {
                result = result.getNextSlot();
            }
        } else {
            // second half of a list
            result = lastSlot;
            for (int i = size - 1; i > index; i--) {
                result = result.getPrevSlot();
            }
        }

        if (result == null) {
            throw new IllegalStateException("Slot inside the mask should not be null. But it is.");
        }

        return result;
    }

    /**
     * Inserts a slot on a specified position
     *
     * @param position index where new slot weill be placed should be >= 0 and <= size.
     * @param slot     slot ot insert. IMPORTANT: a copy of this slot will be inserted!
     * @return newly inserted slot (copy of the passed one)
     */
    public Slot insertSlotAt(final int position, @NonNull final Slot slot) {

        if (position < 0 || size < position) {
            throw new IndexOutOfBoundsException("New slot position should be inside the slots list. Or on the tail (position = size)");
        }

        final Slot toInsert = new Slot(slot);

        Slot currentSlot = getSlot(position);
        Slot leftNeighbour;
        Slot rightNeighbour = null;
        if (currentSlot == null) {
            // this can happen only when position == size.
            // it means we want to add the slot on the tail
            leftNeighbour = lastSlot;
        } else {
            leftNeighbour = currentSlot.getPrevSlot();
            rightNeighbour = currentSlot;
        }

        toInsert.setNextSlot(rightNeighbour);
        toInsert.setPrevSlot(leftNeighbour);

        if (rightNeighbour != null) {
            // right neighbour is only available for non-last slots
            rightNeighbour.setPrevSlot(toInsert);
        }

        if (leftNeighbour != null) {
            // left neighbour is only available for not-first slots
            leftNeighbour.setNextSlot(toInsert);
        }

        if (position == 0) {
            firstSlot = toInsert;
        } else if (position == size) {
            lastSlot = toInsert;
        }

        size++;

        return toInsert;
    }

    public Slot removeSlotAt(int position) {
        if (!checkIsIndex(position)) {
            throw new IndexOutOfBoundsException("Slot position should be inside the slots list");
        }
        return removeSlot(getSlot(position));
    }

    public Slot removeSlot(final Slot slotToRemove) {
        if (slotToRemove == null || !contains(slotToRemove)) {
            return null;
        }

        Slot left = slotToRemove.getPrevSlot();
        Slot right = slotToRemove.getNextSlot();

        if (left != null) {
            left.setNextSlot(right);
        } else {
            firstSlot = right;
        }

        if (right != null) {
            right.setPrevSlot(left);
        } else {
            lastSlot = left;
        }

        size--;

        return slotToRemove;
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }

        // iterate back because by default slots will try to pull new value from next slot
        // when someone try to remove their value
        Slot slot = lastSlot;
        while (slot != null) {
            slot.setValue(null);
            slot = slot.getPrevSlot();
        }
    }

    @Override
    public Iterator<Slot> iterator() {
        return new SlotsIterator(firstSlot);
    }

    @NonNull
    public Slot[] toArray() {
        if (isEmpty()) {
            return new Slot[0];
        }

        return toArray(new Slot[size()]);
    }

    @NonNull
    public <T> T[] toArray(@NonNull T[] array) {
        if (array == null || array.length < size) {
            array = (T[]) Array.newInstance(array.getClass().getComponentType(), size);
        }

        int index = 0;
        Object[] result = array;
        for (Slot slot : this) {
            result[index++] = slot;
        }

        return array;
    }

    public boolean add(Slot slot) {
        return insertSlotAt(size, slot) == slot;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final SlotsList list = (SlotsList) obj;
        if (list.size() != size()) return false;

        final Iterator<Slot> ourIterator = iterator();
        for (Slot otherSlot : list) {
            if (!ourIterator.next().equals(otherSlot)) {
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Slot getFirstSlot() {
        return firstSlot;
    }

    public void setFirstSlot(Slot firstSlot) {
        this.firstSlot = firstSlot;
    }

    public Slot getLastSlot() {
        return lastSlot;
    }

    public void setLastSlot(Slot lastSlot) {
        this.lastSlot = lastSlot;
    }

    private boolean contains(Slot o) {
        for (Slot slot : this) {
            if (slot == o) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.size);
        if (size > 0) {
            dest.writeTypedArray(toArray(), flags);
        }
    }

    protected SlotsList(Parcel in) {
        this.size = in.readInt();
        if (size > 0) {
            Slot[] slots = new Slot[this.size];
            in.readTypedArray(slots, Slot.CREATOR);
            linkSlots(slots, this);
        }
    }

    public static final Creator<SlotsList> CREATOR = new Creator<SlotsList>() {
        @Override
        public SlotsList createFromParcel(Parcel source) {
            return new SlotsList(source);
        }

        @Override
        public SlotsList[] newArray(int size) {
            return new SlotsList[size];
        }
    };

    private static class SlotsIterator implements Iterator<Slot> {

        Slot nextSlot;

        public SlotsIterator(Slot currentSlot) {
            if (currentSlot == null) {
                throw new IllegalArgumentException("Initial slot for iterator cannot be null");
            }

            this.nextSlot = currentSlot;
        }

        @Override
        public boolean hasNext() {
            return nextSlot != null;
        }

        @Override
        public Slot next() {
            Slot current = nextSlot;
            nextSlot = nextSlot.getNextSlot();
            return current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Mask cannot be modified from outside!");
        }
    }
}
