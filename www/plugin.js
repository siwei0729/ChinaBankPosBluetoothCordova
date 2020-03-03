var exec = require('cordova/exec');

var PLUGIN_NAME = 'ChinaBankPos';

var ChinaBankPos = {

    sale: function (amount,transId,phone,email) {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'sale', [amount,transId,phone,email]);
        });
    },
    bocUnionPay: function (amount,transId,phone,email) {
        return new Promise(function (resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, 'bocUnionPay', [amount,transId,phone,email]);
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
     ascanSale: function (appName,activityName,transaction_amount,transaction_type,command_identifier,invoice_number) {
             return new Promise(function (resolve, reject) {
                 exec(resolve, reject, PLUGIN_NAME, 'ascanSale', [appName,activityName,transaction_amount,transaction_type,command_identifier,invoice_number]);
             });
         },
      ascanVoid: function (appName,activityName,inv_number,command_identifier) {
                return new Promise(function (resolve, reject) {
                  exec(resolve, reject, PLUGIN_NAME, 'ascanVoid', [appName,activityName,inv_number,command_identifier]);
              });
          },
      ascanRefund: function (appName,activityName,transaction_amount,command_identifier) {
                 return new Promise(function (resolve, reject) {
                  exec(resolve, reject, PLUGIN_NAME, 'ascanRefund', [appName,activityName,transaction_amount,command_identifier]);
                });
          },
      ascanSettlement: function (appName,activityName,transaction_type,command_identifier) {
                  return new Promise(function (resolve, reject) {
                    exec(resolve, reject, PLUGIN_NAME, 'ascanSettlement', [appName,activityName,transaction_type,command_identifier]);
                    });
              },
};

module.exports = ChinaBankPos;
