SMS Receiver plugin for Cordova
===============================

This Cordova Android plugin allows you to receive incoming SMS. You have the possibility to stop the message broadcasting and, thus, avoid the incoming message native popup.

This plugin is based upon https://github.com/rehy/cordova-plugin-sms-receiver

* Include fix for the plugin's installation.
* Include fix for the deprecation of createFromPdu(byte[] pdu, String format) in API 23.
* Include a new function that ask permission with rationale.


## Install

```
cordova plugin add https://github.com/avitalys/cordova-plugin-sms-receiver.git --save
```

## Usage

### checkAvailability ###
Check if the SMS technology is supported by the device.

```js
SmsReceiver.checkAvailability((available) => {
  if (available === window.SmsReceiver.RESULT.AVAILABLE) {
    alert("SMS feature is available!");
  } else {
    alert("SMS feature is not available.");
  }
}, (error) => {
  alert("Error while checking the SMS feature availabilty: " + error);
});
```

### hasPermission ###
Check if the App has the 'Manifest.permission.RECEIVE_SMS' permission.

```js
SmsReceiver.hasPermission((result) => {
  if (result == window.SmsReceiver.RESULT.GRANTED) {
    alert("App has 'Manifest.permission.RECEIVE_SMS' permission!");
  } else {
    alert("App doesn't have the permission.");
  }
}, (error) => {
  alert("Error while checking RECEIVE_SMS permission: " + error);
});
```

### startReception ###
Start the SMS receiver waiting for incoming message.
The success callback function will be called everytime a new message is received.
The success return value is a JSON object {"originatingAddress":"+32472345678", "messageBody":"Hello World"}.
The error callback is called if an error occurs.

Example:
```js
SmsReceiver.startReception(({messageBody, originatingAddress}) => {
  alert(messageBody);
}, (error) => {
  alert("Error while receiving messages: " + error);
});
```

### stopReception ###
Stop the SMS receiver.

Example:
```js
SmsReceiver.stopReception(() => {
  alert("Successfuly stopped.");
}, (error) => {
  alert("Error while stopping the SMS receiver: " + error);
});
```

### requestPermission ###
Prompts the user to enable a permission.
The success callback function will be called if user has granted the requested permission.
The error callback is called if the permission wasn't received because of either NOT_AVAILABLE, DENIED or DENIED_WITHOUT_ASKING;

Example:
```js
SmsReceiver.requestPermission(() => {
  alert("We have permission for receiving SMS.");
}, (reason) => {
  alert("We do not have permission for receiving SMS.");
});
```

### requestPermissionRationale ###
Checks with the OS whether it is necessary to show a dialog explaining why the permission is needed (as described in https://developer.android.com/training/permissions/requesting.html#explain), and then shows the system permission dialog.

The success callback function will be called if user has granted the requested permission.
The error callback is called if the permission wasn't received because of either NOT_AVAILABLE, DENIED or DENIED_WITHOUT_ASKING;

Example:
```js
SmsReceiver.requestPermissionRationale(() => {
  alert("We have permission for receiving SMS.");
}, (reason) => {
  alert("We do not have permission for receiving SMS.");
});
```

### Aborting a broadcast ###
If you abort the broadcast using this plugin, the SMS will not be broadcast to other
applications like the native SMS app. So ... be careful !

A good way to manage this is to stop the SMS reception when the onPause event is fired and, when the onResume event is fired, restart the reception.
