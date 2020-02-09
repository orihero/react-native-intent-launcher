package com.poberwong.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by poberwong on 16/6/30.
 */
public class IntentLauncherModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final int REQUEST_CODE = 100;
    private static final String ATTR_ACTION = "action";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_CATEGORY = "category";
    private static final String TAG_EXTRA = "extra";
    private static final String ATTR_DATA = "data";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_CLASS_NAME = "className";
    Promise promise;
    ReactApplicationContext reactContext;

    final String E_IMZO_APP = "uz.yt.eimzo";
    final String E_IMZO_ACTIVITY = "uz.yt.eimzo.activity.MainActivity";

    final int RESULT_ERROR = 9;
    final int RESULT_ACCESS_DENIED = 10;

    public final String EXTRA_PARAM_ATTACH_TST = "tst";
    public final String APP_KEY = "86E2F10BA6CD237ADA76579102E1FD147561C390055B062FE5AC49957B1D1A54A266EF04A0E3C9AF6DFD65104E78B08524FF3FA769FDAB47C49DFEC1021A77D4";
    public final String EXTRA_PARAM_ATTACH_PKCS7 = "attach_pkcs7";
    public final String EXTRA_PARAM_ATTACH_SERIAL_NUMBER = "attach_serial_number";
    public final String EXTRA_PARAM_SERIAL_NUMBER = "serial_number";
    public final String EXTRA_PARAM_APPEND_PKCS7 = "append_pkcs7";
    public final String EXTRA_PARAM_MESSAGE = "message";
    public final String EXTRA_PARAM_API_KEY = "api_key";
    public final String EXTRA_RESULT_PKCS7 = "pkcs7";
    public final String EXTRA_RESULT_SERIAL_NUMBER = "serial_number";
    public final String EXTRA_RESULT_SUBJECT_NAME = "subject_name";
    public final String EXTRA_RESULT_SIGNATURE = "signature";
    public final String EXTRA_RESULT_ERROR_CLASS = "error_class";
    public final String EXTRA_RESULT_ERROR_MESSAGE = "error_message";

    String base;
    String regex = ",?(\\\\s)*([A-Za-z]+|[0-9\\\\.]+)=([^=,\\\\]*),?(\\\\s)*";
    Pattern p;

    private static final String FIO = "CN";
    private static final String YUR_TIN = "1.2.860.3.16.1.1";
    private static final String FIZ_TIN = "UID";
    private static final String FORENAME = "Name";
    private static final String SURNAME = "SURNAME";
    private static final String AREA = "L";
    private static final String REGION = "ST";
    private static final String COUNTRY = "C";
    private static final String PINFL = "1.2.860.3.16.1.2";
    private static final String EMAIL = "E";
    private static final String JOBTITLE = "T";
    private static final String ORGANIZATION = "O";
    private static final String DEPARTMENT = "OU";

    public IntentLauncherModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "IntentLauncher";
    }

    /**
     * 选用方案
     * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * getReactApplicationContext().startActivity(intent);
     */
    @ReactMethod
    public void startActivity(ReadableMap params, final Promise promise) {
        this.promise = promise;
        Intent intent = new Intent();

        intent.setClassName(E_IMZO_APP, E_IMZO_ACTIVITY);
        if(params.hasKey(EXTRA_PARAM_API_KEY)){
            intent.putExtra(EXTRA_PARAM_API_KEY,params.getString(EXTRA_PARAM_API_KEY))
        }
        if(params.hasKey(EXTRA_PARAM_SERIAL_NUMBER)){
            intent.putExtra(EXTRA_PARAM_SERIAL_NUMBER,params.getString(EXTRA_PARAM_SERIAL_NUMBER))
        }
        if(params.hasKey(EXTRA_PARAM_MESSAGE)){
            intent.putExtra(EXTRA_PARAM_API_KEY,params.getString(EXTRA_PARAM_MESSAGE).getBytes());
        }
        
        getReactApplicationContext().startActivityForResult(intent, REQUEST_CODE, null); 
    }

    @ReactMethod
    public void isAppInstalled(String packageName, final Promise promise) {
        try {
            this.reactContext.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            promise.reject("app not found");
            return;
        }
        promise.resolve(true);
    }

    @ReactMethod
    public void startAppByPackageName(String packageName, final Promise promise) {
        if (packageName != null) {
            Intent launchIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                getReactApplicationContext().startActivity(launchIntent);
                promise.resolve(true);
                return;
            } else {
                promise.reject("could not start app");
                return;
            }
        }
        promise.reject("package name missing");
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        WritableMap params = Arguments.createMap();
        if (intent != null) {
            params.putInt("resultCode", resultCode);

            Uri data = intent.getData();
            if (data != null) {
                params.putString("data", data.toString());
            }

            Bundle extras = intent.getExtras();
            if (extras != null) {
                params.putMap("extra", Arguments.fromBundle(extras));
            }
        }

        this.promise.resolve(params);
    }
}
