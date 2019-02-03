package com.bulwinkel.intellij.scrcpyconnect

import com.bulwinkel.support.cl.execForString
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

private const val SCRCPY_CONNECT_TITLE = "scrcpy connect"
private val NOTIFICATION_GROUP = NotificationGroup.balloonGroup(SCRCPY_CONNECT_TITLE)

fun notify(message: String, type: NotificationType = NotificationType.INFORMATION) {
    ApplicationManager.getApplication().invokeLater {
        val notification = NOTIFICATION_GROUP.createNotification(SCRCPY_CONNECT_TITLE, message, type, null)
        Notifications.Bus.notify(notification)
    }
}

class ScrcpyConnect : AnAction() {

    private var thread: Thread? = null

    override fun actionPerformed(e: AnActionEvent) {
        println("actionPerformed: thread = ${Thread.currentThread().name}")
        val path = execForString("which scrcpy")
        println("actionPerformed: path = $path")
        if (path.isBlank()) {
            notify("ERROR: scrcpy is not available on your path", NotificationType.ERROR)
            return
        }
        val result = execForString(path)
        println("actionPerformed: result = $result")
        notify("connecting")
    }
}