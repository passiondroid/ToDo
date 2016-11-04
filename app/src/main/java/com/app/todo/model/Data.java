package com.app.todo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arifkhan on 03/11/16.
 */
public class Data implements Parcelable{

@SerializedName("data")
@Expose
private List<Task> data = new ArrayList<Task>();

/**
* 
* @return
* The data
*/
public List<Task> getData() {
return data;
}

/**
* 
* @param data
* The data
*/
public void setData(List<Task> data) {
this.data = data;
}

    @Override
    public String toString() {
        return "Data{" +
                "data=" + data +
                '}';
    }

    protected Data(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Task>();
            in.readList(data, Task.class.getClassLoader());
        } else {
            data = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}