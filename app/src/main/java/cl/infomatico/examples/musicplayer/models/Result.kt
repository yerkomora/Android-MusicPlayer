package cl.infomatico.examples.musicplayer.models

class Result {
    var collectionId: Int = 0

    var artistName: String = ""
    var collectionName: String = ""
    var trackName: String = ""
    var artworkUrl100: String = ""

    var previewUrl: String = ""

    companion object CREATOR {
        const val ID = "ID"
    }
}