package com.example.redditapp.Account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redditapp.FeedAPI;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthActivity extends AppCompatActivity {

    private static final String TAG = "OAuthActivity";
    private static final String REDIRECT_URI = "retrofitreddit://redirect"; // Change this to your app's redirect URI
    private static final String CLIENT_ID = "tZ9l4NMNuuCBgWsLHor2AQ";
    private static final String CLIENT_SECRET = "";
    private static final String AUTH_URL = "https://www.reddit.com/api/v1/authorize";
    private static final String TOKEN_URL = "https://www.reddit.com/api/v1/";
    public AccessTokenResponse ACCESS_TOKEN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String authUrl = AUTH_URL +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&state=random_state" +
                "&redirect_uri=" + REDIRECT_URI +
                "&duration=permanent" +
                "&scope=identity submit read";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        startActivity(intent);
        Log.d(TAG, "Auth URL: " + authUrl);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");

            if (code != null) {
                Log.d(TAG, "Authorization code: " + code);

                exchangeCodeForToken(code);
            } else if (uri.getQueryParameter("error") != null) {
                Log.e(TAG, "Error: " + uri.getQueryParameter("error"));
            }
        }
    }

    private void exchangeCodeForToken(String code) {
        // Retrofit setup to request access token
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI redditOAuthAPI = retrofit.create(FeedAPI.class);

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

//        Call<AccessTokenResponse> call = redditOAuthAPI.getAccessToken2(
//                "authorization_code",
//                code,
//                REDIRECT_URI
//        );

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", credentials);
        //headers.put("Content-Type", "application/x-www-form-urlencoded");

        Call<AccessTokenResponse> call = redditOAuthAPI.getAccessToken(
                headers,
                "authorization_code",
                code,
                "retrofitreddit://redirect"
                //CLIENT_ID
        );

        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful()) {
                    AccessTokenResponse accessTokenResponse = response.body();
                    ACCESS_TOKEN.setAccess_token(String.valueOf(accessTokenResponse));
                    Log.d(TAG, "Access Token: " + accessTokenResponse.getAccess_token());
                    Log.d(TAG, "Refresh Token: " + accessTokenResponse.getRefresh_token());

                } else {
                    Log.e(TAG, "Access token request failed. Response: " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.e(TAG, "Failed to request access token", t);
            }
        });
    }
}
