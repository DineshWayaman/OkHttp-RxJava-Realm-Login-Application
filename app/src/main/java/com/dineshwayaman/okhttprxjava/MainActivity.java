package com.dineshwayaman.okhttprxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dineshwayaman.okhttprxjava.Models.LoginResult;
import com.dineshwayaman.okhttprxjava.Models.ResponseModel;
import com.dineshwayaman.okhttprxjava.Models.UserData;
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;
    private Disposable disposable;

    private String baseApiURL = "https://dineshwayaman.com/cba/";


    EditText edtName, edtPassword;

    TextView txtLogin;
    Button btnLogin;

    OkHttpClient okHttpClient;

    String userName = "", password = "";

    ResponseModel responseModel;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        innit();

        realm = Realm.getDefaultInstance();

        authManager = new AuthManager();

        btnLogin.setOnClickListener(v -> {

            userName = edtName.getText().toString();
            password = edtPassword.getText().toString();

            if(userName.length() == 0 || password.length() == 0){
                Toast.makeText(this, "User name and password are mandatory", Toast.LENGTH_SHORT).show();
            }else{
                signIn(userName, password);
            }

        });


    }

    private void signIn(String userName, String password) {


        disposable = authManager.login(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onLoginSucess,
                        this::onLoginError
                );




    }

    private void onLoginError(Throwable error) {
        Toast.makeText(this, "Login error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }
    private void onLoginSucess(Boolean success) {
        if (success) {

            RealmResults<UserData> users = realm.where(UserData.class).findAll();
            if (users.size() > 0) {
                UserData loggedInUser = users.first();


                txtLogin.setText(String.valueOf(loggedInUser.getId())+" "+loggedInUser.getName()+" "+loggedInUser.getEmail());
            }
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Login failed.. check your username and password", Toast.LENGTH_SHORT).show();
        }
    }

    private void innit() {
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtName);
        btnLogin = findViewById(R.id.btnLogin);
        txtLogin = findViewById(R.id.textView);

    }

    @Override
    protected void onDestroy() {
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }

        super.onDestroy();
    }
}