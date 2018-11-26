package com.sciasv.asv.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sciasv.asv.R;
import com.sciasv.asv.handlers.JSONHandler;
import com.sciasv.asv.models.ProfileHolder;
import com.sciasv.asv.network.Connect;
import com.sciasv.asv.utils.ResponseHandler;
import com.sciasv.asv.utils.Utils;

public class LogIn extends AppCompatActivity {

    EditText eUsername, ePassword;
    ResponseHandler responseHandler;
    ProfileHolder profileHolder;
    Context context;

    @Override
    protected void onResume() {
        super.onResume();
        context = this;
        responseHandler = new ResponseHandler(this);

        profileHolder = new ProfileHolder(this);
        if (profileHolder.userHasLoggedIn()) {
            startActivity(new Intent(this, Home.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        eUsername = findViewById(R.id.userName);
        ePassword = findViewById(R.id.password);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

    }


    private void logIn() {

        if (Utils.isEmpty(eUsername)) {
            responseHandler.showToast("Username is required");
            return;
        }


        if (Utils.isEmpty(ePassword)) {
            responseHandler.showToast("Password is required");
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidNetworking.post(Connect.url + Connect.login)
                .addBodyParameter("username", Utils.getText(eUsername))
                .addBodyParameter("password", Utils.getText(ePassword))
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        JSONHandler jsonHandler = new JSONHandler(context);

                        jsonHandler.login(response);

                    }

                    @Override
                    public void onError(ANError anError) {

                        progressDialog.dismiss();

                        // handle error
                        Log.d(Connect.tag, anError.toString());
                        responseHandler.showToast("We encountered an error");

                    }
                });


    }

}
