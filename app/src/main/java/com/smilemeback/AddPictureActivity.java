package com.smilemeback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;


public class AddPictureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_picture_addpicture);
    }


}
