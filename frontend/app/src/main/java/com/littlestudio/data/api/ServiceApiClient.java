package com.littlestudio.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ServiceApiClient {
    private final static String BASE_URL = "http://ec2-54-180-103-195.ap-northeast-2.compute.amazonaws.com:3000";

    private static ServiceApi apiClient = null;

    private ServiceApiClient() {
    }

    public static ServiceApi getServiceApiInstance() {
        if (apiClient == null) {
            ObjectMapper objectMapper = new ObjectMapper();

            apiClient = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .build()
                    .create(ServiceApi.class);
        }
        return apiClient;
    }
}
