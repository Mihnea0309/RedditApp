package com.example.redditapp;

import com.example.redditapp.Account.AccessTokenResponse;
import com.example.redditapp.Account.CheckLogin;
import com.example.redditapp.Comments.CheckComment;
import com.example.redditapp.model.Feed;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FeedAPI {
    String BASE_URL = "https://www.reddit.com/r/";
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

    @POST("{user}")
    Call<CheckLogin> signIn(
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );

    @POST("{comment}")
    Call<CheckComment> submitComment(
            @HeaderMap Map<String, String> headers,
            //@Path("comment") String comment,
            //@Query("parent") String parent,
            @Query("thing_id") String parent,
            @Query("text") String text
    );

    @FormUrlEncoded
    @POST("access_token")
    Call<AccessTokenResponse> getAccessToken(
            @HeaderMap Map<String, String> headers,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri
           // @Field("client_id") String clientId
    );

    @FormUrlEncoded
    @Headers("Authorization: Basic {Base64(client_id:client_secret)}") // Add your credentials encoded in Base64
    @POST("access_token")
    Call<AccessTokenResponse> getAccessToken2(
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri
    );

}
