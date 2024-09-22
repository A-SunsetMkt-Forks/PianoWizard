package pansong291.piano.wizard

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pansong291.piano.wizard.events.AccessibilityConnectedEvent
import pansong291.piano.wizard.services.ClickAccessibilityService
import pansong291.piano.wizard.services.MainService
import pansong291.piano.wizard.toast.Toaster


class MainActivity : AppCompatActivity() {
    private lateinit var btnFilePerm: Button
    private lateinit var btnWinPerm: Button
    private lateinit var btnAccessibilityPerm: Button
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        btnFilePerm = findViewById(R.id.btn_main_file_perm)
        btnAccessibilityPerm = findViewById(R.id.btn_main_accessibility_perm)
        btnWinPerm = findViewById(R.id.btn_main_win_perm)
        btnStart = findViewById(R.id.btn_main_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnFilePerm.setOnClickListener {
            XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request { _, _ -> updatePermState(1, true) }
        }
        btnWinPerm.setOnClickListener {
            XXPermissions.with(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request { _, _ -> updatePermState(2, true) }
        }
        btnAccessibilityPerm.setOnClickListener {
            if (isAccessibilitySettingsOn(this, ClickAccessibilityService::class.java)) {
                updatePermState(4, true)
            } else {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        btnStart.setOnClickListener {
            startService(Intent(this, MainService::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        updatePermState(7)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AccessibilityConnectedEvent?) {
        Toaster.show("无障碍已启用")
        updatePermState(4)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun updatePermState(flags: Int, success: Boolean? = null) {
        if (flags and 1 == 1) btnFilePerm.text = getString(
            R.string.btn_req_file_perm, getOnOffString(
                success ?: XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE)
            )
        )
        if (flags and 2 == 2) btnWinPerm.text = getString(
            R.string.btn_req_win_perm, getOnOffString(
                success ?: XXPermissions.isGranted(this, Permission.SYSTEM_ALERT_WINDOW)
            )
        )
        if (flags and 4 == 4) btnAccessibilityPerm.text = getString(
            R.string.btn_req_accessibility_perm, getOnOffString(
                success ?: isAccessibilitySettingsOn(
                    this,
                    ClickAccessibilityService::class.java
                )
            )
        )
    }

    private fun getOnOffString(b: Boolean): String {
        if (b) return getString(R.string.common_on)
        return getString(R.string.common_off)
    }

    private fun isAccessibilitySettingsOn(
        mContext: Context,
        clazz: Class<out AccessibilityService>
    ): Boolean {
        try {
            if (
                Settings.Secure.getInt(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                ) == 1
            ) {
                Settings.Secure.getString(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )?.let {
                    val service = mContext.packageName + "/" + clazz.canonicalName
                    TextUtils.SimpleStringSplitter(':').apply {
                        setString(it)
                        while (hasNext()) {
                            if (next().equals(service, ignoreCase = true)) {
                                return true
                            }
                        }
                    }
                }
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return false
    }
}
