package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Api {
    public static List<Map<String, String>> fetchPrintDataFromApi(String apiUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer YOUR_TOKEN")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码: " + response.code());
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseBody, new TypeReference<List<Map<String, String>>>() {});
        }
    }
}
