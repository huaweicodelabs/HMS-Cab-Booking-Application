/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.huawei.hmscabsoftware.java.modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * CabTypeModal modal.
 */
public class BookingType implements Parcelable {
    private String BookingType = "";
    private String BookingTypeId = "";
    private ArrayList<CabTypeModal> Cabtype  = new ArrayList<>();

    public BookingType() {
    }

    public String getBookingType() {
        return BookingType;
    }

    public void setBookingType(String bookingType) {
        BookingType = bookingType;
    }

    public void setBookingTypeId(String bookingTypeId) {
        BookingTypeId = bookingTypeId;
    }

    public ArrayList<CabTypeModal> getCabtype() {
        return Cabtype;
    }

    protected BookingType(Parcel in) {
        BookingType = in.readString();
        BookingTypeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(BookingType);
        dest.writeString(BookingTypeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static final Creator<BookingType> CREATOR = new Creator<BookingType>() {
        @Override
        public BookingType createFromParcel(Parcel in) {
            return new BookingType(in);
        }

        @Override
        public BookingType[] newArray(int size) {
            return new BookingType[size];
        }
    };
}
