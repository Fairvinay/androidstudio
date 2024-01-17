package com.jwell.util;

//import okhttp3.MultipartBody;
import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
        import retrofit2.http.Header;
        import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceInterface {
   /* @GET(HttpConstants.SCANDATAJSON)
    Call<List> taskData(@Query("method")String method, @Query("stdID")int stdID);
*/

/*
    @POST("/upload")
    Call< okhttp3.ResponseBody> someCreateMethod(@Header("Content-Type") String contentType, @Body  RequestBody body);
*/
/*
    @POST("/upload")
   Call< okhttp3.ResponseBody> someCreateMethod(@Header("Content-Type") String contentType, @Body MultipartBody body);
*/
    @Multipart
    @POST("/upload")
    Call<okhttp3.ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    /*
    @Multipart
    @POST("/upload")
    Call<okhttp3.ResponseBody> addRecord(  @Part("file") MultipartBody.Part file);
    */
  /*  @Multipart
    @PUT("/tika")
    Call<okhttp3.ResponseBody> addRecord(@Query("token") String token, @Query("userid") int userId,
                                 @Query("name") String name, @Part("file") MultipartBody.Part file);
    *//*
    @Multipart
    @POST("/upload")
    Call<ResponseBody> addRecordPartOk( @Part("file") MultipartBody.Part file);
     */
  /*
    @Multipart
    @POST("/upload")
    @Headers( {

            "Content-Type: multipart/form-data;charset=UTF-8",
            "Accept: text/plain",
            "User-Agent: Retrofit 2.0.0"
    })
            Call<okhttp3.ResponseBody> addRecordPart( @Part("file") MultipartBody.Part file);
/*
    @Multipart
    @PUT("/tika")
    Call<ResponseBody> addRecordPart(@Query("token") String token, @Query("userid") int userId,
                                         @Query("name") String name, @Part("file") MultipartBody.Part file);




    @Multipart
    @POST("/upload")
    Call<okhttp3.ResponseBody> addRecord(  @Part("file\"; filename=\"pp.png\" ") okhttp3.RequestBody file);

    @Multipart
    @POST("/upload")
    Call<ResponseBody> addRecord(@Part("file\"; filename=\"pp.png\" ") RequestBody file);

   */
}
//https://stackoverflow.com/questions/29680158/how-to-send-multipart-form-data-with-retrofit
// https://stackoverflow.com/questions/68549275/update-default-content-type-for-multipart-form-data-request-in-android
//https://stackoverflow.com/questions/74808733/multer-unexpected-end-of-form
//https://stackoverflow.com/questions/36756019/sending-plain-text-as-post-body-causes-issue-in-retrofit
//https://stackoverflow.com/questions/42578764/image-file-not-going-to-server-using-retrofit-2-post-method