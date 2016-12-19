package ru.tinkoff.decoro.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.slots.ValueInterpreter;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

/**
 * Created by a.shishkin1 on 08.12.2016.
 */

public class SlotBehaviourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_behaviour);

        /* hide hardcoded head behaviour */
        init(R.id.slot0, new ExampleBehaviour() {
            @Override
            void changeMask(MaskImpl mask) {
                mask.setHideHardcodedHead(true);
            }
        });

        /* forbid move*/
        init(R.id.slot1, new ExampleBehaviour() {

            @Override
            void changeSlots(Slot[] slots) {
                slots[3].setFlags(slots[3].getFlags() | Slot.RULE_FORBID_CURSOR_MOVE_LEFT);
            }
        });


        init(R.id.slot2, new ExampleBehaviour() {
            @Override
            void changeSlots(Slot[] slots) {
                slots[1].setValueInterpreter(new ValueInterpreter() {
                    @Override
                    public Character interpret(Character character) {
                        if (character == null) return null;
                        return character == '8' ? '7' : character;
                    }
                });
            }
        });

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
        void changeMask(MaskImpl mask) {}
        void changeSlots(Slot[] slots) {}
        boolean isTerminated() { return true; }
        boolean fillWhenInstall() { return false; }
    }


}
