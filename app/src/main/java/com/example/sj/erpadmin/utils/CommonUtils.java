package com.example.sj.erpadmin.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sj.erpadmin.R;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jaydeepw on 11/8/16.
 */

public class CommonUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    /**
     * Checks if the parameter phone number is valid.
     *
     * @return
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
        return phoneNumber != null && phoneNumber.length() == 10;
    }

    /**
     * Parses the error response to extract meaningful error message from it.
     * Usually called from {@link retrofit2.Callback#onFailure(Call, Throwable)}
     * In case a message extracted is empty returns a generic message.
     *
     * @param throwable parameter usually received from Retrofit.
     * @return
     */
    @NonNull
    public static String parse(Context context, @Nullable Throwable throwable) {

        // could not determine what the error was, show generic message.
        String message = context.getString(R.string.error_general);

        if (throwable == null) {
            return message;
        }

        message = throwable.getMessage();

        // DebugLog.d("message: " + message);

        if (!isEmpty(message)) {

            // Throwable error message for Android OS version below 5.0 is "Failed to connect"
            // And for 5.0 & above is "Unable to resolve host"
            if (message.contains("Unable to resolve host")
                    || message.contains("Failed to connect")) {
                message = context.getString(R.string.msg_no_internet);
            } else {
                message = context.getString(R.string.error_general);
            }
        } else {
            message = context.getString(R.string.error_general);
        }

        return message;
    }

    /**
     * Checks if the parameter string is null or empty.
     * Space is considered as empty.
     *
     * @param string
     * @return true if null or zero-length.
     */
    public static boolean isEmpty(String string) {
        return TextUtils.isEmpty(string)
                || string.length() == 0
                || string.trim().length() == 0;
    }

    @NonNull
    private static int parse(Response response) {

        int statusCode = -1;
        if (response != null) {
            statusCode = response.code();
        }

        return parse(statusCode);
    }

    /**
     * @param statusCode If -1, "No response" message will be returned.
     * @return String resource ID
     */
    @StringRes
    public static int parse(int statusCode) {

        if (statusCode == -1) {
            return R.string.error_server_no_response;
        }

        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            return R.string.error_server_400bad_request;
        } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return R.string.error_server_401unaunthorized;
        } else if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            return R.string.error_server_403forbidden;
        } else if (statusCode == HttpURLConnection.HTTP_BAD_METHOD) {
            return R.string.error_server_405forbidden;
        } else if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return R.string.error_server_500internal_error;
        } else if (statusCode == HttpURLConnection.HTTP_UNAVAILABLE) {
            return R.string.error_server_503unavailable;
        } else {
            return R.string.error_general;
        }
    }

    /**
     * Util method to hold the current thread execution.
     *
     * @param millis
     */
    public static final void waitTill(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show general prompt. A wrapper method to show a prompt message to
     * the user.
     */
    public static final void showMessage(@Nullable View view, String message) {
        openSnackBar(view, message);
    }

    /**
     * @param view
     * @param msg
     */
    public static void openSnackBar(View view, String msg) {
        try {
            if (view != null) {

                // In RA, theme is not proper so snackbar text is appearing in blue color
                // To show text in white, snackbar textView text color is set to white.
                // After fix of RA theme, we can remove the code of changing snackbar textView color
                // Todo: Fix RA theme then remove code of changing textView color
                Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
                View view1 = snackbar.getView();
                TextView tv = (TextView) view1.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Compose a bitmap from base64 encoded string. This is a thread heavy operation.
     *
     * @param baseSixtyFour
     * @return
     */
    @Nullable
    public static Bitmap getBitMapFromBase64(String baseSixtyFour) {
        byte[] decodedString = Base64.decode(baseSixtyFour, Base64.DEFAULT);
        Bitmap oriBitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return oriBitMap;
    }

    /**
     * Create a rounded shaped bitmap from the passed in parameter bitmap.
     * This is a thread heavy operation.
     *
     * @param scaleBitmapImage
     * @return
     */
    public static Bitmap getRoundedShapeBitmap(Bitmap scaleBitmapImage) {
        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        // Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;

    }

    /**
     * This is a thread heavy operation.
     *
     * @param file
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String orientString = null;
        if (exif != null) {
            orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        }

        int orientation = (orientString != null) ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, options.outWidth, options.outHeight, matrix, true);

        return rotatedBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static void hideSoftKeyboard(Activity activity) {

        if (activity == null) {
            return;
        }


        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface InputDialogInputListener {
        void onInput(String text);
    }

    /**
     * @param inputType One of {@link InputType#TYPE_CLASS_DATETIME}
     * @return
     */
    public static void showInputDialog(Context context, int titleResId, @Nullable String prefilText,
                                       int hint, int inputType,
                                       @NonNull final InputDialogInputListener listener) {
        showInputDialog(context, titleResId, prefilText, hint, inputType, listener, null);
    }

    /**
     * @param context
     * @param titleResId
     * @param prefilText
     * @param hint
     * @param inputType
     * @param listener
     * @param textWatcher
     */
    public static void showInputDialog(Context context, int titleResId, @Nullable String prefilText,
                                       int hint, int inputType,
                                       @NonNull final InputDialogInputListener listener, @Nullable TextWatcher textWatcher) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResId);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.alert_dialog_input, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);

        if (!CommonUtils.isEmpty(prefilText)) {
            input.setText(prefilText);
            input.setSelection(prefilText.length());
        }

        input.setHint(hint);
        input.setInputType(inputType);
        input.setMaxLines(1);
        input.setLines(1);
        if (textWatcher != null) {
            input.addTextChangedListener(textWatcher);
        }

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            if (input.getText() != null) {
                Editable editable = input.getText();
                if (editable != null) {
                    String text = editable.toString();
                    listener.onInput(text);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getFormatedTime(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 min";
        } else if (diff < 59 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " mins";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hrs";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            Calendar calendar = Calendar.getInstance();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(new Date(time));
            if (calendar.get(Calendar.YEAR) != calendar1.get(Calendar.YEAR)) {
                Format formatDate = new SimpleDateFormat("dd MMM yyyy");
                Format formatTime = new SimpleDateFormat("HH:mm aa");
                return "On " + formatDate.format(time) + " at " + formatTime.format(time);
            } else {
                Format formatDate = new SimpleDateFormat("dd MMM");
                Format formatTime = new SimpleDateFormat("HH:mm aa");
                return "On " + formatDate.format(time) + " at " + formatTime.format(time);
            }
        }
    }

    public static String[] concat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] finalArray = new String[aLen + bLen];
        System.arraycopy(a, 0, finalArray, 0, aLen);
        System.arraycopy(b, 0, finalArray, aLen, bLen);
        return finalArray;
    }
}