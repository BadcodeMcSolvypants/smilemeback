/*
 This file is part of SmileMeBack.

 SmileMeBack is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 SmileMeBack is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with SmileMeBack.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smilemeback.activities.screens;


import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.smilemeback.R;
import com.smilemeback.activities.AddBaseActivity;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Dialogs;
import com.smilemeback.misc.ValidNameInputFilter;

import java.util.Map;

public class AddNameScreen extends Screen {

    protected Button focusImageNameButton;
    protected EditText imageEditText;

    public AddNameScreen(AddBaseActivity activity, int layoutResId) {
        super(activity, layoutResId);
    }

    public void onSetDefaultScreen(final Map<String, String> data) {
        super.onSetDefaultScreen(data);
        focusImageNameButton = (Button)activity.findViewById(R.id.focusImageNameButton);
        imageEditText = (EditText)activity.findViewById(R.id.imageName);
        imageEditText.setFilters(Dialogs.getNameFilters());

        imageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateNavButtons();
            }
        });

        focusImageNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(imageEditText, InputMethodManager.SHOW_FORCED);
            }
        });

        // set the data
        if (data.containsKey(Constants.ADDED_IMAGE_NAME)) {
            imageEditText.setText(data.get(Constants.ADDED_IMAGE_NAME));
        }

        updateNavButtons();
    }

    @Override
    public boolean canGoForward() {
        if (imageEditText != null) {
            return imageEditText.getText().length() > 0;
        }
        return false;
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    public Map<String, String> collectData(Map<String, String> data) {
        if (imageEditText.getText().length() > 0) {
            data.put(Constants.ADDED_IMAGE_NAME, imageEditText.getText().toString());
        }
        return data;
    }
}
