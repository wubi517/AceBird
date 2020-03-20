package com.kds.gold.acebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.models.LoginModel;

public class ChangePortalDlg extends Dialog {
    private String old_portal;
    private String new_portal;
    private String old_mac;
    private String new_mac;

    public ChangePortalDlg(@NonNull Context context, final ChangePortalDlg.DialogUpdateListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_portal);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView txt_old = findViewById(R.id.txt_old_pass);
        final EditText txt_new = findViewById(R.id.txt_new_pass);
        final TextView txt_old_mac = findViewById(R.id.txt_old_mac);
        final EditText txt_new_mac = findViewById(R.id.txt_new_mac);

        old_portal = (String) MyApp.instance.getPreference().get(Constants.APP_DOMAIN);
        txt_old.setText(old_portal);
        LoginModel loginModel = (LoginModel) MyApp.instance.getPreference().get(Constants.LOGIN_INFO);
        old_mac = loginModel.getMac_address().replace(";","");
        txt_old_mac.setText(old_mac);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnUpdateSkipClick(ChangePortalDlg.this,"","");
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_portal = txt_new.getText().toString();
                if(!new_portal.isEmpty()){
                    if(new_portal.endsWith("c")||new_portal.endsWith("c/")){
                        new_portal = new_portal.substring(0,new_portal.lastIndexOf("c"));
                    }
//                    if(!new_portal.endsWith("/")){
//                        new_portal = new_portal+"/";
//                    }
                }

                new_mac = txt_new_mac.getText().toString();
                if(!new_mac.isEmpty()){
                    if(!new_mac.endsWith(";")){
                        new_mac = new_mac.replace(";",":");
                    }
                    if(new_mac.contains(";")){
                        new_mac = new_mac.replace(";","");
                    }
                }else {
                    new_mac = old_mac;
                }
                listener.OnUpdateNowClick(ChangePortalDlg.this,new_portal,new_mac);
            }
        });
    }

    public interface DialogUpdateListener {
        public void OnUpdateNowClick(Dialog dialog, String  new_portal,String new_mac);
        public void OnUpdateSkipClick(Dialog dialog, String new_portal,String new_mac);
    }
}
