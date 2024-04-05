package com.example.weekly;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;

public class Utils {

    static Dialog createDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return dialog;
    }

}
