package zs.android.module.media.bean

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import zs.android.module.media.MediaService

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/5 14:24
 * @description
 */
class PlayInfo {
    private var metadata: MediaMetadataCompat? = null
    private var state: PlaybackStateCompat? = null
    private var children: List<MediaBrowserCompat.MediaItem>? = null
    fun setMetadata(metadata: MediaMetadataCompat?) {
        this.metadata = metadata
    }

    fun setState(state: PlaybackStateCompat?) {
        this.state = state
    }

    fun setChildren(children: List<MediaBrowserCompat.MediaItem>?) {
        this.children = children
    }

    fun debugInfo(): String {
        val builder = StringBuilder()
        if (state != null) {
            builder.append("当前播放状态：\t")
                .append(if (state!!.state == PlaybackStateCompat.STATE_PLAYING) "播放中" else "未播放")
            val extras = state!!.extras
            if (null != extras) {
                val currentPosition = extras.getLong(MediaService.KEY_CURRENT_POSITION, 0)
                val duration = extras.getLong(MediaService.KEY_DURATION, 0)
                builder.append("\n当前播放进度：\t").append("currentPosition = ")
                    .append(currentPosition).append(" / duration = ").append(duration)
            }
            builder.append("\n\n")
        }
        if (metadata != null) {
            builder.append("当前播放信息：\t").append(transform(metadata))
            builder.append("\n\n")
        }
        if (children != null && children!!.isNotEmpty()) {
            builder.append("当前播放列表：").append("\n")
            for (i in children!!.indices) {
                val mediaItem = children!![i]
                builder.append(i + 1).append(" ").append(mediaItem.description.title).append(" - ")
                    .append(mediaItem.description.subtitle).append("\n")
            }
        }
        return builder.toString()
    }

    companion object {
        fun transform(data: MediaMetadataCompat?): String? {
            if (data == null) {
                return null
            }
            val title = data.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            val artist = data.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
            val albumName = data.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
            val mediaNumber = data.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)
            val mediaTotalNumber = data.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)
            return "$title - $artist"
        }
    }
}