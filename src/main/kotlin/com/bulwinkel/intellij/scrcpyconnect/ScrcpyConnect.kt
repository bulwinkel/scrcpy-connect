package com.bulwinkel.intellij.scrcpyconnect

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

private const val SCRCPY_CONNECT_TITLE = "scrcpy connect"
private val NOTIFICATION_GROUP = NotificationGroup.balloonGroup(SCRCPY_CONNECT_TITLE)

class ScrcpyConnect : AnAction() {

    private var thread: Thread? = null

    override fun actionPerformed(e: AnActionEvent) {
        thread = object : Thread() {
            override fun run() {
                Runtime.getRuntime().exec("scrcpy").waitFor()
            }
        }.apply { start() }

        ApplicationManager.getApplication().invokeLater {
            val notification = NOTIFICATION_GROUP.createNotification(SCRCPY_CONNECT_TITLE, "Starting scrcpy", NotificationType.INFORMATION, null)
            Notifications.Bus.notify(notification)
        }
    }
}