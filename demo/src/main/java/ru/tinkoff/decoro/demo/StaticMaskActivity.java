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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskDescriptor;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
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

        formatWatcher = new FormatWatcherImpl(MaskDescriptor.emptyMask().setTerminated(false));
        formatWatcher.installOn(dataEdit);

        maskPreviewView = (TextView) findViewById(R.id.textMaskPreview);
        maskPreviewView.setText(getString(R.string.mask_preview, ""));

        findViewById(R.id.buttonMask).setOnClickListener(this);

        final Mask mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setPlaceholder('*');
        mask.setShowingEmptySlots(true);
        Log.d("Mask", mask.toString());

        mask.insertFront("999");
        Log.d("Mask", mask.toString());
    }

    @Override
    public void onClick(View v) {
        DialogFragment dialogFragment = new MaskSelectorDialog();
        dialogFragment.show(getSupportFragmentManager(), MASK_DIALOG_TAG);
    }

    @Override
    public void onMaskSelected(@NonNull MaskDescriptor maskDescriptor, @NonNull String title) {
        formatWatcher.changeMask(maskDescriptor.setInitialValue(dataEdit.getText().toString()));
        maskPreviewView.setText(getString(R.string.mask_preview,maskDescriptor.toString()));
    }
}
