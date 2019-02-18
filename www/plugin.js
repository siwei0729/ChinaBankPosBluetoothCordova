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
    },
    login: function (package,className) {
         return new Promise(function (resolve, reject) {
             exec(resolve, reject, PLUGIN_NAME, 'login', [package,className]);
            });
        },
    checkMpos: function (package) {
           return new Promise(function (resolve, reject) {
               exec(resolve, reject, PLUGIN_NAME, 'checkMpos', [package]);
                });
         },
     ocbcSale: function (transaction_amount,transaction_type,command_identifier,invoice_number) {
             return new Promise(function (resolve, reject) {
                 exec(resolve, reject, PLUGIN_NAME, 'ocbcSale', [transaction_amount,command_identifier,invoice_number]);
             });
         },
};

module.exports = ChinaBankPos;
