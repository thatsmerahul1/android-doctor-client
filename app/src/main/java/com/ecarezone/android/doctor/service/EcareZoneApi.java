package com.ecarezone.android.doctor.service;

import com.ecarezone.android.doctor.model.rest.AddDoctorRequest;
import com.ecarezone.android.doctor.model.rest.AddDoctorResponse;
import com.ecarezone.android.doctor.model.rest.AppointmentResponse;
import com.ecarezone.android.doctor.model.rest.CreateProfileRequest;
import com.ecarezone.android.doctor.model.rest.CreateProfileResponse;
import com.ecarezone.android.doctor.model.rest.DeleteProfileRequest;
import com.ecarezone.android.doctor.model.rest.ForgetPassRequest;
import com.ecarezone.android.doctor.model.rest.GetNewsResponse;
import com.ecarezone.android.doctor.model.rest.LoginRequest;
import com.ecarezone.android.doctor.model.rest.LoginResponse;
import com.ecarezone.android.doctor.model.rest.PatientAcceptRequest;
import com.ecarezone.android.doctor.model.rest.Repo;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.doctor.model.rest.SettingsRequest;
import com.ecarezone.android.doctor.model.rest.SignupRequest;
import com.ecarezone.android.doctor.model.rest.UpdateProfileRequest;
import com.ecarezone.android.doctor.model.rest.UploadImageResponse;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.model.rest.base.UpdatePasswordRequest;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.RestMethod;
import retrofit.mime.TypedFile;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public interface EcareZoneApi {
    @GET("/users/{user}/repos")
    List<Repo> listRepos(@Path("user") String user);

    @POST("/login")
    LoginResponse login(@Body LoginRequest request);

    @POST("/logout")
    LoginResponse logout(@Body LoginRequest request);

    @POST("/forgot-password")
    LoginResponse forgetPassword(@Body ForgetPassRequest request);

    @POST("/signup")
    LoginResponse signup(@Body SignupRequest request);

    @POST("/changePassword")
    BaseResponse updatePassword(@Body UpdatePasswordRequest request);

    @PUT("/users/{userid}/settings")
    LoginResponse settingsUpdate(@Path("userid") Long userId, @Body SettingsRequest request);

    @POST("/doctors/search")
    SearchDoctorsResponse searchDoctors(@Body SearchDoctorsRequest request);

    @GET("/users/{userId}/doctors")
    SearchDoctorsResponse getDoctors(@Path("userId") Long userId);

    @POST("/users/{userId}/doctors/{doctorId}")
    AddDoctorResponse addDoctor(@Path("userId") Long userId, @Path("doctorId") Long doctorId, @Body AddDoctorRequest request);

    @PUT("/doctors/{doctorid}/profiles/{profileid}")
    CreateProfileResponse updateProfile(@Path("doctorid") Long doctorid, @Path("profileid") Long profileid, @Body UpdateProfileRequest request);

    @POST("/doctors/{doctorid}/profiles")
    CreateProfileResponse createProfile(@Path("doctorid") Long userId, @Body CreateProfileRequest request);

    @DELETE("/users/{userId}/profiles/{profileId}")
    BaseResponse deleteProfile(@Path("userId") Long userId, @Path("profileId") Long profileId, @Body DeleteProfileRequest request);

    @GET("/users/{userId}/news")
    GetNewsResponse getNews(@Path("userId") Long userId);

    @Multipart
    @POST("/doctors/{doctorid}/profilespic")
    UploadImageResponse upload(@Part("profilepic") TypedFile file,
                               @Path("doctorid") Long doctorid);

    @GET ("/users/{doctorid}/patients")
    SearchDoctorsResponse getPatients(@Path("doctorid") Long doctorid);

//    @POST("/patients/{userid}")
//    SearchDoctorsResponse getPatients(@Path("userId") Long userId) ;

    @POST ("/users/{doctorId}/patients/{patientid}")
    SearchDoctorsResponse requestAccept(@Path("doctorId") Long userId, @Path("patientid") Long profileId, @Body PatientAcceptRequest request);

    @GET("/users/{doctorid}/pendingpatients")
    SearchDoctorsResponse getPendingRequest(@Path("doctorid") Long doctorid);

    @GET("/pendingappointments/doctors/{doctorId}")
    AppointmentResponse getAppointmentDate(@Path("doctorId") Long doctorId);

}

/**
 * Retrofit DELETE request doesn't allow body in its request,
 * created this custom DELETE interface to accept body content.
 * TODO Need to delete this once the server side modifications are done.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
@RestMethod(value = "DELETE", hasBody = true)
@interface DELETE {
    String value();
}
