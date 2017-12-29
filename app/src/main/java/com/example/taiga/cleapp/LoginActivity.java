package com.example.taiga.cleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout idEditText;
    TextInputLayout passEditText;

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.first_coordinator_layout);

        idEditText = (TextInputLayout) findViewById(R.id.edit_id);
        passEditText = (TextInputLayout) findViewById(R.id.edit_pass);

        SharedPreferences data = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String id = data.getString("userId", null);
        String pass = data.getString("userPass", null);

        if (id != null && pass != null) {
            Intent loginIntent = getIntent();
            if (!loginIntent.getBooleanExtra("EditMode", false)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                finish();
            }
            else{
                idEditText.getEditText().setText(id);
                passEditText.getEditText().setText(pass);
            }
        }

    }

    public void login(View v) {
        String userId = idEditText.getEditText().getText().toString();
        String userPass = passEditText.getEditText().getText().toString();

        if (emptycheck(userId, userPass)) {
            SharedPreferences data = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString("userId", userId);
            editor.putString("userPass", userPass);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }

    public boolean emptycheck(String userId, String userPass) {
        int cnt = 0;

        if (userId.length() == 0) {
            idEditText.setErrorEnabled(true);
            idEditText.setError(" ");
            cnt++;
        } else {
            idEditText.setErrorEnabled(false);
            idEditText.setError(null);
        }

        if (userPass.length() == 0) {
            passEditText.setErrorEnabled(true);
            passEditText.setError(" ");
            cnt++;
        } else {
            passEditText.setErrorEnabled(false);
            passEditText.setError(null);
        }

        if (cnt != 0) {
            Snackbar.make(coordinatorLayout,"空白があります", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
