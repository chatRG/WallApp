package com.wallapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.wallapp.R;

/**
 * Created by chatRG.
 */

public class CommonUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void checkNetworkDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.no_network_connection)
                .setMessage(R.string.no_network_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                })
                .setCancelable(false)
                .setIcon(R.drawable.ic_error)
                .show();
    }

    public static void showSnack(Context context, View contentView, int text) {
        TSnackbar snackbar = TSnackbar
                .make(contentView, text, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(R.drawable.ic_intro, 48);
        snackbar.setIconPadding(8);
        snackbar.setMaxWidth(3000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snack_back));
        TextView textView = (TextView)
                snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary2));
        snackbar.show();
    }
}
