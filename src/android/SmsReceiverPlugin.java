package name.ratson.cordova.sms_receiver;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Method;

public class SmsReceiverPlugin extends CordovaPlugin {
    private static final String TAG = "SmsReceiverPlugin";
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_REQUEST_PERMISSION_RATIONALE = "requestPermissionRationale";
    private static final String ACTION_RECEIVE_SMS = "startReception";
    private static final String ACTION_STOP_RECEIVE_SMS = "stopReception";
	private static final String ACTION_HAS_PERMISSION = "hasPermission";
	private static final String ACTION_CHECK_AVAILABILITY = "checkAvailability";
    private CallbackContext callbackReceive;
    private SmsReceiver smsReceiver = null;
    private boolean isReceiving = false;
    private final int RECEIVE_SMS_REQUEST_CODE = 20160916;
    public static CallbackContext mCallbackContext;
    public static PluginResult mPluginResult;    
	private static FrameLayout layout;
    private static Snackbar snackbar;

	public enum Result {
		GRANTED,
		DENIED,
		DENIED_WITHOUT_ASKING,
		NOT_AVAILABLE,
		AVAILABLE,
		ERROR
    }

    public SmsReceiverPlugin() {
        super();
    }
	
	@Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        layout = (FrameLayout) webView.getView().getParent();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) 
			throws JSONException {
        mCallbackContext = callbackContext;

		if (action.equals(ACTION_CHECK_AVAILABILITY)) {
			sendAvailabilityCheckResult();
			return true;
		}
		else if (action.equals(ACTION_RECEIVE_SMS)) {
			receiveSms();
			return true;
		}
		else if (action.equals(ACTION_STOP_RECEIVE_SMS)) {
			stopReceiveSms();
			return true;
		}
		else if (action.equals(ACTION_HAS_PERMISSION)) {
			sendHasPermissionCheckResult();
			return true;
		}
		else if (action.equals(ACTION_REQUEST_PERMISSION)) {
			sendRequestPermissionResult();
			return true;
		}
		else if (action.equals(ACTION_REQUEST_PERMISSION_RATIONALE)) {
			sendRequestPermissionRationaleResult();
			return true;
		}
        return false;
    }

    private void stopReceiveSms() {
		try	{
			if (this.smsReceiver != null) {
				smsReceiver.stopReceiving();
				smsReceiver = null;
			}
			this.isReceiving = false;

			// 1. Stop the receiving context
			mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			mPluginResult.setKeepCallback(false);
			this.callbackReceive.sendPluginResult(mPluginResult);

			// 2. Send result for the current context
			mPluginResult = new PluginResult(PluginResult.Status.OK);
			mCallbackContext.sendPluginResult(mPluginResult);
		} catch (Exception e) {
			Log.e(TAG, "Error: Stop Receiving SMS Error: Exception: " + e.toString());
            setPluginResultError(e.toString());
		}
    }

    private void receiveSms() {
        // if already receiving (this case can happen if the startReception is called
        // several times
        if (this.isReceiving) {
            // close the already opened callback ...
            mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            mPluginResult.setKeepCallback(false);
            this.callbackReceive.sendPluginResult(mPluginResult);

            // ... before registering a new one to the sms receiver
        }
        this.isReceiving = true;

        if (this.smsReceiver == null) {
            this.smsReceiver = new SmsReceiver();
            IntentFilter fp = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            fp.setPriority(1000);
            // fp.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            this.cordova.getActivity().registerReceiver(this.smsReceiver, fp);
        }

        this.smsReceiver.startReceiving(mCallbackContext);

        mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        mPluginResult.setKeepCallback(true);
        mCallbackContext.sendPluginResult(mPluginResult);
        this.callbackReceive = mCallbackContext;
    }

	private void sendAvailabilityCheckResult() {
		try	{
			if (hasSmsPossibility()) {
				mCallbackContext.success(Result.AVAILABLE.name());
			} else {
				mCallbackContext.success(Result.NOT_AVAILABLE.name());
			}
		} catch (Exception e) {
			//errorMessage = Result.ERROR.name();
			Log.e(TAG, "Error: SMS Feature Availability Check Result: Exception: " + e.toString());
            setPluginResultError(e.toString());
        }
    }
	
	private void sendHasPermissionCheckResult() {
		try	{
			boolean hasPermission = hasPermissionGranted(Manifest.permission.RECEIVE_SMS);
			if (hasPermission) {
				mCallbackContext.success(Result.GRANTED.name());
			} else {
				mCallbackContext.success(Result.DENIED.name());
			}
			mPluginResult = new PluginResult(PluginResult.Status.OK);
			mCallbackContext.sendPluginResult(mPluginResult);
			return;
		} catch (Exception e) {
			Log.e(TAG, "Error: Check SMS Existing Permissions Error: Exception: " + e.toString());
            setPluginResultError(e.toString());
        }
    }

    private void sendRequestPermissionResult() {
		String errorMessage = null;
		try	{
			if (!hasSmsPossibility()) {
				errorMessage = Result.NOT_AVAILABLE.name();
			} else {
				requestPermission(Manifest.permission.RECEIVE_SMS, false);
			}
		} catch (Exception e) {
			errorMessage = Result.ERROR.name();
			Log.e(TAG, "Error: SMS Request Permission Result: Exception: " + e.toString());
        }
		if (errorMessage != null) {
            Log.e(TAG, errorMessage);
            setPluginResultError(errorMessage);
        }
    }
	
    private void sendRequestPermissionRationaleResult() {
		String errorMessage = null;
		try	{
			if (!hasSmsPossibility()) {
				errorMessage = Result.NOT_AVAILABLE.name();
			} else {
				requestPermission(Manifest.permission.RECEIVE_SMS, true);
			}
		} catch (Exception e) {
			//errorMessage = Result.ERROR.name();
			Log.e(TAG, "Error: SMS Request Permission Result: Exception: " + e.toString());
        }
		if (errorMessage != null) {
            Log.e(TAG, errorMessage);
            setPluginResultError(errorMessage);
        }
    }

	private boolean hasSmsPossibility() throws Exception {
		return this.cordova.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

    private boolean hasPermissionGranted(String androidPermission) throws Exception {
        return (ContextCompat.checkSelfPermission(this.cordova.getActivity(), androidPermission) 
			== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(String androidPermission, boolean isRationale) throws Exception {
        if (!hasPermissionGranted(androidPermission)) {

			// Should we show an explanation?
			if (isRationale && shouldShowRequestPermissionRationale(this.cordova.getActivity(), androidPermission)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				this.showExplanation();
				setPluginResultError(Result.DENIED_WITHOUT_ASKING.name());
			} else {
				// No explanation needed, we can request the permission.
				cordova.requestPermissions(this, RECEIVE_SMS_REQUEST_CODE, new String[] { androidPermission });
			}
        } else {
			// Permission granted
			mPluginResult = new PluginResult(PluginResult.Status.OK);
			mCallbackContext.success(Result.GRANTED.name());
			mCallbackContext.sendPluginResult(mPluginResult);
		}
		return;
    }

	public void onRequestPermissionResult(int requestCode, String[] permissions,
                                         int[] grantResults) throws JSONException {
		if (requestCode == RECEIVE_SMS_REQUEST_CODE) {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && 
				grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				// permission was granted
				mPluginResult = new PluginResult(PluginResult.Status.OK);
				mCallbackContext.success(Result.GRANTED.name());
				mCallbackContext.sendPluginResult(mPluginResult);
			} else {
				// permission denied
				//Log.e(TAG, "Receive SMS permission denied.");
                setPluginResultError(Result.DENIED.name());
			}
			return;
		}
	}

	public boolean setPluginResultError(String errorMessage) {
        mCallbackContext.error(errorMessage);
        mPluginResult = new PluginResult(PluginResult.Status.ERROR);
        return false;
    }

	private boolean shouldShowRequestPermissionRationale(Activity activity, String permission) throws Exception{
        boolean shouldShow;
        try {
            java.lang.reflect.Method method = ActivityCompat.class.getMethod("shouldShowRequestPermissionRationale", Activity.class, java.lang.String.class);
            Boolean bool = (Boolean) method.invoke(null, activity, permission);
            shouldShow = bool.booleanValue();
        } catch (NoSuchMethodException e) {
            throw new Exception("shouldShowRequestPermissionRationale() method not found in ActivityCompat class. Check you have Android Support Library v23+ installed");
        }
        return shouldShow;
    }

	private void showExplanation()
    {
		//showSnackbar();
    }
	/*
	private synchronized void showSnackbar()
    {
		try {
			// if snackbar object
			if (snackbar != null) {
				snackbar.dismiss();
			}

			final String message = "Snackbar message";
			final String buttonText = "OK";
			final String durationType = "SHORT";

			if (cordova == null)
			{
				throw new Exception("cordova in null");
			}

			if (cordova.getActivity() == null)
			{
				throw new Exception("cordova.getActivity() in null");
			}
			if (this.layout == null)
			{
				throw new Exception("this.layout in null");
			}
			
			//***
			// Code Failling : android.view.InflateException: Binary XML file line #18: 
			// Binary XML file line #18: Error inflating class android.support.design.widget.Snackbar$SnackbarLayout
			//***
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_SHORT);
						snackbar.show();
					} catch (Exception e) {
						setPluginResultError(e.toString());
					}
					
					if (durationType.equals("SHORT")){
						snackbar.setDuration(Snackbar.LENGTH_SHORT);
					} else if(durationType.equals("LONG")){
						snackbar.setDuration(Snackbar.LENGTH_LONG);
					} else if(durationType.equals("INDEFINITE")){
						snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
					}
					if (buttonText != null && !buttonText.isEmpty()){
						snackbar.setAction(buttonText, new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								snackbar.dismiss();
								setPluginResultError(Result.DENIED.name());
							}
						});
					}
				}
			});
			
			return;
		} catch (Exception e) {
			Log.e(TAG, "ShowSnackbar Exception: " + e.getMessage());
			setPluginResultError(e.toString()); //Result.DENIED_WITHOUT_ASKING.name());
		}
    }*/
}
