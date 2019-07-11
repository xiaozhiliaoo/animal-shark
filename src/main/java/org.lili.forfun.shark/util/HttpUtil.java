package org.lili.forfun.shark.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public final class HttpUtil {

    private HttpUtil() {
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String HTTP_PREFIX = "http://";

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();


    public static String post(String url, Map<String, String> params) {

        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        return doRequest(request);
    }

    public static String postPayload(String url, String payload) {
        return postWithHeader(url, null, payload);
    }

    public static String postWithHeader(String url, Map<String, String> headerMap, Map<String, String> paramMap,
                                        String payload) {
        FormBody.Builder builder = new FormBody.Builder();
        paramMap.forEach(builder::add);
        RequestBody body = builder.build();
        return post(url, headerMap, body);
    }

    private static String postWithHeader(String url, Map<String, String> headerMap, String payload) {
        RequestBody body = RequestBody.create(JSON, payload == null ? "" : payload);
        return post(url, headerMap, body);
    }

    private static String post(String url, Map<String, String> headerMap, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (headerMap != null) {
            headerMap.forEach(builder::header);
        }
        Request request = builder.build();
        return doRequest(request);
    }


    public static String get(String url, Map<String, String> params) {
        String newUrl = makeGetStyleUrl(url, params);
        return get(newUrl);
    }

    public static String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return doRequest(request);
    }

    private static String makeGetStyleUrl(String originalUrl, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (!originalUrl.startsWith(HTTP_PREFIX)) {
            sb.append(HTTP_PREFIX).append(originalUrl).append("?");
        } else {
            sb.append(originalUrl).append("?");
        }
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static String doRequest(Request request) {
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                return body == null ? "" : body.string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
