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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import ru.tinkoff.decoro.MaskDescriptor;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.watchers.FormatWatcherImpl;

/**
 * @author Mikhail Artemev
 */

public class CustomMaskActivity extends AppCompatActivity {

    private EditText dataEdit;

    private FormatWatcherImpl formatWatcher;

    private TextWatcher maskTextWatcher = new TextWatcherImpl() {
        @Override
        public void afterTextChanged(Editable s) {
            final MaskDescriptor maskDescriptor;

            if (TextUtils.isEmpty(s)) {
                maskDescriptor = MaskDescriptor.emptyMask().setTerminated(false);
            } else {
                maskDescriptor = MaskDescriptor.ofRawMask(s.toString())
                        .setInitialValue(dataEdit.getText().toString());
            }

            formatWatcher.changeMask(maskDescriptor);

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mask);

        EditText maskEdit = (EditText) findViewById(R.id.editMask);
        maskEdit.addTextChangedListener(maskTextWatcher);

        formatWatcher = new FormatWatcherImpl(new UnderscoreDigitSlotsParser(),
                MaskDescriptor.emptyMask().setTerminated(false));

        dataEdit = (EditText) findViewById(R.id.editData);
        formatWatcher.installOn(dataEdit);
    }


}
