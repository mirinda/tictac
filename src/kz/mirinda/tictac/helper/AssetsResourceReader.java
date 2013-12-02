package kz.mirinda.tictac.helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by asus on 05.10.13.
 */
public class AssetsResourceReader {
    public static final String TAG= AssetsResourceReader.class.getSimpleName();
    public static String getFileStringByPath(Context context,String assetsPath){
        try {
            InputStream is =context.getAssets().open(assetsPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String s="";
            StringBuffer sb = new StringBuffer("");
            while((  s =reader.readLine())!=null){
                sb.append(s).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "cannot open file :" + assetsPath,e);
            e.printStackTrace();
        }

        return "";
    }
}
