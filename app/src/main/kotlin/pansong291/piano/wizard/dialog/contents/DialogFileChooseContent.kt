package pansong291.piano.wizard.dialog.contents

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Environment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import pansong291.piano.wizard.R
import pansong291.piano.wizard.consts.ColorConst
import pansong291.piano.wizard.dialog.IDialog
import java.io.File
import java.io.FileFilter

object DialogFileChooseContent {
    fun loadIn(dialog: IDialog): Pair<FastScrollRecyclerView, FileListAdapter> {
        val application = dialog.getAppContext()
        val content = View.inflate(
            application,
            R.layout.dialog_content_file_choose,
            dialog.findContentWrapper()
        )
        val adapter = FileListAdapter(application)
        // 主内容：一个回退按钮和文件列表
        val backwardItem = content.findViewById<AppCompatTextView>(android.R.id.undo).apply {
            ellipsize = TextUtils.TruncateAt.START
            setOnClickListener { adapter.backwardFolder() }
        }
        adapter.onPathLoaded = {
            backwardItem.text = it
        }
        val recyclerView = content.findViewById<FastScrollRecyclerView>(android.R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(application).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recyclerView.adapter = adapter
        return recyclerView to adapter
    }

    class FileInfo {
        var icon: Int = 0
        var name: String = ""
    }

    class FileViewHolder(val textView: AppCompatTextView) : RecyclerView.ViewHolder(textView)

    class FileListAdapter(
        private val application: Application
    ) : RecyclerView.Adapter<FileViewHolder>() {
        private lateinit var fileList: List<FileInfo>
        var highlight: Pair<String, String>? = null
        var basePath: String = Environment.getExternalStorageDirectory().path
        var fileFilter: FileFilter = FileFilter { true }
        var onFileChose: ((path: String, file: String) -> Unit)? = null
        var onPathLoaded: ((path: String) -> Unit)? = null

        fun reload() {
            loadFileList(null)
        }

        fun backwardFolder() {
            val cur = File(basePath)
            if (Environment.getExternalStorageDirectory() == cur) return
            loadFileList(cur.parentFile)
            cur.parent?.let { basePath = it }
        }

        fun forwardFolder(folder: String) {
            val file = File(basePath, folder)
            loadFileList(file)
            basePath = file.path
        }

        fun findItemPosition(predicate: (FileInfo) -> Boolean): Int {
            return fileList.indexOfFirst(predicate)
        }

        fun getItem(position: Int): FileInfo? {
            return fileList.getOrNull(position)
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun loadFileList(folder: File?) {
            val folderFile = folder ?: File(basePath)
            fileList = folderFile.listFiles(fileFilter)?.map {
                FileInfo().apply {
                    icon = if (it.isDirectory) R.drawable.outline_folder_24
                    else R.drawable.outline_file_24
                    name = it.name
                }
            }?.sortedWith { p, q ->
                when (p.icon) {
                    q.icon -> p.name.compareTo(q.name)
                    R.drawable.outline_folder_24 -> -1
                    else -> 1
                }
            } ?: emptyList()
            onPathLoaded?.invoke(folderFile.path)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
            val view = LayoutInflater.from(application)
                .inflate(R.layout.list_item_file, parent, false) as AppCompatTextView
            return FileViewHolder(view)
        }

        override fun getItemCount(): Int {
            return fileList.size
        }

        override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
            val item = fileList[position]
            holder.textView.setTextColor(
                if (highlight?.let { it.first == basePath && it.second == item.name } == true)
                    ColorConst.GREEN_600
                else Color.BLACK
            )
            holder.textView.text = item.name
            holder.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, 0, 0, 0)
            holder.itemView.setOnClickListener {
                if (item.icon == R.drawable.outline_folder_24) {
                    forwardFolder(item.name)
                } else {
                    onFileChose?.invoke(basePath, item.name)
                }
            }
        }
    }
}
