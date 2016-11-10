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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.tinkoff.decoro.MaskDescriptor;
import ru.tinkoff.decoro.slots.PredefinedSlots;


/**
 * @author Mikhail Artemev
 */
public class MaskSelectorDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public interface OnMaskSelectedListener {
        void onMaskSelected(@NonNull MaskDescriptor maskDescriptor, @NonNull String title);
    }

    private static final int MASK_PHONE = 0;
    private static final int MASK_CARD_STANDARD = 1;
    private static final int MASK_CARD_MAESTRO = 2;
    private static final int MASK_PASSPORT = 3;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setItems(R.array.masks, this)
                .setOnDismissListener(this)
                .setOnCancelListener(this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final MaskDescriptor maskDescriptor;
        switch (which) {
            case MASK_PHONE:
                maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.RUS_PHONE_NUMBER);
                break;
            case MASK_CARD_STANDARD:
                maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.CARD_NUMBER_STANDARD_MASKABLE);
                break;
            case MASK_CARD_MAESTRO:
                maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.CARD_NUMBER_MAESTRO_MASKABLE)
                        .setTerminated(false);
                break;
            case MASK_PASSPORT:
                maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.RUS_PASSPORT);
                break;

            default:
                throw new IllegalStateException("Unknown mask option: " + which);
        }

        final String[] stringArray = getContext().getResources().getStringArray(R.array.masks);
        publishMask(maskDescriptor, stringArray[which]);
    }

    public void publishMask(@NonNull MaskDescriptor maskDescriptor, @NonNull String title) {
        if (getActivity() instanceof OnMaskSelectedListener) {
            ((OnMaskSelectedListener) getActivity()).onMaskSelected(maskDescriptor, title);
        }
    }
}
