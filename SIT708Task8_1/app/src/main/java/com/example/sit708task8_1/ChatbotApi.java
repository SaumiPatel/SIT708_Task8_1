package com.example.sit708task8_1;

import android.os.AsyncTask;
import okhttp3.*;
import org.json.JSONObject;

public class ChatbotApi {
    // Use your OpenRouter API key here
    private static final String API_KEY = "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";
    private static final String ENDPOINT_URL = "https://openrouter.ai/api/v1/chat/completions";

    public interface Callback {
        void onResponse(String response);
    }

    public static void sendMessage(String message, String username, Callback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("model", "meta-llama/llama-3.3-8b-instruct:free");
                    json.put("messages", new org.json.JSONArray()
                        .put(new JSONObject()
                            .put("role", "user")
                            .put("content", message)
                        )
                    );
                    RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
                    Request request = new Request.Builder()
                            .url(ENDPOINT_URL)
                            .addHeader("Authorization", "Bearer " + API_KEY)
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject respJson = new JSONObject(response.body().string());
                        if (respJson.has("choices")) {
                            JSONObject firstChoice = respJson.getJSONArray("choices").getJSONObject(0);
                            JSONObject messageObj = firstChoice.getJSONObject("message");
                            return messageObj.optString("content", "");
                        }
                    } else if (response.body() != null) {
                        return response.body().string();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
                return "Sorry, I couldn't get a response.";
            }

            @Override
            protected void onPostExecute(String result) {
                callback.onResponse(result);
            }
        }.execute();
    }
} 