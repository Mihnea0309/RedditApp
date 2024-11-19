package com.example.redditapp.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redditapp.FeedAPI;
import com.example.redditapp.R;
import com.example.redditapp.URLS;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private URLS urls = new URLS();

    public AccessTokenResponse accessToken;

    private ProgressBar mProgressBar;
    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        mPassword = (EditText) findViewById(R.id.input_password);
        mUsername = (EditText) findViewById(R.id.input_username);
        mProgressBar = (ProgressBar) findViewById(R.id.loginRequestLoadingProgressBar);
        mProgressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to log in.");

                String clientId = "tZ9l4NMNuuCBgWsLHor2AQ";
                String redirectUri = "RetrofitReddit://redirect";
                String responseType = "code";
                String state = "random_state";
                String scope = "identity submit read";

                String authUrl = "https://www.reddit.com/api/v1/authorize?" +
                        "client_id=" + clientId +
                        "&response_type=" + responseType +
                        "&state=" + state +
                        "&redirect_uri=" + redirectUri +
                        "&duration=permanent" +
                        "&scope=" + scope;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                startActivity(browserIntent);
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if(!username.equals("") && !password.equals("")){
                    mProgressBar.setVisibility(View.VISIBLE);
                    login(username, password);

                }
            }
        });
    }
    private void login(final String username, String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedAPI.signIn(headerMap, username, username, password, "json");

        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
                try {

                    Log.d(TAG, "onResponse: Server Response: " + response.toString());

                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();
                    Log.d(TAG, "onResponse: modhash" + modhash);
                    Log.d(TAG, "onResponse: cookie" + cookie);

                    if (!modhash.equals("")) {
                        setSessionParams(username, modhash, cookie);
                        mProgressBar.setVisibility(View.GONE);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onResponse: NullPointerException" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setSessionParams(String username, String modhash, String cookie){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables: \n" +
                "username: " + username + "\n" +
                "modhash: " + modhash + "\n" +
                "cookie: " + cookie + "\n");

        editor.putString("@string/SessionUsername", username);
        editor.commit();
        editor.putString("@string/SessionModhash", modhash);
        editor.commit();
        editor.putString("@string/SessionCookie", cookie);
        editor.commit();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith("RetrofitReddit://redirect")) {
            String code = uri.getQueryParameter("code");
            String state = uri.getQueryParameter("state");
            exchangeAuthCodeForAccessToken(code);
        }
    }

    private void exchangeAuthCodeForAccessToken(String authCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI redditAPI = retrofit.create(FeedAPI.class);

        String clientId = "tZ9l4NMNuuCBgWsLHor2AQ";
        String clientSecret = "";
        String credentials = Base64.encodeToString((clientId + ":").getBytes(), Base64.NO_WRAP);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + credentials);

        // Create POST body
//        Call<AccessTokenResponse> call = redditAPI.getAccessToken(
//                headers,
//                "authorization_code",
//                authCode,
//                "RetrofitReddit://redirect",
//                clientId
//        );

//        call.enqueue(new Callback<AccessTokenResponse>() {
//            @Override
//            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
//                if (response.isSuccessful()) {
//                    AccessTokenResponse token = response.body();
//                    accessToken.setAccess_token(String.valueOf(token));
//                    AccessTokenResponse accessTokenResponse = response.body();
//                    Log.d(TAG, "Access Token: " + accessTokenResponse.getAccess_token());
//                    Log.d(TAG, "Refresh Token: " + accessTokenResponse.getRefresh_token());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
//                Log.e(TAG, "Failed to request access token", t);
//            }
//        });
    }


}
