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

/**
 * Class containing constants used in the application.
 */
public class Constants {
    // Intent variables
    public static final String CATEGORY_INDEX = "category_index";
    public static final String IMAGE_DRAG_TAG = "image_drag_tag";

    public static final String ADDED_IMAGE_NAME = "added_image_name";
    public static final String ADDED_IMAGE_PATH = "added_image_path";
    public static final String ADDED_IMAGE_AUDIO_PATH = "added_image_audio_path";

    public static final int MAX_ICONS_IN_DRAG_SHADOW = 8;
    public static final float SELECTED_ICONVIEW_ALPHA = 0.5f;
    public static final float DISABLED_BUTTON_ALPHA = 0.4f;

    public static final int AUDIO_SAMPLING_RATE = 44100;
    public static final int AUDIO_NUM_CHANNELS = 1;
    public static final int AUDIO_BITRATE = 192 * 1024;

    public static final int SMOOTH_SCROLL_DURATION = 500;
    public static final int MAX_NAME_LENGTH = 20;

    public static final String PREFS_NAME = "SmbPreferences";
    public static final String PREFS_SHOW_LOCK_HELP = "show_lock_help";
    public static final String PREFS_PASSWORD = "password";
    public static final String PREFS_DEFAULT_PASSWORD = "smile";
    public static final String PREFS_SHOW_GREETING = "show_greeting";
    public static final String PREFS_SHOW_EDITMODE = "show_edit";
}
