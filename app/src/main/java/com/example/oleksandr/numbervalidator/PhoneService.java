package com.example.oleksandr.numbervalidator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface PhoneService {

    @Headers({
            "version: 18",
            "deviceType: android",
            "language: ru"
    })
    @GET("user/checkMobile/{phoneNumber}")
    Call<PhoneResponse> getNumber(@Path("phoneNumber") String number);

}
