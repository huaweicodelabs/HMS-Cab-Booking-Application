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
 * DriverDetailsModel modal.
 */
public class DriverDetailsModel implements Parcelable {
    String driverId = "";
    String driverName = "";
    String carNumber = "";
    String phoneNumber = "";
    String rating = "";
    String totalNoOfTravel = "";

    public DriverDetailsModel() {
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setTotalNoOfTravel(String totalNoOfTravel) {
        this.totalNoOfTravel = totalNoOfTravel;
    }

    protected DriverDetailsModel(Parcel in) {
        driverId = in.readString();
        driverName = in.readString();
        carNumber = in.readString();
        phoneNumber = in.readString();
        rating = in.readString();
        totalNoOfTravel = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(driverId);
        dest.writeString(driverName);
        dest.writeString(carNumber);
        dest.writeString(phoneNumber);
        dest.writeString(rating);
        dest.writeString(totalNoOfTravel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static final Creator<DriverDetailsModel> CREATOR = new Creator<DriverDetailsModel>() {
        @Override
        public DriverDetailsModel createFromParcel(Parcel in) {
            return new DriverDetailsModel(in);
        }

        @Override
        public DriverDetailsModel[] newArray(int size) {
            return new DriverDetailsModel[size];
        }
    };
}
