package bd.com.universal.eparking.owner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Nipon on 9/29/2018.
 */

public class MyFirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);

        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext(),getString(R.string.default_notification_channel_id))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification.build());

    }
}
