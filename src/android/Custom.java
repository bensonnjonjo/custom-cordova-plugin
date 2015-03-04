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

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /* -- End Blue Bamboo Custom Code -- */

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if("connect".equals(action)){
                printMacAddress = args.optString(0);

                if(btAdapter == null)
                    btAdapter = BluetoothAdapter.getDefaultAdapter();

                if(CheckBTState()){
                    this.blueBambooConnect();
                }

                callbackContext.success();
                return true;
            }

            if ("print".equals(action)){
                printContent = args.optString(0);

                this.blueBambooPrint();

                callbackContext.success();
                return true;
            }

            if ("disconnect".equals(action)){
                this.blueBambooDisconnect();

                callbackContext.success();
                return true;
            }

            callbackContext.error("Invalid action --" + action);
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
        if(btDevice == null)
            btDevice = btAdapter.getRemoteDevice(printMacAddress);

        try
        {
            if(btSocket == null)
                btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
        }
        catch (IOException e) {}

        btAdapter.cancelDiscovery();

        try
        {
            if(!btSocket.isConnected())
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
            if(outStream == null)
                outStream = btSocket.getOutputStream();
        }
        catch (IOException e) {}
    }

    private void blueBambooDisconnect()
    {
        //close socket connection
        try
        {
            if(btSocket != null)
                btSocket.close();
        }
        catch (IOException e) {}

        //turn off bluetooth
        if(btAdapter != null)
            btAdapter.disable();
    }

    private void blueBambooPrint()
    {
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
