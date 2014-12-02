package smilemeback.com.smilememack.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import smilemeback.com.smilememack.R;


public class IconDisplayActivity extends Activity {

    public boolean isEnableInput() {
        return enableInput;
    }

    public void setEnableInput(boolean enableInput) {
        this.enableInput = enableInput;
        invalidateOptionsMenu();
    }

    protected boolean enableInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icondisplay);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icondisplay, menu);
        if (isEnableInput()) {
            menu.findItem(R.id.enable_input).setVisible(false);
        } else {
            menu.findItem(R.id.disable_input).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.enable_input:
                setEnableInput(true);
                break;
            case R.id.disable_input:
                setEnableInput(false);
                break;
            case R.id.back_to_categories:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_icon, container, false);
            return rootView;
        }
    }
}
