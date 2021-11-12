package com.example.contacttracingapp.retroConfig;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {
    @FormUrlEncoded
    @POST("login")
    Call<Object> LOGIN(@Field("username") String username, @Field("password") String password);

    @POST("loginAreaLocation")
    Call<Object> AREAS();

    @POST("apptemp")
    Call<Object> ScannedPerson();

    @FormUrlEncoded
    @POST("vaccinatedV1")
    Call<Object> VACCINE(@Field("contact") String contact,@Field("name") String name,@Field("lname") String lname);

    @FormUrlEncoded
    @POST("appscantemp")
    Call<Object> _appscantemp(@Field("name") String name,
                              @Field("lname") String lname,
                              @Field("AD") String AD,
                              @Field("contact") String contact,
                              @Field("temp") String temp,
                              @Field("vaccine") String vaccine);

    @FormUrlEncoded
    @POST("appscann_new")
    Call<Object> con_register(@Field("type") String type,
                              @Field("cn") String cn,
                              @Field("empid") String empid,
                              @Field("plate") String plate,
                              @Field("name") String name,
                              @Field("lname") String lname,
                              @Field("gender") String gender,
                              @Field("address") String address,
                              @Field("dob") String dob,
                              @Field("age") String age,
                              @Field("contact") String contact,
                              @Field("AD") String AD,
                              @Field("pro") String pro,
                              @Field("mun") String mun,
                              @Field("brgy") String brgy,
                              @Field("img") String img,
                              @Field("plantcode") String plantcode,
                              @Field("vaccine") String vaccine);


    @POST("Province")
    Call<Object> province();

    @FormUrlEncoded
    @POST("city")
    Call<Object> city(@Field("id") String id);

    @FormUrlEncoded
    @POST("brgy")
    Call<Object> brgy(@Field("id") String id);

    @POST("trucking")
    Call<Object> trucking();

    @POST("agency")
    Call<Object> agency();

    @POST("getAll")
    Call<Object> all_registered();

    @FormUrlEncoded
    @POST("scannedID")
    Call<Object> ScannedID(@Field("contact") String contact,@Field("AD") String AD);

    @POST("BanType")
    Call<Object> GetBanTypes();

    @FormUrlEncoded
    @POST("BanPerson")
    Call<Object> Ban(@Field("id") String id,
                     @Field("start") String start,
                     @Field("end") String end,
                     @Field("type") String type,
                     @Field("remark") String remark,
                     @Field("AD") String AD);

    @POST("Blocklisted")
    Call<Object> Blocklisted();


    @FormUrlEncoded
    @POST("update_banned")
    Call<Object> UpdateBanned(@Field("id") String id,
                              @Field("start") String start,
                              @Field("end") String end,
                              @Field("type") String type,
                              @Field("remark") String remark,
                              @Field("AD") String AD);

    @POST("employees")
    Call<Object> Employees();

}
