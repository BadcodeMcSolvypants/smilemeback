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

import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

import com.smilemeback.R;



public class Toasts {

    /**
     * Tells the user to increase volume, if it is on mute.
     * @param context The context to display the toast.
     */
    public static void checkAndNotifyUserIfVolumeMute(Context context) {
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (audio.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            Toast.makeText(context, context.getString(R.string.addimage_common_speaker_is_muted), Toast.LENGTH_SHORT).show();
        }
    }
}
