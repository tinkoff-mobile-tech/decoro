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

package ru.tinkoff.decoro.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.slots.ValueInterpreter;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

/**
 * @author a.shishkin1
 */
public class SlotBehaviourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_behaviour);

        //hide hardcoded head behaviour
        init(R.id.slot0, new ExampleBehaviour() {
            @Override
            void changeMask(MaskImpl mask) {
                mask.setHideHardcodedHead(true);
            }
        });

        //forbid left movement
        init(R.id.slot1, new ExampleBehaviour() {

            @Override
            void changeSlots(Slot[] slots) {
                slots[3].setFlags(slots[3].getFlags() | Slot.RULE_FORBID_CURSOR_MOVE_LEFT);
            }
        });

        //replace 8 to 7 in +7, forbid left movement
        init(R.id.slot2, new ExampleBehaviour() {
            @Override
            void changeSlots(Slot[] slots) {
                slots[1].setValueInterpreter(new ValueInterpreter() {
                    @Override
                    public Character interpret(Character character) {
                        if (character == null) {
                            return null;
                        }
                        return character == '8' ? '7' : character;
                    }
                });
            }
        });

        //fill mask when installed, forbid left movement
        init(R.id.slot3, new ExampleBehaviour() {

            @Override
            boolean fillWhenInstall() {
                return true;
            }

            @Override
            void changeSlots(Slot[] slots) {
                slots[3].setFlags(slots[3].getFlags() | Slot.RULE_FORBID_CURSOR_MOVE_LEFT);
            }
        });

        //forbid right movement
        init(R.id.slot4, new ExampleBehaviour() {
            @Override
            void changeSlots(Slot[] slots) {
                slots[9].setFlags(slots[9].getFlags() | Slot.RULE_FORBID_CURSOR_MOVE_RIGHT);
            }
        });
    }

    private void init(int editTextId, ExampleBehaviour behaviour) {
        EditText editText = (EditText) findViewById(editTextId);
        Slot[] slots = Slot.copySlotArray(PredefinedSlots.RUS_PHONE_NUMBER);
        behaviour.changeSlots(slots);
        MaskImpl mask = new MaskImpl(slots, behaviour.isTerminated());
        behaviour.changeMask(mask);
        MaskFormatWatcher watcher = new MaskFormatWatcher(mask);
        if (behaviour.fillWhenInstall()) {
            watcher.installOnAndFill(editText);
        } else {
            watcher.installOn(editText);
        }
    }

    class ExampleBehaviour {
        void changeMask(MaskImpl mask) {
        }

        void changeSlots(Slot[] slots) {
        }

        boolean isTerminated() {
            return true;
        }

        boolean fillWhenInstall() {
            return false;
        }
    }
}
