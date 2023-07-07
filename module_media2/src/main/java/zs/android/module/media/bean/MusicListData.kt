package com.media.android.bean

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import zs.android.module.media.R

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/5 14:07
 * @description
 */
object MusicListData {

    /**
     * rwa 转换成Uri
     */
    @JvmStatic
    fun rawToUri(context: Context, id: Int?): Uri {
        val uriStr = "android.resource://${context.packageName}/$id"
        return Uri.parse(uriStr)
    }

    @JvmStatic
    fun getPlayList(): List<MusicData> {
        return arrayListOf<MusicData>().apply {
            MusicData().apply {
                mediaId = R.raw.cesinspire
                title = "cesinspire.mp3"
                artist = "千千阙歌"
                add(this)
            }

            MusicData().apply {
                mediaId = R.raw.bigbigbig
                title = "bigbigbig"
                artist = "罗罗罗罗罗罗"
                add(this)
            }
        }
    }

    @JvmStatic
    fun getPlayListUpdate(): List<MusicData> {
        return arrayListOf<MusicData>().apply {
            MusicData().apply {
                mediaId = R.raw.cesinspire
                title = "cesinspire.mp3"
                artist = "千千阙歌"
                add(this)
            }

            MusicData().apply {
                mediaId = R.raw.bigbigbig
                title = "bigbigbig"
                artist = "罗罗罗罗罗罗"
                add(this)
            }

            MusicData().apply {
                mediaId = R.raw.fengtimo
                title = "说散就散"
                artist = "fengtimo"
                add(this)
            }
        }
    }

    /**
     * 数据转换
     */
    @JvmStatic
    fun transformPlayList(playBeanList: List<MusicData>): ArrayList<MediaBrowserCompat.MediaItem> {
        return arrayListOf<MediaBrowserCompat.MediaItem>().apply {
            playBeanList.forEach {
                val metadata = transformPlayBean(it)
                add(createMediaItem(metadata))
            }
        }
    }

    @JvmStatic
    fun transformPlayBean(bean: MusicData): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + bean.mediaId)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, bean.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, bean.artist)
            .build()
    }

    @JvmStatic
    fun createMediaItem(metadata: MediaMetadataCompat): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            metadata.description,
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
    }
}