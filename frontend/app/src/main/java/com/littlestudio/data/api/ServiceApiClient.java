package com.littlestudio.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

public class ServiceApiClient {
    private final static String BASE_URL = "http://ec2-54-180-103-195.ap-northeast-2.compute.amazonaws.com:3000";

    private static ServiceApi apiClient = null;

    private ServiceApiClient() {
    }

    public static ServiceApi getServiceApiInstance() {
        if (apiClient == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            apiClient = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .client(httpClient)
                    .build()
                    .create(ServiceApi.class);
        }
        return apiClient;
    }
}
