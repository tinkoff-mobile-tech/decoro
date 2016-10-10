package ru.tinkoff.formatting.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import ru.tinkoff.formatting.MaskDescriptor;
import ru.tinkoff.formatting.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.formatting.watchers.FormatWatcherImpl;

/**
 * @author Mikhail Artemev
 */

public class CustomMaskActivity extends AppCompatActivity {

    private EditText dataEdit;
    private EditText maskEdit;

    private FormatWatcherImpl formatWatcher;

    private TextWatcher maskTextWatcher = new TextWatcherImpl() {
        @Override
        public void afterTextChanged(Editable s) {
            final MaskDescriptor maskDescriptor = MaskDescriptor.ofRawMask(s.toString())
                    .withHideHardcodedHead(true)
                    .withInitialValue(dataEdit.getText().toString());
            formatWatcher.changeMask(maskDescriptor);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mask);

        maskEdit = (EditText) findViewById(R.id.editMask);
        maskEdit.addTextChangedListener(maskTextWatcher);

        formatWatcher = new FormatWatcherImpl(new UnderscoreDigitSlotsParser(),
                MaskDescriptor.emptyMask().withTermination(false));

        dataEdit = (EditText) findViewById(R.id.editData);
        formatWatcher.installOn(dataEdit);
    }


}
