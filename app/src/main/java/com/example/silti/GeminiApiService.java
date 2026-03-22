package com.example.silti;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiService {

    private static final String TAG = "GeminiApi";
    private static final String API_KEY = "AIzaSyCIMBMAQaUDR72y8VS_3Zu9yawcqouf51s";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private Handler mainHandler;

    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public GeminiApiService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void sendMessage(String message, GeminiCallback callback) {
        try {
            String jsonRequest = buildJsonRequest(message);
            RequestBody body = RequestBody.create(jsonRequest, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network error: " + e.getMessage());
                    mainHandler.post(() -> callback.onError("خطأ في الاتصال بالإنترنت"));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();

                    if (response.isSuccessful()) {
                        String result = parseGeminiResponse(responseBody);
                        mainHandler.post(() -> callback.onSuccess(result));
                    } else {
                        mainHandler.post(() -> callback.onError("خطأ في الخادم: " + response.code()));
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
            callback.onError("خطأ في إنشاء الطلب");
        }
    }

    private String buildJsonRequest(String message) throws JSONException {
        JSONObject request = new JSONObject();

        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();

        part.put("text", message);
        parts.put(part);
        content.put("parts", parts);
        contents.put(content);

        request.put("contents", contents);

        // إعدادات التوليد
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.9);
        generationConfig.put("maxOutputTokens", 800);
        generationConfig.put("topP", 0.95);
        generationConfig.put("topK", 40);
        request.put("generationConfig", generationConfig);

        return request.toString();
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray candidates = response.getJSONArray("candidates");

            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject content = candidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");

                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
            return "عذراً، لم أتمكن من فهم الطلب";

        } catch (JSONException e) {
            Log.e(TAG, "Parse error: " + e.getMessage());
            return "خطأ في تحليل الرد";
        }
    }
}
