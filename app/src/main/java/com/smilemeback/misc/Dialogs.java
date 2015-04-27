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
import android.widget.EditText;

public class Dialogs {

    /**
     * Show a confirmation dialog.
     */
    public static void confirmation(Context context, String title, String posButtonTitle, String negButtonTitle, DialogInterface.OnClickListener callBack) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setPositiveButton(posButtonTitle, callBack);
        dialog.setNegativeButton(negButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Show a text input dialog.
     */
    public static void input(Context context, String title, String initialText, String posButtonTitle, String negButtonTitle, final InputCallback callBack) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        final EditText edit = new EditText(context);
        edit.setSingleLine(true);
        edit.setText(initialText);
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
     * Callback for input function result.
     */
    public interface InputCallback {
        void inputDone(String text);
    }
}
