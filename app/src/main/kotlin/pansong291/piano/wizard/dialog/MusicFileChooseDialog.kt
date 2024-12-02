package pansong291.piano.wizard.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import pansong291.piano.wizard.R
import pansong291.piano.wizard.consts.StringConst
import java.io.File
import java.io.FileFilter

@SuppressLint("NotifyDataSetChanged")
class MusicFileChooseDialog(context: Context) : FileChooseDialog(context) {
    init {
        initialize { ok ->
            // TODO 后续支持弹奏多个乐谱时改造 ok 按钮
            //  支持列表弹奏模式, 与单曲模式分开, 该模式无法调整变调, 自动忽略无法完整弹奏问题
            //  初始时未选择歌曲, 自动切歌, 支持循环和随机
            //  暂停时可切歌, 支持进度调整 (需要换算播放进度)
            //  在开始列表播放模式后，一次性读取列表缓存起来，切歌的时候不必刷新列表
            ok.visibility = View.GONE
        }
        setIcon(R.drawable.outline_music_file_32)
        setTitle(R.string.select_music)
        adapter.basePath = File(context.getExternalFilesDir(null), "yp").path
        adapter.fileFilter = FileFilter {
            it.isDirectory || it.name.endsWith(StringConst.MUSIC_NOTATION_FILE_EXT, true)
        }
    }
}
