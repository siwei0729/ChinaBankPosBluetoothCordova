var exec = require('cordova/exec');

var PLUGIN_NAME = 'ChinaBankPos';

var ChinaBankPos = {
    
    sale: function (amount,transId,phone,email) {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'sale', [amount,transId,phone,email]);
        });
    },
    refund: function (transId) {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'refund', [transId]);
        });
    }
};

module.exports = ChinaBankPos;
