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

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.tinkoff.decoro.slots.Slot;

/**
 * Interface representing a Mask that holds slots with user input and hardcoded data.
 * The mask performs actual formatting of the input and able to return formatted and unformatted
 * string.
 * <br/><br/>
 * See: {@link MaskImpl} - the main implementation of this interface.
 *
 * @author Mikhail Artemev
 */
public interface Mask extends Iterable<Slot>, Parcelable {

    /**
     * @return mask contents without <b>decoration</b> characters.
     * Decoration characters are specified with {@link Slot#TAG_DECORATION} tag on a corresponding
     * {@link Slot}
     */
    @NonNull
    String toUnformattedString();

    /**
     * Looks for initial position for cursor since buffer can be predefined with starting
     * characters
     */
    int getInitialInputPosition();

    /**
     * Checks whether there're filled out slot except of 'hardcoded' ones.
     * This can happen only if some text was inserted to a mask (e.g. by user input).
     *
     * @return true if there're anything except hardcoded data in the mask
     */
    boolean hasUserInput();

    /**
     * Checks whether th whole mask is filled out.
     *
     * @return true if the're no more empty slots
     */
    boolean filled();

    /**
     * Removes user input from this mask.
     */
    void clear();

    /**
     * Method insert {@code input} to the buffer. Only validated characters would be inserted.
     * Hardcoded slots are omitted. Method returns new cursor position that is affected by input
     * and {@code cursorAfterTrailingHardcoded} flag. In most cases if input string is followed by
     * a sequence of hardcoded characters we should place cursor after them. But this behaviour can
     * be modified by {@code cursorAfterTrailingHardcoded} flag.
     *
     * @param position                     from which position to begin input
     * @param input                        string to insert
     * @param cursorAfterTrailingHardcoded when input is followed by a hardcoded characters
     *                                     sequence then this flag defines whether new cursor
     *                                     position should be after or before them
     * @return cursor position after insert
     */
    int insertAt(int position, @Nullable CharSequence input, boolean cursorAfterTrailingHardcoded);

    /**
     * Convenience method for {@link MaskImpl#insertAt(int, CharSequence, boolean)} that always
     * places cursor after trailing hardcoded sequence.
     *
     * @param position from which position to begin input
     * @param input    string to insert
     * @return cursor position after insert
     */
    int insertAt(int position, @Nullable CharSequence input);

    /**
     * Convenience method for {@link MaskImpl#insertAt(int, CharSequence, boolean)} that inserts
     * text at the first position of a mask and always places cursor after trailing hardcoded
     * sequence.
     *
     * @param input string to insert
     * @return cursor position after insert
     */
    int insertFront(@Nullable CharSequence input);

    /**
     * Removes available symbols from the buffer. This method should be called on deletion event of
     * user's input. Symbols are deleting backwards (just as backspace key). Hardcoded symbols
     * would not be deleted, only cursor will be moved over them.
     * <p>
     * Method also updates {@code showHardcodedTail} flag that defines whether tail of hardcoded
     * symbols (at the end of user's input) should be shown. In most cases it should not. The only
     * case when they are visible - buffer starts with them and deletion was inside them.
     *
     * @param position from where to start deletion
     * @param count    number of  symbols to delete.
     * @return new cursor position after deletion
     */
    int removeBackwards(int position, int count);

    int removeBackwardsWithoutHardcoded(int position, int count);

    /**
     * Returns current size of a mask in slots. <br/>
     * <b>IMPORTANT:</b> empty slots are also counted.
     *
     * @return returns current size of a mask in slots
     */
    int getSize();

    /**
     * @return true if mask allows to display slots with no value, putting <b>placeholder</b>
     * on those positions.
     * <br/><br/>
     * See: {@link #setPlaceholder(Character)}
     */
    boolean isShowingEmptySlots();

    /**
     * Defines whether mask allows to display slots with no value, putting <b>placeholder</b>
     * on those positions.
     * <br/><br/>
     * See: {@link #setPlaceholder(Character)}
     *
     * @param showingEmptySlots true if mask allows empty slots to be displayed
     */
    void setShowingEmptySlots(boolean showingEmptySlots);

    /**
     * @return character that will be put on empty position when calling {@link #toString()} or
     * {@link #toUnformattedString()}.
     * <br/><br/>
     * See: {@link #setShowingEmptySlots(boolean)}
     */
    @NonNull
    Character getPlaceholder();

    /**
     * Sets character that will be put on empty position when calling {@link #toString()} or
     * {@link #toUnformattedString()}. Placeholder should not be null!
     * <br/><br/>
     * See: {@link #setShowingEmptySlots(boolean)}
     *
     * @param placeholder character that represents empty slots
     */
    void setPlaceholder(Character placeholder);

    /**
     * @return true if mask will hide leading hardcoded sequence of its contents.
     * <br/><br/>
     * See: {@link #setHideHardcodedHead(boolean)}
     */
    boolean isHideHardcodedHead();

    /**
     * Defines whether mask should hide leading hardcoded sequence when calling {@link #toString()}
     * or {@link #toUnformattedString()}.
     * <br/><br/>
     * Example of "hardcoded head" is <b>+7 (</b> in the russian cellphone number mask defined in
     * {@link
     * ru.tinkoff.decoro.slots.PredefinedSlots#RUS_PHONE_NUMBER}.
     *
     * @param shouldHideHardcodedHead the "hide hardcode head" value
     */
    void setHideHardcodedHead(boolean shouldHideHardcodedHead);

    /**
     * @return true if mask won't allow to perform further input if all the slots are already filled
     * out.
     * <br/><br/>
     * See: {@link #setForbidInputWhenFilled(boolean)}
     */
    boolean isForbidInputWhenFilled();

    /**
     * Defines whether mask won't allow to perform further input if all the slots are already
     * filled out. If <b>false</b> passed - mask will shift existing input when trying to insert
     * text inside filled out mask ({@link #filled()} returns true).
     * <br/><br/>
     * WARNING: This flag only makes sense for terminated masks.
     *
     * @param forbidInputWhenFilled flag that forbids overwriting terminated filled out masks.
     */
    void setForbidInputWhenFilled(boolean forbidInputWhenFilled);

    /**
     * Finds cursor position in unformatted string that corresponds to passed cursor position
     * in a formatted string.
     * @param cursorPosition
     * @return corresponding cursor position in unformatted string
     * @throws IndexOutOfBoundsException if cursorPosition < 0 or cursorPosition > size
     */
    int findCursorPositionInUnformattedString(int cursorPosition);
}
