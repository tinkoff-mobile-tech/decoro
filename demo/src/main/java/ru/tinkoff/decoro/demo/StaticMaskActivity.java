package ru.tinkoff.decoro.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.tinkoff.decoro.MaskDescriptor;
import ru.tinkoff.decoro.watchers.FormatWatcherImpl;

/**
 * @author Mikhail Artemev
 */

public class StaticMaskActivity extends AppCompatActivity implements View.OnClickListener,
        MaskSelectorDialog.OnMaskSelectedListener{

    private static final String MASK_DIALOG_TAG = "mask-dialog";

    private EditText dataEdit;
    private TextView maskPreviewView;

    private FormatWatcherImpl formatWatcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_mask);

        dataEdit = (EditText) findViewById(R.id.editData);

        formatWatcher = new FormatWatcherImpl(MaskDescriptor.emptyMask().withTermination(false));
        formatWatcher.installOn(dataEdit);

        maskPreviewView = (TextView) findViewById(R.id.textMaskPreview);
        maskPreviewView.setText(getString(R.string.mask_preview, ""));

        findViewById(R.id.buttonMask).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        DialogFragment dialogFragment = new MaskSelectorDialog();
        dialogFragment.show(getSupportFragmentManager(), MASK_DIALOG_TAG);
    }

    @Override
    public void onMaskSelected(@NonNull MaskDescriptor maskDescriptor, @NonNull String title) {
        formatWatcher.changeMask(maskDescriptor.withInitialValue(dataEdit.getText().toString()));
        maskPreviewView.setText(getString(R.string.mask_preview,maskDescriptor.toString()));
    }
}
