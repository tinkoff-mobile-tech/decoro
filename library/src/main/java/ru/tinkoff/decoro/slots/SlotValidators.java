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

package ru.tinkoff.decoro.slots;

import java.util.Arrays;

/**
 * @author Mikhail Artemev
 */
public final class SlotValidators {

    /**
     * SlotValidator that allows <b>any</b> characters to be input in the slot.
     */
    public static class GenerousValidator implements Slot.SlotValidator {

        @Override
        public boolean validate(char value) {
            return true;
        }

        /*
         * equals(Object) and hashCode() here are override in order to allow have only single
         * instance of this validator in a SlotValidatorSet (which is actually just a HashSet).
         */

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof GenerousValidator;
        }

        @Override
        public int hashCode() {
            return -56328;
        }
    }

    public static class DigitValidator implements Slot.SlotValidator {

        @Override
        public boolean validate(final char value) {
            return Character.isDigit(value);
        }

        /*
         * equals(Object) and hashCode() here are override in order to allow have only single
         * instance of this validator in a SlotValidatorSet (which is actually just a HashSet).
         */

        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof DigitValidator;
        }

        @Override
        public int hashCode() {
            return -56329;
        }
    }

    public static class MaskedDigitValidator extends DigitValidator {

        private static final char[] DEFAULT_DIGIT_MASK_CHARS = {'X', 'x', '*'};
        private char[] maskChars = DEFAULT_DIGIT_MASK_CHARS;

        public MaskedDigitValidator() {
        }

        public MaskedDigitValidator(char... maskChars) {
            if (maskChars == null) {
                throw new IllegalArgumentException("Mask chars cannot be null");
            }
            this.maskChars = maskChars;
        }

        @Override
        public boolean validate(char value) {
            if (super.validate(value)) {
                return true;
            }

            for (char aChar : maskChars) {
                if (aChar == value) {
                    return true;
                }
            }

            return false;
        }

        /*
         * equals(Object) and hashCode() here are override in order to allow have only single
         * instance of this validator in a SlotValidatorSet (which is actually just a HashSet).
         */

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MaskedDigitValidator that = (MaskedDigitValidator) o;

            return Arrays.equals(maskChars, that.maskChars);

        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(maskChars);
        }
    }

    public static class LetterValidator implements Slot.SlotValidator {

        private final boolean supportsEnglish;
        private final boolean supportsRussian;

        public LetterValidator() {
            this(true, true);
        }

        public LetterValidator(final boolean supportsEnglish,
                               final boolean supportsRussian) {
            this.supportsEnglish = supportsEnglish;
            this.supportsRussian = supportsRussian;
        }

        @Override
        public boolean validate(final char value) {
            return validateEnglishLetter(value) || validateRussianLetter(value);
        }

        private boolean validateEnglishLetter(final char value) {
            return supportsEnglish && isEnglishCharacter(value); // true when both 1
        }

        private boolean validateRussianLetter(final char value) {
            return supportsRussian && isRussianCharacter(value); // true when both 1
        }

        private boolean isEnglishCharacter(final int charCode) {
            return ('A' <= charCode && charCode <= 'Z') || ('a' <= charCode && charCode <= 'z');
        }

        private boolean isRussianCharacter(final int charCode) {
            return 'А' <= charCode && charCode <= 'я'; // 'А' is russian!!
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LetterValidator that = (LetterValidator) o;

            if (supportsEnglish != that.supportsEnglish) return false;
            return supportsRussian == that.supportsRussian;

        }

        @Override
        public int hashCode() {
            int result = (supportsEnglish ? 1 : 0);
            result = 31 * result + (supportsRussian ? 1 : 0);
            return result;
        }
    }


}
