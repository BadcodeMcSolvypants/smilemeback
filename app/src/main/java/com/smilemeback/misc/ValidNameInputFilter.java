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


import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import com.smilemeback.storage.Name;

/**
 * {@link android.text.InputFilter} for removing illegal characters from image names.
 * The reason is that the main storage of images is on filesystem, therefore certain
 * reserved characters (such as /, . etc) are illegal.
 */
public class ValidNameInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean keepOriginal = true;
        StringBuilder sb = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isCharAllowed(c)) {
                sb.append(c);
            } else {
                keepOriginal = false;
            }
        }
        if (keepOriginal)
            return null;
        else {
            if (source instanceof Spanned) {
                SpannableString sp = new SpannableString(sb);
                TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                return sp;
            } else {
                return sb;
            }
        }
    }

    private boolean isCharAllowed(char c) {
        return Name.ILLEGAL_CHARACTERS.indexOf(c) == -1;
    }
}
