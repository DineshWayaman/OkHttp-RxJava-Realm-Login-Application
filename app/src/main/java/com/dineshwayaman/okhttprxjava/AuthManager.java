package com.dineshwayaman.okhttprxjava;

import android.util.Log;
import android.util.Pair;

import com.dineshwayaman.okhttprxjava.Models.ResponseModel;
import com.dineshwayaman.okhttprxjava.Models.UserData;
import com.google.gson.Gson;

import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthManager {

    private String baseApiURL = "https://dineshwayaman.com/cba/";
    private OkHttpClient okHttpClient;

    Realm realm;
    public AuthManager() {
        okHttpClient = new OkHttpClient();
    }

    public Observable<Boolean> login(String name, String password){
        RequestBody body = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(baseApiURL)
                .post(body)
                .build();

        return Observable.create(emitter -> {
            try {
                // Execute the request synchronously
                Response response = okHttpClient.newCall(request).execute();
                // Check the response and emit the appropriate boolean value
                if (response.isSuccessful()) {




                    JSONObject json = new JSONObject(response.body().string());

                    Log.e("response", json.toString());


                    int res_code = json.getInt("res_code");

                    Log.e("Tag", String.valueOf(res_code));

                    if(res_code == 0){
//                        realm.deleteAll();

                        Log.e("Tag", String.valueOf(res_code));

//                        Json value should pass
                        saveUserDataIntoRealm(json.toString());
                        emitter.onNext(true); // Login successful
                    }else if(res_code == -1){
                        emitter.onNext(false); // Login successful

                    }
                    emitter.onComplete();




                } else {
                    emitter.onNext(false); // Login failed
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });




    }

    private void saveUserDataIntoRealm(String responseData) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(r -> {

                try{

                    Log.e("Tag_Realm_S", responseData);
                    JSONObject json = new JSONObject(responseData);
                    int res_code = json.getInt("res_code");
                    String res_desc = json.getString("res_desc");


                    if(res_code == 0){
                        JSONObject userObject = json.getJSONObject("user_data");


                        Log.e("Tag2", userObject.toString());

                        UserData user = new UserData();
                        user.setId(userObject.getInt("id"));
                        user.setName(userObject.getString("name"));
                        user.setEmail(userObject.getString("email"));
                        user.setDob(userObject.getString("dob"));
                        user.setGender(userObject.getString("gender"));
                        user.setCompany(userObject.getString("company"));
                        user.setPosition(userObject.getString("position"));


                        // Save or update the User object in Realm
                        r.insertOrUpdate(user);
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }


                // Create or update RealmObject with parsed data

            });
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }
}
