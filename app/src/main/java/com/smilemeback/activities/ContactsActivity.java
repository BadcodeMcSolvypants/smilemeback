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
package com.smilemeback.activities;


import android.os.Bundle;
import android.webkit.WebView;

import com.smilemeback.R;


public class ContactsActivity extends HomeEnabledActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contacts_page);
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl(getContactsHtmlPath());
    }

    private String getContactsHtmlPath() {
        return "file:///android_asset/contacts_page/" + getString(R.string.language) + "_contacts.html";
    }
}

