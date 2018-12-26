/**
 */
package com.example;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChinaBankPos extends CordovaPlugin {
    private static final String TAG = "ChinaBankPos";
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;

    public static final int SALE = 1;
    public static final int REFUND = 2;
    CallbackContext callbackContext = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing ChinaBankPos");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("sale")) {
            sale(args);
        }else if (action.equals("refund")){
            refund(args);
        }
        return true;
    }
    public void refund(JSONArray args) throws JSONException {
        String transId = args.getString(0);

        Intent intent_mpos = new Intent();
        intent_mpos.setClassName("com.bocs.mpos", "com.bocs.mpos.activity.TransQueryActivity");

        Bundle bundle = new Bundle();
        bundle.putString("sSysUniqueID", transId);
        bundle.putString("sTransTypeInd", "1");
        bundle.putString("sRemark", "refund");

        intent_mpos.putExtras(bundle);
        this.cordova.startActivityForResult(this,intent_mpos, REFUND);

    }
    public void sale(JSONArray args) throws JSONException {
        String amount = args.getString(0);
        String transId = args.getString(1);
        String phone = args.getString(2);
        String email = args.getString(3);

        Intent intent_mpos = new Intent();
        intent_mpos.setClassName("com.bocs.mpos", "com.bocs.mpos.activity.SalesActivity");
        Bundle bundle = new Bundle();
        bundle.putString("sTransTypeInd", "0");
        bundle.putString("sAmount", amount);
        bundle.putString("sOrderNo", transId);
        bundle.putString("sPhoneNO", phone);
        bundle.putString("sEmailAddr", email);
        bundle.putString("sCurrencyCode", "702");
        bundle.putString("sRemark", "sale");

        intent_mpos.putExtras(bundle);
        this.cordova.startActivityForResult(this,intent_mpos, SALE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        

        if (requestCode == SALE) {
            if (resultCode == RESULT_CANCELED) {
                callbackContext.error("User Canceled");
            } else if (resultCode == RESULT_OK) {
                String response_code = data.getExtras().getString("sResponseCode");
                String sSysUniqueID = data.getExtras().getString("sSysUniqueID");

                if ("SS00".equals(response_code) || "00".equals(response_code) || "0000".equals(response_code)) {
                    //success
                    callbackContext.success(sSysUniqueID);
                } else {
                    //fail
                    callbackContext.error("Transaction Fail");
                }
            }
        }else if (requestCode == REFUND) {
            if (resultCode == RESULT_CANCELED) {
                callbackContext.error("User Canceled");
            } else if (resultCode == RESULT_OK) {
                String response_code = data.getExtras().getString("sResponseCode");

                if ("SS00".equals(response_code) || "00".equals(response_code) || "0000".equals(response_code)) {
                    //success
                    callbackContext.success();
                } else {
                    //fail
                    callbackContext.error("Fail to void");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
