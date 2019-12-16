package cl.infomatico.examples.musicplayer.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ITunesClient {

    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://itunes.apple.com/"

    val instance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit
        }
}