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

import java.util.Locale;

/**
 * @author Mikhail Artemev
 */
class DiffMeasures {

    private static final int INSERT = 1;
    private static final int REMOVE = 1 << 1;

    private static final int MASK_BOTH_TYPE = 3;

    private int diffStartPosition;
    private int diffInsertLength;
    private int diffRemoveLength;
    private int diffType;
    private int cursorPosition;

    private boolean trimmingSequence;

    public DiffMeasures() {
    }

    public void calculateBeforeTextChanged(int start, int count, int after) {
        diffStartPosition = start;
        diffRemoveLength = 0;
        diffType = 0;
        diffInsertLength = 0;
        cursorPosition = -1;

        if (after > 0) {
            diffType |= INSERT;
            diffInsertLength = after;
        }

        if (count > 0) {
            diffType |= REMOVE;
            diffRemoveLength = count;
        }

        trimmingSequence =
                diffInsertLength > 0
                        && diffRemoveLength > 0
                        && diffInsertLength < diffRemoveLength;
    }

    public void recalculateOnModifyingWord(int realDiffLen) {
        diffRemoveLength -= diffInsertLength;
        diffStartPosition += realDiffLen;
        diffType &= ~INSERT;
    }

    public boolean isInsertingChars() {
        return (diffType & INSERT) == INSERT;
    }

    public boolean isRemovingChars() {
        return (diffType & REMOVE) == REMOVE;
    }

    public int getInsertEndPosition() {
        return diffStartPosition + diffInsertLength;
    }

    public int getRemoveEndPosition() {
        return diffStartPosition + diffRemoveLength - 1;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public int getStartPosition() {
        return diffStartPosition;
    }

    public int getDiffInsertLength() {
        return diffInsertLength;
    }

    public int getRemoveLength() {
        return diffRemoveLength;
    }

    public int getDiffType() {
        return diffType;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public boolean isTrimmingSequence() {
        return trimmingSequence;
    }

    @Override
    public String toString() {
        String type = null;
        if ((MASK_BOTH_TYPE & diffType) == MASK_BOTH_TYPE) {
            type = "both";
        } else if ((INSERT & diffType) == INSERT) {
            type = "insert";
        } else if ((REMOVE & diffType) == REMOVE) {
            type = "remove";
        } else if (diffType == 0) {
            type = "none";
        }
        if (type == null) {
            throw new IllegalStateException("unknown behaviour for diffType " + diffType);
        }
        return String.format(Locale.getDefault(),
                "[ DiffMeasures type=%s, diffStartPosition=%d, diffInsertLength=%d, diffRemoveLength=%d, cursor: %d ]",
                type,
                diffStartPosition,
                diffInsertLength,
                diffRemoveLength,
                cursorPosition
        );
    }

}
