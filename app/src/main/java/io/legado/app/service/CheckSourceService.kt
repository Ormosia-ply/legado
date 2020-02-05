package io.legado.app.service

import android.content.Intent
import androidx.core.app.NotificationCompat
import io.legado.app.R
import io.legado.app.base.BaseService
import io.legado.app.constant.AppConst
import io.legado.app.constant.IntentAction
import io.legado.app.help.AppConfig
import io.legado.app.help.IntentHelp
import io.legado.app.ui.book.source.manage.BookSourceActivity
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

class CheckSourceService : BaseService() {
    private var searchPool =
        Executors.newFixedThreadPool(AppConfig.threadCount).asCoroutineDispatcher()
    private val allIds = LinkedHashSet<String>()
    private val unCheckIds = LinkedHashSet<String>()

    override fun onCreate() {
        super.onCreate()
        updateNotification(0, getString(R.string.start))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            IntentAction.start -> intent.getStringArrayListExtra("selectIds")?.let {
                check(it)
            }
            else -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchPool.close()
    }

    private fun check(ids: List<String>) {
        allIds.addAll(ids)
        unCheckIds.addAll(ids)

    }

    /**
     * 更新通知
     */
    private fun updateNotification(state: Int, msg: String) {
        val builder = NotificationCompat.Builder(this, AppConst.channelIdReadAloud)
            .setSmallIcon(R.drawable.ic_network_check)
            .setOngoing(true)
            .setContentTitle(getString(R.string.check_book_source))
            .setContentText(msg)
            .setContentIntent(
                IntentHelp.activityPendingIntent<BookSourceActivity>(this, "activity")
            )
            .addAction(
                R.drawable.ic_stop_black_24dp,
                getString(R.string.cancel),
                IntentHelp.servicePendingIntent<CheckSourceService>(this, IntentAction.stop)
            )
        builder.setProgress(allIds.size, state, false)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        val notification = builder.build()
        startForeground(112202, notification)
    }

}