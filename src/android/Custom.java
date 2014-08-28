package de.appplant.cordova.plugin.custom;
 
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Custom extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("print".equals(action)){
                JSONObject arg_object = args.getJSONObject(0);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra("MAC", "00:12:6F:39:CF:00");
                sendIntent.putExtra("DATA", "Test Data\nNew Line");
                sendIntent.setComponent(new ComponentName("com.blueslib.android.app","com.blueslib.android.app.PrintService"));
         
               this.cordova.getActivity().startService(sendIntent);
               callbackContext.success();
               return true;
            }
            callbackContext.error("Invalid action");
            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }
    }
}
