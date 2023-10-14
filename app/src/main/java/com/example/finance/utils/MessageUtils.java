package com.example.finance.utils;

import android.app.PendingIntent;
import android.telephony.SmsManager;

public class MessageUtils {
    public static boolean sendMessage(String destinationAddress, String scAddress, String message, PendingIntent sendIntent, PendingIntent deliveryIntent) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destinationAddress, scAddress, message, sendIntent, deliveryIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
