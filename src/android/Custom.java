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

/* -- Blue Bamboo Custom Code -- */ 
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
/* -- End Blue Bamboo Custom Code -- */

public class Custom extends CordovaPlugin {

    /* -- Blue Bamboo Custom Code -- */
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /* -- End Blue Bamboo Custom Code -- */

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("print".equals(action)){
                String content    = args.optString(0, "<html></html>");
                String mac_address = args.optString(1);
                this.blueBambooPrint(content, mac_address);
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

    /* -- Blue Bamboo Custom Code -- */
    private void blueBambooPrint(String content, String mac_address) 
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBTState();

        BluetoothDevice device = btAdapter.getRemoteDevice(mac_address);
        try 
        {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } 
        catch (IOException e) {}

        btAdapter.cancelDiscovery();

        try 
        {
            btSocket.connect();
        } 
        catch (IOException e) {
            try 
            {
                btSocket.close();
            } 
            catch (IOException e2) {}
        }

        try 
        {
            outStream = btSocket.getOutputStream();
        } 
        catch (IOException e) {}

        //String message = "Hello from Android.\n";
        byte[] msgBuffer = content.getBytes();
        try 
        {
            outStream.write(msgBuffer);
        } 
        catch (IOException e) {}
    }

    private void CheckBTState() 
    {
        if(btAdapter!=null) 
        {
            if (!btAdapter.isEnabled()) 
            {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                cordova.startActivityForResult(this, enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
    /* -- End Blue Bamboo Custom Code -- */
}
