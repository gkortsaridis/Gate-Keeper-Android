package gr.gkortsaridis.gatekeeper.Utils

object FavIconDownloader {
    fun buildUrl(websiteUrl: String, color: String): String {
        return "https://i.olsh.me/icon?size=30..120..200&url=$websiteUrl&fallback_icon_color=$color"
    }
}