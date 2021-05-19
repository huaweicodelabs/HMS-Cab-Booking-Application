# HMS Cab Booking Application

# Huawei Mobile Services
Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.

## Table of Contents
* [Introduction](#introduction)
* [Installation](#installation)
* [Supported Environments](#supported-environments)
* [Configuration](#configuration)
* [Sample Code](#sample-code)
* [License](#license)

## Introduction

HMS Cab Booking App is based on the cab booking kind of applications. This app will give insight about how HMS Map, Site and Location kits can be used for Travel purpose category. This app also demonstrate on how end user can book the cab easily using locations as well as nearby cabs with the help of Huawei Map. It provides many sample programs for your reference or usage.
The following describes packages of Android sample code.

* Map: Sample code of HUAWEI Map.
* Site: Sample code of HUAWEI Site.
* Location: Sample code of HUAWEI Location.
* Model: Code for model class.
* Adapter: Code for adapter holder.
* Common: Common components and utils.

## Installation

To use functions provided by examples, please make sure Huawei Mobile Service 5.0 has been installed on your cellphone.
There are two ways to install the sample demo:

* You can compile and build the codes in Android Studio. After building the APK, you can install it on the phone and debug it.
* Generate the APK file from Gradle. Use the ADB tool to install the APK on the phone and debug it adb install
{YourPath}\app\release\app-release.apk

## Supported Environments

Android SDK Version >= 19 and JDK version >= 1.8 is recommended.

## Configuration

Create an app in AppGallery Connect and obtain the project configuration file agconnect-services.json.
In Android Studio, switch to the Project view and move the agconnect-services.json file to the root directory of the app.

Change the value of applicationId in the build.gradle file of the app to the name of the app package applied for in the preceding step.

## Sample Code

The HMS Guide-Demo provides demonstration for following scenarios:

HuaweiMaActivity: Application home screen is this page which display huawei map with current location as well as nearby cabs and also have the option to select the source/destination address and confirm booking.

* [Map Kit](https://developer.huawei.com/consumer/en/hms/huawei-MapKit/)
* [Site Kit](https://developer.huawei.com/consumer/en/hms/huawei-sitekit/)
* [Location Kit](https://developer.huawei.com/consumer/en/hms/huawei-locationkit/)

## License
HMS Cab Booking Application is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).