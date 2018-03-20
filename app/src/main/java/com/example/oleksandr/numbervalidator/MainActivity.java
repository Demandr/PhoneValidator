package com.example.oleksandr.numbervalidator;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText mTextNumber;
    private  Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://bank4u.pp.ua/rest/api/")
                .build();

        findViewById(R.id.button).setOnClickListener(v -> sendRequest());

        mTextNumber = findViewById(R.id.number);
        mTextNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendRequest();
            }
            return true;
        });
    }

    private void sendRequest(){
        if (mTextNumber.getText().length() < 10 || mTextNumber.getText().length() > 13) {
            mTextNumber.setError("Wrong number");
            if (mTextNumber.getText().length() < 10) {
                showToast("Number is short");
            } else {
                showToast("Number is long");
            }
        } else {

            if (isNetworkAvailable()) {
                PhoneService phoneService = retrofit.create(PhoneService.class);
                final Call<PhoneResponse> request = phoneService.getNumber(formatText(mTextNumber.getText().toString()));
                request.enqueue(new Callback<PhoneResponse>() {
                    @Override
                    public void onResponse(Call<PhoneResponse> call, Response<PhoneResponse> response) {
                        Log.d("asd", "onResponse: " + response.toString());
                        if (response.code() == 200) {
                            showAlert("Code equal 200");
                        } else {
                            showAlert("Code does not equal 200");
                        }
                    }

                    @Override
                    public void onFailure(Call<PhoneResponse> call, Throwable t) {
                        showAlert("Request failure");
                    }
                });
            } else {
                showAlert("Network is not available");
            }

        }
    }

    private void showToast(String text) {
        Toast toast = Toast.makeText(this,
                text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private String formatText(String text) {
        if (text.charAt(0) == '+') text = text.substring(1);
        if (text.length() == 13) text = text.substring(3);
        if (text.length() == 12) text = text.substring(2);
        if (text.length() == 11) text = text.substring(1);

        return "380" + text;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showAlert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Code")
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {})
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
