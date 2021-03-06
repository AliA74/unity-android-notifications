package net.agasper.unitynotification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.unity3d.player.UnityPlayer;
//import com.unity3d.player.UnityPlayerNativeActivity;

public class UnityNotificationManager extends BroadcastReceiver
{
	
	public static void SetNotification(int id, long delayMs, String title, String message, String ticker, int sound, int vibrate, 
            int lights, String largeIconResource, String smallIconResource, int bgColor, int executeMode,String IntentUri)
    {
        Activity currentActivity = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, UnityNotificationManager.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);
        intent.putExtra("l_icon", largeIconResource);
        intent.putExtra("s_icon", smallIconResource);
        
        //if an intent view was intended
        if(IntentUri!=null && IntentUri.isEmpty()==false)
        	intent.putExtra("intentUri", IntentUri);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
        	if (executeMode == 2)
        		am.setExactAndAllowWhileIdle(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
        	else if (executeMode == 1)
        		am.setExact(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
        	else
        		am.set(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
        }
        else
        	am.set(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
    }
    
    public static void SetRepeatingNotification(int id, long delay, String title, String message, String ticker, long rep, int sound, int vibrate, int lights, 
    		String largeIconResource, String smallIconResource, int bgColor,String IntentUri)
    {
    	Activity currentActivity = UnityPlayer.currentActivity;
    	AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, UnityNotificationManager.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);
        intent.putExtra("l_icon", largeIconResource);
        intent.putExtra("s_icon", smallIconResource);
        
      //if an intent view was intended
        if(IntentUri!=null && IntentUri.isEmpty()==false)
        	intent.putExtra("intentUri", IntentUri);
        
    	am.setRepeating(0, System.currentTimeMillis() + delay, rep, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
    }
    
    public void onReceive(Context context, Intent intent)
    {
    	NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String ticker = intent.getStringExtra("ticker");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String s_icon = intent.getStringExtra("s_icon");
        String l_icon = intent.getStringExtra("l_icon");
        int color = intent.getIntExtra("color", 0);
        Boolean sound = Boolean.valueOf(intent.getBooleanExtra("sound", false));
        Boolean vibrate = Boolean.valueOf(intent.getBooleanExtra("vibrate", false));
        Boolean lights = Boolean.valueOf(intent.getBooleanExtra("lights", false));
        int id = intent.getIntExtra("id", 0);
        String IntentUri = null;
        
        if(intent.hasExtra("intentUri"))
        {
        	IntentUri = intent.getStringExtra("intentUri");
        }
               
        Resources res = context.getResources();
        

		Intent notificationIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		
		if(IntentUri!=null)
		{//replace launch intent with the link passed
			
			notificationIntent = new Intent( Intent.ACTION_VIEW,Uri.parse(IntentUri) );
		}
		
        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        
        builder.setContentIntent(contentIntent)
        	.setWhen(System.currentTimeMillis())
        	.setAutoCancel(true)
        	.setContentTitle(title)
        	.setContentText(message);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        	builder.setColor(color);
        
        if(ticker != null && ticker.length() > 0)
            builder.setTicker(ticker);
               
		if (s_icon != null && s_icon.length() > 0)
			builder.setSmallIcon(res.getIdentifier(s_icon, "drawable", context.getPackageName()));
		
		if (l_icon != null && l_icon.length() > 0)
			builder.setLargeIcon(BitmapFactory.decodeResource(res, res.getIdentifier(l_icon, "drawable", context.getPackageName())));
		
        if(sound.booleanValue())
            builder.setSound(RingtoneManager.getDefaultUri(2));
        
        if(vibrate.booleanValue())
            builder.setVibrate(new long[] {
                1000L, 1000L
            });
        
        if(lights.booleanValue())
            builder.setLights(Color.GREEN, 3000, 3000);
       
        
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public static void CancelNotification(int id)
    {
        Activity currentActivity = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, UnityNotificationManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(currentActivity, id, intent, 0);
        am.cancel(pendingIntent);
    }
}
