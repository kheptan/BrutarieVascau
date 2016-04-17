import android.content.Context;
import android.widget.Toast;

import com.example.kp.brutarievascau.R;

/**
 * Created by kp on 14/12/15.
 */
public class InfoBox {
     Context context;
     String info_text;

     int duration = Toast.LENGTH_LONG;

    public InfoBox(Context context) {
        this.context = context;
    }

    public void ShowInfo(String infotext){
        Toast toast = Toast.makeText(context,infotext,duration);
        toast.show();
    }
}
