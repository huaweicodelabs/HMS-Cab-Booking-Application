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
package com.huawei.hmscabsoftware.kotlin.utils

/**
 * Place all constant values
 */
object ApiConstant {
    /**
     * Direction API Base URL
     */
    const val DRIVINGBASEURL =
        "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/"
    /**
     * Direction API Method Name
     */
    const val DRIVINGSUFFIXURL = "driving?key="
    /**
     * Encoded API Key for Map & Site API
     */
    const val APIENCODEDKEY = "API_KEY"
    /**
     * Site API Base URL
     */
    const val SITEBASEURL = "https://siteapi.cloud.huawei.com/mapApi/v1/siteService/"
    /**
     * Reverse Geocode Suffix URL
     */
    const val REVERSEGEOCODESUFFIXURL = "reverseGeocode?key="
}

