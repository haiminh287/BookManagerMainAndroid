package com.example.bookmanager.network;
import com.example.bookmanager.model.CardItem;
import com.example.bookmanager.model.CardReviewItem;
import com.example.bookmanager.model.Category;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.model.ResponsePayment;
import com.example.bookmanager.model.ReviewStatusResponse;
import com.example.bookmanager.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/categories")
    Call<List<Category>> getCategory();

    @GET("api/books")
    Call<List<CardItem>> getBooks();

    @GET("api/books/{id}")
    Call<CardItem> getBookDetail(@Path("id") int bookId);

    @FormUrlEncoded
    @POST("api/books/{id}/reviews")
    Call<Void> addReview(@Path("id") int bookId,@Field("content") String content,@Field("user_id") int userId);


    @GET("api/books/{id}/reviews")
    Call<List<CardReviewItem>> getReviews(@Path("id") int bookId);
    @GET("orders/{order_id}/pay_android")
    Call<ResponsePayment> payOrder(@Path("order_id") int orderId);
    @GET("/api/users/{user_id}/books/{book_id}/reviews/status")
    Call<ReviewStatusResponse> getReviewStatus(
            @Path("user_id") int userId,
            @Path("book_id") int bookId
    );

    @GET("/api/orders/{order_id}/update_state_cancel")
    Call<ReviewStatusResponse> updateOrderStatus(
            @Path("order_id") int orderId
    );
    @GET("/api/orders/{order_id}/status")
    Call<ReviewStatusResponse> getOrderStatus(
            @Path("order_id") int orderId
    );
    @FormUrlEncoded
    @POST("api/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );
    @FormUrlEncoded
    @POST("api/register")
    Call<User> register(
            @Field("name") String name,
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("password_confirmation") String password_confirmation
    );

    @GET("api/user/{id}")
    Call<User> getUser(@Path("id") int userId);



    @GET("api/user/{id}/taked_books")
    Call<List<ItemTaked>> getTakedBook(@Path("id") int userId);

    @GET("api/category/")
    Call<List<CardItem>> getBooksByCategory(@Query("cate_id") int categoryId);

    @GET("api/user/{id}/historyorder")
    Call<List<Order>> getHistoryOrder(@Path("id") int userId);

    @GET("api/orders/{id}")
    Call<List<ItemTaked>> getOrderDetail(@Path("id") int orderId);
    @FormUrlEncoded
    @POST("api/user/{id}/taked_book_details")
    Call<Void> addBookToCart(
            @Path("id") int userId,
            @Field("book_id") int bookId
    );

    @FormUrlEncoded
    @POST("api/user/{id}/orders")
    Call<Void> addOrder(
            @Path("id") int userId,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("address") String address

    );
    @FormUrlEncoded
    @PATCH("api/user/{user_id}/taked_book_details/{book_id}/quantity")
    Call<Void> updateTakedBookQuantity(
            @Path("user_id") int userId,
            @Path("book_id") int bookId,
            @Field("quantity") int quantity
    );

    @DELETE("api/user/{user_id}/taked_book_details/{book_id}")
    Call<Void> deleteTakedBookQuantity(
            @Path("user_id") int userId,
            @Path("book_id") int bookId
    );

}
