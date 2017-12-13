/* Copyright 2017 Andrew Dawson
 *
 * This file is a part of Tusky.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>. */

package org.tootto.network;

import org.tootto.entity.AppCredentials;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.POST;


public interface MastodonApi {
    String ENDPOINT_AUTHORIZE = "/oauth/authorize";
    @FormUrlEncoded
    @POST("api/v1/apps")
    Call<AppCredentials> authenticateApp(
            @Field("client_name") String clientName,
            @Field("redirect_uris") String redirectUris,
            @Field("scopes") String scopes,
            @Field("website") String website);



}
