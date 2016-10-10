package ru.tinkoff.formatting.demo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.tinkoff.formatting.MaskDescriptor;
import ru.tinkoff.formatting.slots.PredefinedSlots;


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
                maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.CARD_NUMBER_STANDART);
                break;
            case MASK_CARD_MAESTRO:
                maskDescriptor = MaskDescriptor.ofSlots(PredefinedSlots.CARD_NUMBER_MAESTRO)
                        .withTermination(false);
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
