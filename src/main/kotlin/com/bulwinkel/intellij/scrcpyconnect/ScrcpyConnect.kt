package com.bulwinkel.intellij.scrcpyconnect

import com.bulwinkel.support.cl.runCl
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

    override fun actionPerformed(e: AnActionEvent) {
        val projectPath = e.project?.basePath ?: ""
        println("actionPerformed: projectPath = $projectPath")

        notify("connecting")
        e.runCl("scrcpy")

        // todo - command `which scrcpy` don't run due to a process exception
//        e.runCl("which scrcpy") { output ->
//            val scrcpyPath = output.filter { it.second != null && "${it.second}" == "stdout" }
//                .joinToString("\n") { it.first.text }
//                .trim()
//
//            println("scrcpyPath = $scrcpyPath")
//
//            // scrcpy is available on the path
//            if (scrcpyPath.isNotBlank()) {
//                notify("connecting")
//                e.runCl("scrcpy")
//            } else {
//                notify("ERROR: scrcpy is not available on your path", NotificationType.ERROR)
//            }
//        }
    }
}