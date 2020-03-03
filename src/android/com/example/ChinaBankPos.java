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
import org.json.JSONObject;
import java.io.Serializable;
import java.text.DecimalFormat;


import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.database.Cursor;
import android.content.ContentResolver;
import android.content.ComponentName;
import android.util.Log;
import android.app.ActivityManager;
import android.content.Context;
import java.util.List;
import android.net.Uri;
import android.app.ActivityManager.RunningServiceInfo;

public class ChinaBankPos extends CordovaPlugin {
  //cordova-china-bank-pos-bluetooth
    private static final String TAG = "ChinaBankPos";
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;

    public static final int SALE = 1;
    public static final int REFUND = 2;
    public static final int LOGIN = 3;
    public static final int ASCAN=4;

    private final DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00");

    CallbackContext callbackContext = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing ChinaBankPos");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("sale")) {
            sale(args);
        }
        if (action.equals("bocUnionPay")) {
            bocUnionPay(args);
        }else if (action.equals("refund")){
            refund(args);
        }else if (action.equals("login")){
            login(args);
        }
        else if (action.equals("checkMpos")){
           checkMpos(this.cordova.getActivity().getApplicationContext());
        }
        else if (action.equals("ascanSale")){
            ascanSale(args);
        }
        else if (action.equals("ascanVoid")){
            ascanVoid(args);
        }
        else if (action.equals("ascanRefund")){
            ascanRefund(args);
        }
        else if (action.equals("ascanSettlement")){
            ascanSettlement(args);
        }
        return true;
    }
    public void checkMpos(Context context) {
        int num = 100;
        String processName = "com.bocs.mpos";
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServiceInfos = activityManager
                .getRunningServices(num);
        for (RunningServiceInfo rsi : runningServiceInfos) {
            if (rsi.process.equals(processName))
                callbackContext.success(1);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://com.bocs.mpos.provider/tb_login");
                Cursor cursor = resolver.query(uri, new String[]{"isLogin"}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int isLogin = cursor.getInt(cursor.getColumnIndex("isLogin"));
                        if (isLogin == 1) {
                            cursor.close();
                          // return true;
                            callbackContext.success(1);
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        callbackContext.success(0);
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

    public void bocUnionPay(JSONArray args) throws JSONException {
        String amount = args.getString(0);
        String transId = args.getString(1);
        String phone = args.getString(2);
        String email = args.getString(3);



        Intent intent_mpos = new Intent();
        intent_mpos.setClassName("com.bocs.mpos", "com.bocs.mpos.activity.SalesActivity");
        Bundle bundle = new Bundle();
        bundle.putString("sTransTypeInd", "15");
        bundle.putString("sAmount", amount);
        bundle.putString("sOrderNo", transId);
        bundle.putString("sPhoneNO", phone);
        bundle.putString("sEmailAddr", email);
        bundle.putString("sCurrencyCode", "702");
        bundle.putString("sRemark", "unionpay");

        intent_mpos.putExtras(bundle);
        this.cordova.startActivityForResult(this,intent_mpos, SALE);
    }

    public void ascanSale(JSONArray args) throws JSONException {
        String app_name = args.getString(0);
        String activity_name = args.getString(1);
        String transaction_amount = args.getString(2);
        String transaction_type = args.getString(3);
        String command_identifier = args.getString(4);
        String invoice_number = args.getString(5);

        Intent launchIntent = new Intent();
        launchIntent.setClassName(app_name, activity_name);
      //  launchIntent.setClassName("sg.com.eftpos.mobilepos.ocbc", "sg.com.mobileeftpos.paymentapplication.ecr.EcrGatewayActivity");
        //launchIntent.setClassName("sg.com.mobileeftpos.paymentapplication", "sg.com.mobileeftpos.paymentapplication.activities.ECRActivity");
        if (launchIntent != null) {
            launchIntent.setFlags(0);
            Bundle bundleApp = new Bundle();
            bundleApp.putString("Request", getSaleObject(transaction_amount,transaction_type,command_identifier));
            launchIntent.putExtras(bundleApp);
            this.cordova.startActivityForResult(this,launchIntent, ASCAN);
          }

    }


    private String getSaleObject(String amount,String transactionType,String commandIdentifier) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("transaction_type", transactionType);
            jsonObject.put("invoice_number", commandIdentifier);
            jsonObject.put("command_identifier",commandIdentifier);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    private String getConvertedAmount(String amount) {

        double l = Double.parseDouble(amount);
        l = l / 100;
        return REAL_FORMATTER.format(l);

    }
    public void ascanVoid(JSONArray args) throws JSONException {
      String app_name = args.getString(0);
      String activity_name = args.getString(1);
        String invoice_number = args.getString(2);
        String command_identifier = args.getString(3);

        Intent launchIntent = new Intent();
        launchIntent.setClassName(app_name, activity_name);
        if (launchIntent != null) {
            launchIntent.setFlags(0);
            Bundle bundleApp = new Bundle();
            bundleApp.putString("Request", getVoidObject(invoice_number,command_identifier));
            launchIntent.putExtras(bundleApp);
            this.cordova.startActivityForResult(this,launchIntent, ASCAN);
          }

    }


    private String getVoidObject(String invNumber,String commandIdentifier) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("invoice_number", invNumber);
            jsonObject.put("transaction_type", "C300");
            jsonObject.put("command_identifier", commandIdentifier);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
    public void ascanRefund(JSONArray args) throws JSONException {
        String app_name = args.getString(0);
        String activity_name = args.getString(1);
        String transaction_amount = args.getString(2);
        String command_identifier = args.getString(3);

        Intent launchIntent = new Intent();
        launchIntent.setClassName(app_name, activity_name);
        if (launchIntent != null) {
            launchIntent.setFlags(0);
            Bundle bundleApp = new Bundle();
            bundleApp.putString("Request", getRefundObject(transaction_amount,command_identifier));
            launchIntent.putExtras(bundleApp);
            this.cordova.startActivityForResult(this,launchIntent, ASCAN);
          }

    }


    private String getRefundObject(String amount,String commandIdentifier) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("transaction_amount", getConvertedAmount(amount));
            jsonObject.put("command_identifier", commandIdentifier);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
    public void ascanSettlement(JSONArray args) throws JSONException {
        String app_name = args.getString(0);
        String activity_name = args.getString(1);
        String transaction_type = args.getString(2);
        String command_identifier = args.getString(3);


        Intent launchIntent = new Intent();
        launchIntent.setClassName(app_name, activity_name);
        if (launchIntent != null) {
            launchIntent.setFlags(0);
            Bundle bundleApp = new Bundle();
            bundleApp.putString("Request", getSettlementObject(transaction_type,command_identifier));
            launchIntent.putExtras(bundleApp);
            this.cordova.startActivityForResult(this,launchIntent, ASCAN);
          }

    }


    private String getSettlementObject(String transactionType,String commandIdentifier) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transaction_type", transactionType);
            jsonObject.put("command_identifier",commandIdentifier);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public void login(JSONArray args) throws JSONException {
        String packageName = args.getString(0);
        String className = args.getString(1);


        Intent intent_mpos = new Intent(Intent.ACTION_MAIN);
        intent_mpos.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName comp=new ComponentName("com.bocs.mpos", "com.bocs.mpos.activity.WelcomeActivity");
        intent_mpos.setComponent(comp);
        Bundle bundle = new Bundle();
        bundle.putString("package", packageName);
        bundle.putString("className", className);
        bundle.putString("modeType", "2");
        bundle.putBoolean("isOnlineSign", true);

        intent_mpos.putExtras(bundle);
        this.cordova.startActivityForResult(this,intent_mpos,LOGIN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ASCAN){
            if (resultCode == RESULT_CANCELED) {
                callbackContext.error("User Canceled");
            }else if (resultCode == RESULT_OK) {
                String responseData;
                responseData = data.getStringExtra("Response");
                callbackContext.success(responseData);
            }

        }else if (requestCode == SALE) {
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
        else if (requestCode == LOGIN) {
            if (resultCode == RESULT_CANCELED) {
                callbackContext.error("User Canceled");
            }
            callbackContext.success();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
