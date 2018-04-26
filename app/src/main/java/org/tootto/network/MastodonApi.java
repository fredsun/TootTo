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

import org.tootto.entity.AccessToken;
import org.tootto.entity.Account;
import org.tootto.entity.AppCredentials;
import org.tootto.entity.SearchResults;
import org.tootto.entity.Status;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface MastodonApi {
    String ENDPOINT_AUTHORIZE = "/oauth/authorize";
    @FormUrlEncoded
    @POST("api/v1/apps")
    Call<AppCredentials> authenticateApp(
            @Field("client_name") String clientName,
            @Field("redirect_uris") String redirectUris,
            @Field("scopes") String scopes,
            @Field("website") String website);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> fetchOAuthToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("code") String code,
            @Field("grant_type") String grantType
    );

    @GET("api/v1/accounts/verify_credentials")
    Call<Account> accountVerifyCredentials();

    @GET("api/v1/timelines/home")
    Call<List<Status>> homeTimeline(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);

    @GET("api/v1/timelines/public")
    Call<List<Status>> publicTimeline(
            @Query("local") Boolean local,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);

    @GET("api/v1/search")
    Call<SearchResults> search(@Query("q") String q, @Query("resolve") Boolean resolve);
}
