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
    private BluetoothDevice btDevice = null;
    private String printContent = "";
    private String printMacAddress = "";
    private String printConnect = "";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /* -- End Blue Bamboo Custom Code -- */

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("print".equals(action)){
                printContent    = args.optString(0);
                printMacAddress = args.optString(1);
                printConnect    = args.optString(2);

                //if("true".equals(printConnect))
                //{
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if(CheckBTState()){
                        this.blueBambooConnect();
                    }
                //}
                else
                {
                    this.blueBambooPrint();
                }
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            this.blueBambooConnect();
        }
    }

    private void blueBambooConnect()
    {
        btDevice = btAdapter.getRemoteDevice(printMacAddress);
        try 
        {
            btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
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
    }

    /* -- Blue Bamboo Custom Code -- */
    private void blueBambooPrint() 
    {
        //String message = "Hello from Android.\n";
        byte[] msgBuffer = printContent.getBytes();
        try 
        {
            outStream.write(msgBuffer);

            try 
            {
                outStream.flush();
            }
            catch (IOException e) {}
        } 
        catch (IOException e) {}
    }

    private boolean CheckBTState() 
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

        return btAdapter.isEnabled();
    }
    /* -- End Blue Bamboo Custom Code -- */
}
