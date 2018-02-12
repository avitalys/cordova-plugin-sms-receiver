function SmsReceiver() { };

FingerprintAuth.prototype.isSupported = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'hasSMSPossibility', 
        []
    );
};

FingerprintAuth.prototype.startReception = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'startReception', 
        []
    );
};

FingerprintAuth.prototype.stopReception = function (successCallback, failureCallback) {
    cordova.exec(
        successCallback, 
        failureCallback, 
        'SmsReceiverPlugin', 
        'stopReception', 
        []
    );
};

FingerprintAuth.prototype.requestPermission = function (successCallback, errorCallback) {
    cordova.exec(
        successCallback, 
        errorCallback, 
        'SmsReceiverPlugin', 
        'requestPermission', 
        []
    );
};

SmsReceiver = new SmsReceiver();
module.exports = SmsReceiver;