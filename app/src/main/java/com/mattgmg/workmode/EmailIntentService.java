package com.mattgmg.workmode;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EmailIntentService extends IntentService {
    private static final String TAG = "EmailIntentService";

    private static final String ACTION_SEND_EMAIL = "com.mattgmg.workmode.action.SEND_EMAIL";

    private static class Credentials {
        String username;
        String password;

        public Credentials(String json) {
            try {
                JSONObject credObj = new JSONObject(json);
                username = credObj.getString("username");
                password = credObj.getString("password");
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse credentials file!", e);
            }
        }
    }

    private Credentials getCredentials(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("credentials.json");
            final char[] buffer = new char[16];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return new Credentials(out.toString());
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to read credentials file!", e);
        }
        catch (IOException e) {
            Log.e(TAG, "Unable to read credentials file!", e);
        }
        return null;
    }

    /**
     * Starts this service to perform action SEND_EMAIL with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSendEmail(Context context) {
        Intent intent = new Intent(context, EmailIntentService.class);
        intent.setAction(ACTION_SEND_EMAIL);
        context.startService(intent);
    }

    public EmailIntentService() {
        super("EmailIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_EMAIL.equals(action)) {
                handleActionSendEmail();
            }
        }
    }

    /**
     * Handle action SEND_EMAIL in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendEmail() {
        Credentials creds = getCredentials(this);
        if (creds != null) {
            GMailSender sender = new GMailSender(creds.username, creds.password);
            try {
                sender.sendMail("HEY I GOT TO WORK!", "This was triggered by my CyanogenMod work "
                                                      + "profile! Just making sure you knew :)",
                                "Matt Garnes", "matt@mattgarnes.com");
            } catch (Exception e) {
                Log.e(TAG, "Failure sending email. Check credentials.", e);
            }
        } else {
            Log.e(TAG, "Credentials was null, could not send email. Did you forget to include the"
                       + " file? Or is it malformed?");
        }
    }
}
