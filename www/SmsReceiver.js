function SmsReceiver() { };

SmsReceiver.prototype.isSupported = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'hasSMSPossibility', 
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

SmsReceiver.prototype.requestPermission = function (successCallback, errorCallback) {
    cordova.exec(
        successCallback, 
        errorCallback, 
        'SmsReceiverPlugin', 
        'requestPermission', 
        []
    );
};

module.exports = new SmsReceiver();
