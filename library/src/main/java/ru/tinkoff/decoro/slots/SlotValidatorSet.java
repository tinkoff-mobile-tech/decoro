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

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Mikhail Artemev
 */
public class SlotValidatorSet extends HashSet<Slot.SlotValidator> implements Slot.SlotValidator {

    public static SlotValidatorSet setOf(Slot.SlotValidator... validators) {
        if (validators == null) {
            return new SlotValidatorSet();
        }

        SlotValidatorSet result = new SlotValidatorSet(validators.length);
        for (Slot.SlotValidator one : validators) {
            if (one instanceof SlotValidatorSet) {
                result.addAll((SlotValidatorSet) one);
            } else {
                result.add(one);
            }
        }

        return result;
    }

    public SlotValidatorSet() {
        super();
    }

    public SlotValidatorSet(int capacity) {
        super(capacity);
    }

    public int countValidIn(Collection<Character> chars) {
        if (chars == null) {
            throw new IllegalArgumentException("String to validate cannot be null");
        }

        int result = 0;
        for (Character c : chars) {
            if (c == null) {
                throw new NullPointerException("We don't support collections with null elements");
            }

            if (validate(c)) {
                result++;
            }
        }

        return result;
    }

    @Override
    public boolean validate(char value) {
        for (Slot.SlotValidator validator : this) {
            if (validator.validate(value)) {
                return true;
            }
        }

        return false;
    }
}
