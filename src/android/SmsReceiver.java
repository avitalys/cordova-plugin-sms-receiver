package name.ratson.cordova.sms_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.telephony.SmsMessage;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

public class SmsReceiver extends BroadcastReceiver {
    public static final String SMS_EXTRA_NAME = "pdus";
    private CallbackContext callbackReceive;
    private boolean isReceiving = true;

    // This broadcast boolean is used to continue or not the message broadcast
    // to the other BroadcastReceivers waiting for an incoming SMS (like the native SMS app)
    private boolean broadcast = false;

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            try {
                // Get the SMS map from Intent
                Bundle intentExtras = intent.getExtras();

                if (intentExtras != null) {
                    // Retrieve the SMS message received
                    Object[] smsExtra = (Object[]) intentExtras.get(SMS_EXTRA_NAME);

                    if (smsExtra != null) {
                        String msgBody = "";
                        SmsMessage[] msgs = new SmsMessage[smsExtra.length];

                        // Long SMS are splitted to multiple part - and 
                        // this is why we need to collect the message body.
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = getIncomingMessage(smsExtra[i], intentExtras);
                            msgBody += msgs[i].getMessageBody();
                        }

                        // Create the result JSON object
                        if (this.isReceiving && this.callbackReceive != null) {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("messageBody", msgBody);
                            jsonObj.put("originatingAddress", msgs[0].getOriginatingAddress());
                            jsonObj.put("getTimestampMillis", msgs[0].getTimestampMillis());
                            jsonObj.put("isStatusReportMessage", msgs[0].isStatusReportMessage());

                            PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObj);
                            result.setKeepCallback(true);
                            callbackReceive.sendPluginResult(result);
                        }
                    }

                    // If the plugin is active and we don't want to broadcast to other receivers
                    if (this.isReceiving && !broadcast) {
                        this.abortBroadcast();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.toString());
                result.setKeepCallback(true);
                callbackReceive.sendPluginResult(result);
            }
        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle intentExtras) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get the format extra from the Telephony.Sms.Intents.SMS_RECEIVED_ACTION intent
            String format = intentExtras.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    public void broadcast(boolean v) {
        this.broadcast = v;
    }

    public void startReceiving(CallbackContext ctx) {
        this.callbackReceive = ctx;
        this.isReceiving = true;
    }

    public void stopReceiving() {
        this.callbackReceive = null;
        this.isReceiving = false;
    }
}
