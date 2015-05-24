package us.apacible.emoticonselector.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Adapter for the faces list.
 *
 * @author japacible
 */
public class FaceAdapter extends BaseAdapter {
    private static final String FACES_FILE = "faces.txt";

    private Context mContext;
    private CharSequence[] facesArray;

    public FaceAdapter(Context c) {
        this.mContext = c;
        this.facesArray = readEmoticonsFromFile();
    }

    @Override
    public int getCount() { return this.facesArray.length; }

    @Override
    public Object getItem(int position) { return this.facesArray[position]; }

    @Override
    public long getItemId(int position) { return 0; }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = convertView == null ?
            new TextView(mContext) : (TextView) convertView;

        tv.setText(facesArray[position]);
        tv.setPadding(10, 10, 10, 10);
        tv.setElevation(5);
        return tv;
    }

    private CharSequence[] readEmoticonsFromFile() {
        ArrayList<CharSequence> facesArrayList = new ArrayList<>();

        try {
            InputStream in = mContext.getAssets().open(FACES_FILE);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(in));
            String line = reader.readLine();

            while (line != null) {
                facesArrayList.add(line);
                line = reader.readLine();
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return facesArrayList.toArray(new CharSequence[facesArrayList.size()]);
    }
}