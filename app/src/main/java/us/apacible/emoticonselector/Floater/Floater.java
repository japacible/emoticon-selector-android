package us.apacible.emoticonselector.Floater;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import us.apacible.emoticonselector.Activities.MainActivity;
import us.apacible.emoticonselector.Adapter.FaceAdapter;
import us.apacible.emoticonselector.R;

/**
 * Floating icon to click on to open the list of faces.
 *
 * @author japacible
 */
public class Floater extends Service {
    public static final int ID_NOTIFICATION = 1020888;

    private final int X_INITIAL = 10;
    private final int Y_INITIAL = 250;

    private final WindowManager.LayoutParams LAYOUT_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private boolean clicked;

    private WindowManager windowManager;
    private ImageView emoticonPicker;

    public Floater() { this.clicked = false; }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        emoticonPicker = new ImageView(this);
        emoticonPicker.setImageResource(R.drawable.ic_blur_circular_black_48dp);

        LAYOUT_PARAMS.gravity = Gravity.TOP | Gravity.LEFT;
        LAYOUT_PARAMS.x = X_INITIAL;
        LAYOUT_PARAMS.y = Y_INITIAL;

        windowManager.addView(emoticonPicker, LAYOUT_PARAMS);

        try {
            emoticonPicker.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams currentParams = LAYOUT_PARAMS;
                private int currentPositionY;
                private float touchPositionY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            clicked = true;

                            currentPositionY = currentParams.y;
                            touchPositionY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            clicked = false;

                            currentParams.x = X_INITIAL;
                            currentParams.y = currentPositionY +
                                    (int) (event.getRawY() - touchPositionY);

                            windowManager.updateViewLayout(emoticonPicker, currentParams);

                            // Get rid of |this| if positioned towards the bottom of the screen.
                            if (currentParams.y >= 1200)
                                Floater.this.stopSelf();
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        emoticonPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            if (clicked)
                showPopupWindow(emoticonPicker);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (emoticonPicker != null)
            windowManager.removeView(emoticonPicker);
    }

    private void showPopupWindow(View anchor) {
        try {
            ListPopupWindow popup = new ListPopupWindow(this);
            popup.setAnchorView(anchor);
            popup.setWidth(500);
            popup.setHorizontalOffset(50);
            popup.setAdapter(new FaceAdapter(getApplicationContext()));
            popup.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg, View view, int position, long id) {
                copyAndToast(view);
                }
            });
            popup.setModal(true);
            popup.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotification(){
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_blur_on_white_48dp)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_message));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        Intent resultIntent = new Intent(this, MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        Intent notificationIntent = new Intent(getApplicationContext(), Floater.class);
        PendingIntent resultPendingIntent = PendingIntent.getService(getApplicationContext(), 0,
                                                                     notificationIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());
    }

    // Copies the clicked text to clipboard and show a toast.
    private void copyAndToast(View view) {
        TextView textClicked = (TextView) view;
        showToast(textClicked.getText());
        copy(textClicked.getText());
    }

    // Shows a toast with the given CharSequence.
    private void showToast(CharSequence cs) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, cs, duration).show();
    }

    // Copies the given CharSequence to clipboard.
    private void copy(CharSequence cs) {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", cs);
        clipboard.setPrimaryClip(clip);
    }
}