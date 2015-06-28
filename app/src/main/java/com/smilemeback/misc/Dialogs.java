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
package com.smilemeback.misc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.smilemeback.R;

/**
 * Common dialogs we need in the application.
 */
public class Dialogs {

    /**
     * Show a confirmation dialog_information.
     */
    public static void confirmation(Context context, String title, String text, String posButtonTitle, String negButtonTitle, DialogInterface.OnClickListener callBack) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        if (text != null) {
            dialog.setMessage(text);
        }
        dialog.setPositiveButton(posButtonTitle, callBack);
        dialog.setNegativeButton(negButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void confirmation(Context context, String title, String posButtonTitle, String negButtonTitle, DialogInterface.OnClickListener callBack) {
        confirmation(context, title, null, posButtonTitle, negButtonTitle, callBack);
    }

    /**
     * Show a text input dialog_information.
     */
    public static void input(Context context, String title, String initialText, String posButtonTitle, String negButtonTitle, final InputCallback callBack) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        final EditText edit = new EditText(context);
        edit.setSingleLine(true);
        edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edit.setFilters(getNameFilters());
        edit.append(initialText);
        dialog.setView(edit);
        dialog.setPositiveButton(posButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.inputDone(edit.getText().toString());
            }
        });
        dialog.setNegativeButton(negButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Show an information dialog_information with "do not show" possibility.
     *
     * @param memoryKey The key to store the "do not show" information about the dialog
     *                  in SharedPreferences.
     */
    public static void information(final Context context, String title, String text, final String memoryKey) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean show = settings.getBoolean(memoryKey, true);
        if (!show) {
            return;
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        LayoutInflater adbInflater = LayoutInflater.from(context);
        View layout = adbInflater.inflate(R.layout.dialog_information, null);
        final CheckBox dontShowAgain = (CheckBox) layout.findViewById(R.id.skip);
        final TextView textView = (TextView) layout.findViewById(R.id.message);
        textView.setText(Html.fromHtml(text));
        adb.setView(layout);
        adb.setTitle(title);

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                boolean show = !dontShowAgain.isChecked();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(memoryKey, show);
                // Commit the edits!
                editor.commit();
                return;
            }
        });

        adb.show();
    }

    /**
     * Callback for input function result.
     */
    public interface InputCallback {
        void inputDone(String text);
    }

    /**
     * @return Get the filterest for category/icon name fields.
     */
    public static InputFilter[] getNameFilters() {
        return new InputFilter[] {
                new ValidNameInputFilter(),
                new InputFilter.LengthFilter(Constants.MAX_NAME_LENGTH)
        };
    }
}
