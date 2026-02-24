package com.kiosk.printbridge;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import woyou.aidlservice.jiuiv5.IWoyouService;

/**
 * Headless activity that receives sunmiprint:// intents from Chrome,
 * decodes the receipt JSON, prints via AIDL, and closes immediately.
 *
 * URL format: sunmiprint://receipt?data=<base64-encoded-JSON>
 *
 * JSON fields:
 *   title    - e.g. "Queue Ticket", "eSIM Request"
 *   brand    - e.g. "MTN"
 *   status   - e.g. "Submitted", "Successful"
 *   ticketId - e.g. "TKD-095"
 *   date     - e.g. "24/02/2026, 10:30:00 AM"
 *   message  - optional longer text
 */
public class PrintActivity extends Activity {

    private static final String TAG = "SunmiPrintBridge";
    private IWoyouService printerService;
    private String pendingData;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            printerService = IWoyouService.Stub.asInterface(service);
            if (pendingData != null) {
                printReceipt(pendingData);
                pendingData = null;
            }
            finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            printerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri == null) {
            finish();
            return;
        }

        String base64Data = uri.getQueryParameter("data");
        if (base64Data == null) {
            finish();
            return;
        }

        // Decode the base64 JSON
        try {
            byte[] decoded = Base64.decode(base64Data, Base64.DEFAULT);
            pendingData = new String(decoded, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode data", e);
            finish();
            return;
        }

        // Bind to Sunmi printer AIDL service
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        boolean bound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (!bound) {
            Log.e(TAG, "Failed to bind printer service — is this a Sunmi device?");
            finish();
        }
    }

    private void printReceipt(String jsonString) {
        try {
            JSONObject data = new JSONObject(jsonString);

            printerService.printerInit(null);
            printerService.enterPrinterBuffer(true);

            // ── Header: brand name ──
            printerService.setAlignment(1, null); // center
            printerService.printTextWithFont(
                data.optString("brand", "Bank") + "\n",
                "", 32, null
            );

            // ── Separator ──
            printerService.setAlignment(0, null); // left
            printerService.printText("--------------------------------\n", null);

            // ── Title ──
            printerService.setAlignment(1, null);
            printerService.setFontSize(28, null);
            printerService.printText(
                data.optString("title", "Receipt") + "\n",
                null
            );

            printerService.setAlignment(0, null);
            printerService.printText("--------------------------------\n", null);

            // ── Fields ──
            printerService.setFontSize(24, null);

            if (data.has("status")) {
                printerService.printText("Status: " + data.getString("status") + "\n", null);
            }

            if (data.has("ticketId")) {
                printerService.setFontSize(28, null);
                printerService.printText("Ticket: " + data.getString("ticketId") + "\n", null);
                printerService.setFontSize(24, null);
            }

            if (data.has("date")) {
                printerService.printText("Date: " + data.getString("date") + "\n", null);
            }

            if (data.has("message")) {
                printerService.printText("\n" + data.getString("message") + "\n", null);
            }

            // ── Footer ──
            printerService.printText("--------------------------------\n", null);
            printerService.setAlignment(1, null);
            printerService.printText("Thank you\n", null);

            // Feed paper so receipt clears the cutter
            printerService.lineWrap(4, null);

            printerService.exitPrinterBufferWithCallback(true, null);

        } catch (JSONException | RemoteException e) {
            Log.e(TAG, "Print failed", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(serviceConnection);
        } catch (Exception ignored) {}
    }
}
