package com.kds.gold.acebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.MyApp;

/**
 * Created by RST on 7/27/2017.
 */

public class MacDlg extends Dialog {

    public MacDlg(@NonNull Context context, final DialogMacListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_mac);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_mac = (TextView)findViewById(R.id.txt_mac);
        txt_mac.setText(MyApp.instance.mac_address.toUpperCase());
        findViewById(R.id.dlg_mac_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnYesClick(MacDlg.this);
            }
        });
    }

    public interface DialogMacListener {
        public void OnYesClick(Dialog dialog);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        return super.dispatchKeyEvent(event);
    }
}
