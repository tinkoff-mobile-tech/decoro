package ru.tinkoff.decoro.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author Mikhail Artemev
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnCustomMask).setOnClickListener(this);
        findViewById(R.id.btnStaticMask).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final Class<? extends Activity> activityClass;
        switch (v.getId()) {
            case R.id.btnCustomMask:
                activityClass  = CustomMaskActivity.class;
                break;
            case R.id.btnStaticMask:
                activityClass  = StaticMaskActivity.class;
                break;
            default:
                throw new IllegalStateException();
        }

        startActivity(new Intent(this, activityClass));
    }
}
