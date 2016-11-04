package com.app.todo.network;

import com.app.todo.model.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by arifkhan on 03/11/16.
 */
public interface DataApiEndpointInterface {
    // Request method and URL specified in the annotation

    @GET("u/{id}/tasks.json")
    Call<Data> getData(@Path("id") String id);

}