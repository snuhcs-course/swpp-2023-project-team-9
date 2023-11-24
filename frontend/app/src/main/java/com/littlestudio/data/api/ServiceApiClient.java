package com.littlestudio.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

public class ServiceApiClient {

    private static volatile ServiceApi instance;
    private final static String BASE_URL = "http://ec2-3-35-208-114.ap-northeast-2.compute.amazonaws.com:3000";


    public static ServiceApi getInstance() {
        if (instance == null) {
            synchronized (ServiceApiClient.class) {
                if (instance == null) {
                    instance = new ServiceApiClient().ServiceApi();
                }
            }
        }
        return instance;
    }

    private ServiceApi ServiceApi() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build()
                .create(ServiceApi.class);
    }
}
