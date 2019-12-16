package cl.infomatico.examples.musicplayer.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cl.infomatico.examples.musicplayer.R
import cl.infomatico.examples.musicplayer.adapters.ResultAdapter
import cl.infomatico.examples.musicplayer.models.Result
import cl.infomatico.examples.musicplayer.network.ITunesApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_album.*

class AlbumActivity : AppCompatActivity() {

    private var id: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        id = intent.getIntExtra(Result.ID, 0)

        resultsLoad()
        mediaPlayer = MediaPlayer()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer.reset()
    }

    // Results

    private var iTunesApi: ITunesApi = ITunesApi()
    private lateinit var resultAdapter: ResultAdapter

    private fun resultsLoad() {

        if (id == 0)
            return

        iTunesApi.lookup(object : ITunesApi.Listener {
            override fun load(results: List<Result>) {
                if (results.isEmpty()) {
                    llNoResultProducts.visibility = View.VISIBLE
                    //searchView.clearFocus()
                } else {
                    llNoResultProducts.visibility = View.GONE
                }

                val album = results[0]
                tvArtist.text = album.artistName
                tvAlbum.text = album.collectionName

                Picasso.get().load(album.artworkUrl100).into(ivCover)

                val subList = results.subList(1, results.size)

                resultAdapter = ResultAdapter()
                resultAdapter.setResults(ArrayList(subList))
                rvProducts.adapter = resultAdapter

                supportStartPostponedEnterTransition()

                resultAdapter.onListener = object : ResultAdapter.OnListener {
                    override fun onClick(
                        result: Result
                        , viewHolder: ResultAdapter.ViewHolder
                    ) {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(result.previewUrl)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        // goAlbum(result, viewHolder)
                    }
                }
            }
        }, "song", id, 200)
    }
}