package com.maheshwari.stores.grocerystore.api;


import com.maheshwari.stores.grocerystore.model.Category;
import com.maheshwari.stores.grocerystore.model.CategoryResult;
import com.maheshwari.stores.grocerystore.model.Order;
import com.maheshwari.stores.grocerystore.model.OrdersResult;
import com.maheshwari.stores.grocerystore.model.PlaceOrder;
import com.maheshwari.stores.grocerystore.model.ProductResult;
import com.maheshwari.stores.grocerystore.model.SearchProduct;
import com.maheshwari.stores.grocerystore.model.Token;
import com.maheshwari.stores.grocerystore.model.User;
import com.maheshwari.stores.grocerystore.model.UserResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by : Akash Sharma
 */

public interface RestService {

    @POST("storesapi/StoresAPI/1.0.0/user/register")
    Call<UserResult> register(@Body User user);

    @POST("storesapi/StoresAPI/1.0.0/user/login")
    Call<UserResult> login(@Body User user);

    @POST("storesapi/StoresAPI/1.0.0/category/getAll")
    Call<CategoryResult> allCategory(@Body Token token);

    @FormUrlEncoded
    @POST("storesapi/StoresAPI/1.0.0/user/updateFCMToken")
    Call<UserResult> updateUserFCM(@Field("token") String token,@Field("user_id") String user_id,@Field("fcm_token") String fcm_token);

    @POST("storesapi/StoresAPI/1.0.0/product/getNew")
    Call<ProductResult> newProducts(@Body Token token);

    @POST("storesapi/StoresAPI/1.0.0/product/getPopular")
    Call<ProductResult> popularProducts(@Body Token token);

    @POST("storesapi/StoresAPI/1.0.0/product/getAllFromCategory")
    Call<ProductResult> getCategoryProduct(@Body Category category);

    //@POST("storesapi/StoresAPI/1.0.0/order/addOrder")
    @POST("storesapi/StoresAPI/1.0.0/mockapi")
    Call<OrdersResult> confirmPlaceOrder(@Body PlaceOrder placeOrder);

    @POST("storesapi/StoresAPI/1.0.0/order/getAllOrders")
    @FormUrlEncoded
    Call<OrdersResult> orderDetails(@Field("token") String token,@Field("user_id") String user_id);

    @POST("storesapi/StoresAPI/1.0.0/user/updateDetails")
    Call<UserResult> updateUser(@Body User user);

    @POST("storesapi/StoresAPI/1.0.0/product/searchAll")
    Call<ProductResult> searchProduct(@Body SearchProduct search);
}
