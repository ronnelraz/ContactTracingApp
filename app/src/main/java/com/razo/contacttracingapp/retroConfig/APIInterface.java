package com.razo.contacttracingapp.retroConfig;

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


    @FormUrlEncoded
    @POST("report_temp")
    Call<Object> Temp(@Field("start") String start,
                      @Field("end") String end,
                      @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("report_temp_m")
    Call<Object> Temp_monthly(@Field("start") String start,
                      @Field("end") String end,
                      @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("report_person")
    Call<Object> report_person(@Field("start") String start,
                      @Field("end") String end,
                      @Field("plantcode") String plantcode);


    @FormUrlEncoded
    @POST("report_person_m")
    Call<Object> report_person_monthly(@Field("start") String start,
                               @Field("end") String end,
                               @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("report_weekly_person_entrance")
    Call<Object> report_weekly_person_entrance(@Field("to") String start,
                               @Field("from") String end,
                               @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("Daily_people_entrance_province")
    Call<Object> Daily_people_entrance_province(@Field("to") String start,
                                               @Field("from") String end,
                                               @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("entranceReports")
    Call<Object> entranceReports(@Field("start") String start,
                                                @Field("end") String end,
                                                @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("AgencyReports")
    Call<Object> AgencyReports(@Field("start") String start,
                                 @Field("end") String end,
                                 @Field("plantcode") String plantcode);


    @FormUrlEncoded
    @POST("month_entrance_group")
    Call<Object> month_entrance_group(@Field("start") String start,
                               @Field("end") String end,
                               @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("month_entrance_agency")
    Call<Object> month_entrance_agency(@Field("start") String start,
                                      @Field("end") String end,
                                      @Field("plantcode") String plantcode);



    @FormUrlEncoded
    @POST("monthly_person_entrance")
    Call<Object> monthly_person_entrance(@Field("plantcode") String plantcode);


    @FormUrlEncoded
    @POST("Monthly_people_entrance_province_report")
    Call<Object> Monthly_people_entrance_province_report(@Field("to") String start,
                                                @Field("from") String end,
                                                @Field("plantcode") String plantcode);

    @FormUrlEncoded
    @POST("received_vitamins")
    Call<Object> received_vitamins(@Field("con") String con,@Field("qty") String qty,@Field("user") String user);


    @FormUrlEncoded
    @POST("received_vitamins_empo")
    Call<Object> received_vitamins_empo(@Field("to") String to);


    @FormUrlEncoded
    @POST("vitamins_total_received")
    Call<Object> vitamins_total_received(@Field("to") String to);


}
