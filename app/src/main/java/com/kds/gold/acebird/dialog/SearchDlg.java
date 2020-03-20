package com.kds.gold.acebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.MyApp;

public class SearchDlg extends Dialog implements View.OnClickListener{
    Context context;
    DlgPinListener listener;
    Button btn_ok,btn_cancel;
    EditText txt_pin;
    String pin_code;
    public SearchDlg(@NonNull Context context, final DlgPinListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        txt_pin = (EditText)findViewById(R.id.txt_pin);
        txt_pin.requestFocus();
        txt_pin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    txt_pin.setBackground(getContext().getResources().getDrawable(R.drawable.input_act));
                }else {
                    txt_pin.setBackground(getContext().getResources().getDrawable(R.drawable.input));
                }

            }
        });
        txt_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                pin_code = txt_pin.getText().toString();
                if(pin_code.length()>2){
                    MyApp.instance.is_after = true;
                    listener.OnYesClick(SearchDlg.this,pin_code);
                }
            }
        });
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                MyApp.instance.is_after = false;
                pin_code = txt_pin.getText().toString();
                listener.OnYesClick(SearchDlg.this,pin_code);
                break;
            case R.id.btn_cancel:
                listener.OnCancelClick(SearchDlg.this,"");
                break;
        }
    }

    public interface DlgPinListener {
        public void OnYesClick(Dialog dialog, String pin_code);
        public void OnCancelClick(Dialog dialog, String pin_code);
    }
}
