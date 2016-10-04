package ru.tinkoff.formatting.watchers;

/**
 * @author Mikhail Artemyev
 */

class DiffMeasures {

    private static final int INSERT = 1;
    private static final int REMOVE = 1 << 1;

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
}
