package yau.tommy.com.amusic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yiu Chung Yau on 11/7/18.
 */
public class NotificationHelper extends Activity {

    private NotificationHelper ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ctx = this;
        String action = (String) getIntent().getExtras().get("DO");
        if (action.equals("prev")) {
            Toast t = Toast.makeText(this,"prev",Toast.LENGTH_SHORT);
            t.show();
            Log.e("helper","prev!!!");
        } else if (action.equals("playPause")) {
            Toast t = Toast.makeText(this,"playPause",Toast.LENGTH_SHORT);
            t.show();
        } else if (action.equals("next")) {
            Toast t = Toast.makeText(this,"next",Toast.LENGTH_SHORT);
            t.show();
        }
        finish();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}