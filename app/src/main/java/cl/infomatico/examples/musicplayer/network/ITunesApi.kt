package cl.infomatico.examples.musicplayer.network

import cl.infomatico.examples.musicplayer.models.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ITunesApi {

    private var iTunesService: ITunesService? =
        ITunesClient.instance?.create(ITunesService::class.java)

    interface Listener {
        fun load(results: List<Result>) {}
        fun error() {}
    }

    fun search(
        listener: Listener, media: String, entity: String, term: String, limit: Int
    ) {
        val iTunesResponse: Call<ITunesResponse>? =
            iTunesService?.search(media, entity, term, limit)

        iTunesResponse?.enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() == null)
                        listener.error()
                    else listener.load((response.body()!!.results))
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                listener.error()
            }
        })
    }

    fun lookup(
        listener: Listener, entity: String, id: Int, limit: Int
    ) {
        val iTunesResponse: Call<ITunesResponse>? =
            iTunesService?.lookup(entity, id, limit)

        iTunesResponse?.enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() == null)
                        listener.error()
                    else
                        listener.load(response.body()!!.results)
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                listener.error()
            }
        })
    }
}