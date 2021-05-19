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

/**
 * CabTypeModal modal.
 */
public class CabTypeModal implements Parcelable {
    int cabID = 1;
    String cabTypeName = "";
    String cabSeats = "";
    String bookingTypeName  = "";

    public CabTypeModal() {
    }

    public String getCabTypeName() {
        return cabTypeName;
    }

    public void setCabTypeName(String cabTypeName) {
        this.cabTypeName = cabTypeName;
    }

    public String getBookingTypeName() {
        return bookingTypeName;
    }

    public void setBookingTypeName(String bookingTypeName) {
        this.bookingTypeName = bookingTypeName;
    }

    public void setCabID(int cabID) {
        this.cabID = cabID;
    }

    public void setCabSeats(String cabSeats) {
        this.cabSeats = cabSeats;
    }

    protected CabTypeModal(Parcel in) {
        cabID = in.readInt();
        cabTypeName = in.readString();
        cabSeats = in.readString();
        bookingTypeName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cabID);
        dest.writeString(cabTypeName);
        dest.writeString(cabSeats);
        dest.writeString(bookingTypeName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static final Creator<CabTypeModal> CREATOR = new Creator<CabTypeModal>() {
        @Override
        public CabTypeModal createFromParcel(Parcel in) {
            return new CabTypeModal(in);
        }

        @Override
        public CabTypeModal[] newArray(int size) {
            return new CabTypeModal[size];
        }
    };
}
