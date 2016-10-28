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
                activityClass = CustomMaskActivity.class;
                break;
            case R.id.btnStaticMask:
                activityClass = StaticMaskActivity.class;
                break;
            default:
                throw new IllegalStateException();
        }

        startActivity(new Intent(this, activityClass));
    }
}
