function SmsReceiver() {
    SmsReceiver.prototype.RESULT = {
        GRANTED: 'GRANTED',
        DENIED: 'DENIED',
        DENIED_WITHOUT_ASKING: 'DENIED_WITHOUT_ASKING',
        NOT_AVAILABLE: 'NOT_AVAILABLE',
        AVAILABLE: 'AVAILABLE',
        ERROR: 'ERROR'
    }
};

SmsReceiver.prototype.checkAvailability = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'checkAvailability', 
        []
    );
};

SmsReceiver.prototype.startReception = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'startReception', 
        []
    );
};

SmsReceiver.prototype.stopReception = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'stopReception', 
        []
    );
};

SmsReceiver.prototype.hasPermission = function (successCallback, errorCallback) {
    cordova.exec(
        successCallback, 
        errorCallback, 
        'SmsReceiverPlugin', 
        'hasPermission', 
        []
    );
};

SmsReceiver.prototype.requestPermission = function (successCallback, errorCallback) {
    cordova.exec(
        successCallback, 
        errorCallback, 
        'SmsReceiverPlugin', 
        'requestPermission', 
        []
    );
};

SmsReceiver.prototype.requestPermissionRationale = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'requestPermissionRationale', 
        []
    );
};

module.exports = new SmsReceiver();
