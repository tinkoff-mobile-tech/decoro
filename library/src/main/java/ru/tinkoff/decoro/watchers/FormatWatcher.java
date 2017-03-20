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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.EditText;
import android.widget.TextView;

import ru.tinkoff.decoro.FormattedTextChangeListener;
import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskFactory;

/**
 * <p>
 * This class encapsulates logic of formatting (pretty printing) content of a TextView. All
 * the formatting logic is encapsulated inside the {@link Mask} class. This class is only
 * used to follow TextView changes and format it according to the {@link Mask}. It's okay
 * to
 * use it either with {@link TextView} or {@link EditText}. Important note for using with
 * bare {@link TextView}. Since its content usually changes with {@link TextView#setText},
 * inserting text should contain all the hardcoded symbols of the {@link Mask}.
 * <p>
 * All the children classes should implement their own way of creating {@link Mask}.
 *
 * @author Mikhail Artemev
 */
public abstract class FormatWatcher implements TextWatcher, MaskFactory {

    public static boolean DEBUG = false;

    private static final String TAG = "FormatWatcher";

    private DiffMeasures diffMeasures = new DiffMeasures();

    private CharSequence textBeforeChange;

    private Mask mask;
    private TextView textView;
    private boolean initWithMask;

    private boolean selfEdit = false;
    private boolean noChanges = false;
    private boolean formattingCancelled = false;

    private FormattedTextChangeListener callback;

    protected FormatWatcher() {
    }

    /**
     * Starts to follow text changes in the specified {@link TextView} to format any input. <br/>
     * IMPORTANT: this call will force watcher to re-create the mask
     *
     * @param textView text view to watch and format
     */
    public void installOn(@NonNull final TextView textView) {
        installOn(textView, false);
    }

    /**
     * Starts to follow text changes in the specified {@link TextView} to format any input.
     * Initial mask's value (e.g. hardcoded head) will be displayed in the view.<br/>
     * IMPORTANT: this call will force watcher to re-create the mask
     *
     * @param textView text view to watch and format
     */
    public void installOnAndFill(@NonNull final TextView textView) {
        installOn(textView, true);
    }

    public void removeFromTextView() {
        if (textView != null) {
            this.textView.removeTextChangedListener(this);
            this.textView = null;
        }
    }

    public boolean isInstalled() {
        return this.textView != null;
    }

    public boolean hasMask() {
        return mask != null;
    }

    /**
     * @param textView     an observable text view which content text will be formatted using
     *                     {@link
     *                     Mask}
     * @param initWithMask this flags defines whether hardcoded head of the mask (e.g "+7 ") will
     *                     fill the initial text of the {@code textView}.
     */
    protected void installOn(final TextView textView, final boolean initWithMask) {
        if (textView == null) {
            throw new IllegalArgumentException("text view cannot be null");
        }

        this.textView = textView;
        this.initWithMask = initWithMask;

        // try to remove us from listeners (useful in case user's trying to install the formatter twice on a same TextView)
        textView.removeTextChangedListener(this);

        textView.addTextChangedListener(this);

        this.mask = null;
        refreshMask();
    }

    public void refreshMask() {
        refreshMask(null);
    }

    public void refreshMask(@Nullable final CharSequence initialValue) {
        final boolean initial = this.mask == null;

        this.mask = createMask();
        checkMask();

        final boolean initiationNeeded = initialValue != null;
        diffMeasures = new DiffMeasures();
        if (initiationNeeded) {
            diffMeasures.setCursorPosition(mask.insertFront(initialValue));
        }

        if ((!initial || initWithMask || initiationNeeded) && isInstalled()) {
            selfEdit = true;
            final String formattedInitialValue = mask.toString();
            if (textView instanceof EditText) {
                final Editable editable = (Editable) textView.getText();
                editable.replace(0, editable.length(), formattedInitialValue, 0, formattedInitialValue.length());
            } else {
                textView.setText(formattedInitialValue);
            }
            setSelection(mask.getInitialInputPosition());
            selfEdit = false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return mask == null ? "" : mask.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        if (selfEdit || mask == null) {
            return;
        }

        // copy original string
        textBeforeChange = new String(s.toString());

        diffMeasures.calculateBeforeTextChanged(start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int insertedCount) {

        if (selfEdit || mask == null) {
            return;
        }

        CharSequence diffChars = null;
        if (diffMeasures.isInsertingChars()) {
            diffChars = s.subSequence(diffMeasures.getStartPosition(), diffMeasures.getInsertEndPosition());

            // Here's the android's "feature": if EditText supports input hints then modification of
            // a word from the TextWatcher's perspective will look like a removing a word and
            // inserting shorter (or longer) word instead. For example: removing trailing "s" from
            // the word "Carlos" will be presented as removing the word "Carlos" and inserting the
            // word "Carlo" instead. If modified sequence ends with hardcoded symbols then such a
            // modification will affect the cursor position. To avoid placing a cursor in an
            // unexpected position we should detect such changes and present them as the removing of
            // an actual character(s). So in the above example we should present a modification as a
            // simple removing of "s".
            if (diffMeasures.isTrimmingSequence()) {
                CharSequence diffBefore = textBeforeChange.subSequence(diffMeasures.getStartPosition(), diffMeasures.getInsertEndPosition());
                if (diffBefore.equals(diffChars)) {
                    // that's it. We're removing trailing character(s) of a word!
                    // so modify diff info and present it as a removing of those characters
                    diffMeasures.recalculateOnModifyingWord(diffChars.length());
                }
            }
        }

        // ask client code - should we proceed the modification of a mask
        if (callback != null && callback.beforeFormatting(textBeforeChange.toString(), s.toString())) {
            formattingCancelled = true;
            return;
        }

        noChanges = textBeforeChange.equals(s.toString());
        if (noChanges) {
            return;
        }

        if (diffMeasures.isRemovingChars()) {
            if (!diffMeasures.isInsertingChars()) {
                diffMeasures.setCursorPosition(mask.removeBackwards(diffMeasures.getRemoveEndPosition(), diffMeasures.getRemoveLength()));
            } else {
                diffMeasures.setCursorPosition(mask.removeBackwardsWithoutHardcoded(diffMeasures.getRemoveEndPosition(), diffMeasures.getRemoveLength()));
            }
        }

        if (diffMeasures.isInsertingChars()) {
            diffMeasures.setCursorPosition(mask.insertAt(diffMeasures.getStartPosition(), diffChars));
        }
    }

    @Override
    public void afterTextChanged(Editable newText) {
        if (formattingCancelled || selfEdit || mask == null || noChanges) {
            formattingCancelled = false;
            noChanges = false;
            return;
        }

        String formatted = mask.toString();

        final int cursorPosition = diffMeasures.getCursorPosition();
        // force change text of EditText we're attached to
        // only in case it's necessary (formatted text differs from inputted)
        if (!formatted.equals(newText.toString())) {
            int start = BaseInputConnection.getComposingSpanStart(newText);
            int end = cursorPosition > newText.length() ? newText.length() : cursorPosition;
            CharSequence pasted;
            if (start == -1 || end == -1) {
                pasted = formatted;
            } else {
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append(formatted.substring(0, start));
                SpannableString composing = new SpannableString(formatted.substring(start, end));
                // void setComposingSpans(Spannable text, int start, int end) in BaseInputConnection is hide api
                BaseInputConnection.setComposingSpans(composing);
                sb.append(composing);
                sb.append(formatted.substring(end, formatted.length()));
                pasted = sb;
            }
            selfEdit = true;
            newText.replace(0, newText.length(), pasted, 0, formatted.length());
            selfEdit = false;
        }

        if (0 <= cursorPosition && cursorPosition <= newText.length()) {
            setSelection(cursorPosition);
        }

        textBeforeChange = null;

        if (callback != null) {
            callback.onTextFormatted(this, toString());
        }
    }

    /**
     * @return Unmodifiable wrapper around inner mask. It allows to obtain inner mask statem
     * but not to change it.
     */
    public Mask getMask() {
        return new UnmodifiableMask(mask);
    }

    public boolean isAttachedTo(@NonNull View view) {
        return this.textView == view;
    }

    public void setCallback(@NonNull FormattedTextChangeListener callback) {
        this.callback = callback;
    }

    public int getCursorPosition() {
        return diffMeasures.getCursorPosition();
    }

    protected Mask getTrueMask() {
        return mask;
    }

    protected void setTrueMask(Mask mask) {
        this.mask = mask;
    }

    protected TextView getTextView() {
        return textView;
    }

    protected void setTextView(TextView textView) {
        this.textView = textView;
    }


    private void checkMask() {
        if (mask == null) {
            throw new IllegalStateException("Mask cannot be null at this point. Check maybe you forgot " +
                    "to call refreshMask()");
        }
    }

    private void setSelection(int position) {
        if (textView instanceof EditText && position <= textView.length()) {
            ((EditText) textView).setSelection(position);
        }
    }
}
