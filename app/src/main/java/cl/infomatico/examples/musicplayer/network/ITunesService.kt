package cl.infomatico.examples.musicplayer.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {
    @GET("search")
    fun search(
        @Query("media") media: String,
        @Query("entity") entity: String,
        @Query("term") term: String,
        @Query("limit") limit: Int
    ): Call<ITunesResponse>

    @GET("lookup")
    fun lookup(
        @Query("entity") entity: String,
        @Query("id") id: Int,
        @Query("limit") limit: Int
    ): Call<ITunesResponse>
}