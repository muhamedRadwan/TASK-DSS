package com.friendsteam_cafehak.app;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsynchronousHttpClient {
    private static final String BASE_URL = "http://localhost:8080/CafePocket/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public AsynchronousHttpClient() {
    }


    public static void get(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {

        client.get(getAbsoluteUrl(paramString), paramRequestParams, paramAsyncHttpResponseHandler);
    }

    private static String getAbsoluteUrl(String paramString) {
        return "http://localhost:8080/CafePocket/" + paramString;
    }

    public static void post(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {

        client.post(getAbsoluteUrl(paramString), paramRequestParams, paramAsyncHttpResponseHandler);
    }
}