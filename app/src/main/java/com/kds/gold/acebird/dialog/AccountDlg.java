package com.kds.gold.acebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.models.LoginModel;

/**
 * Created by RST on 7/27/2017.
 */

public class AccountDlg extends Dialog {
    private LoginModel loginModel = (LoginModel) MyApp.instance.getPreference().get(Constants.LOGIN_INFO);
    public AccountDlg(@NonNull Context context, final DialogMacListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_account);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_mac = (TextView)findViewById(R.id.txt_mac);
        txt_mac.setText(loginModel.getMac_address());
        TextView txt_expired = findViewById(R.id.txt_expired);
        txt_expired.setText((String ) MyApp.instance.getPreference().get(Constants.PROFILE));

    }

    public interface DialogMacListener {
        public void OnYesClick(Dialog dialog);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
            dismiss();
        }
        return super.dispatchKeyEvent(event);
    }
}
