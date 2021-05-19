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
 * BookingTypeModal modal.
 */
public class BookingTypeModal implements Parcelable {
    private String bookingTypeName = "";
    private String bookingTypeId = "";
    private ArrayList<CabTypeModal> cabTypeModalList  = new ArrayList<>();

    public BookingTypeModal() {
    }

    public String getBookingTypeName() {
        return bookingTypeName;
    }

    public void setBookingTypeName(String bookingTypeName) {
        this.bookingTypeName = bookingTypeName;
    }

    public void setBookingTypeId(String bookingTypeId) {
        this.bookingTypeId = bookingTypeId;
    }

    public ArrayList<CabTypeModal> getCabTypeModalList() {
        return cabTypeModalList;
    }

    protected BookingTypeModal(Parcel in) {
        bookingTypeName = in.readString();
        bookingTypeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingTypeName);
        dest.writeString(bookingTypeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static final Creator<BookingTypeModal> CREATOR = new Creator<BookingTypeModal>() {
        @Override
        public BookingTypeModal createFromParcel(Parcel in) {
            return new BookingTypeModal(in);
        }

        @Override
        public BookingTypeModal[] newArray(int size) {
            return new BookingTypeModal[size];
        }
    };
}
