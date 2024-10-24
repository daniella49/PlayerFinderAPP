package com.example.playerfinderapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable {
    private String id;
    private String name; // Add any other fields you need

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Member(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}

