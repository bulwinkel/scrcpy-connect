package com.bulwinkel.support.cl

import com.intellij.execution.process.OSProcessHandler
import java.nio.charset.Charset
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.Key
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

// todo - convert this to coroutines
fun execForString(vararg cmds: String): String {
    val homeDir = System.getProperty("user.home")
    val cmdList = cmds.toList()
    println("execForString: cmds = $cmdList")

    val generalCommandLine = GeneralCommandLine(cmdList)
    generalCommandLine.charset = Charset.forName("UTF-8")
    generalCommandLine.setWorkDirectory(homeDir)

    val processHandler = OSProcessHandler(generalCommandLine)
    val output = Vector<String>()
    val isFinished = AtomicBoolean(false)
    processHandler.addProcessListener(object : ProcessListener {
        override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
            println("runCl::processListener::onTextAvailable: event.text = ${event.text}, event.exitCode = ${event.exitCode}, outputType = $outputType")
            if ("$outputType" == "stdout") {
                println("adding to output")
                output.addElement(event.text)
            }
        }

        override fun processTerminated(event: ProcessEvent) {
            println("runCl::processListener::processTerminated: event.text = ${event.text}, event.exitCode = ${event.exitCode}")
            isFinished.set(true)
        }

        override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
            println("runCl::processListener::processWillTerminate: event.text = ${event.text}, event.exitCode = ${event.exitCode}, willBeDestroyed = $willBeDestroyed")
        }

        override fun startNotified(event: ProcessEvent) {
            println("runCl::processListener::startNotified: event.text = ${event.text}, event.exitCode = ${event.exitCode}")
        }
    })
    processHandler.startNotify()
    while (!isFinished.get()) {
        println("output = $output")
    }

    return if (output.size == 1) output.firstElement() else output.joinToString("\n")
}

// this method works without having to start the ide from the command line
fun AnActionEvent.runCl(vararg cmds: String, onComplete: ((List<Pair<ProcessEvent, Key<*>?>>) -> Unit)? = null) {
    val projectPath = project?.basePath ?: "" // there should always be a path
    println("runCl: projectPath = $projectPath")

    val generalCommandLine = GeneralCommandLine(cmds.toList())
    generalCommandLine.charset = Charset.forName("UTF-8")
    generalCommandLine.setWorkDirectory(projectPath)

    val processHandler = OSProcessHandler(generalCommandLine)
    val output = Vector<Pair<ProcessEvent, Key<*>?>>()
    processHandler.addProcessListener(object : ProcessListener {
        override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
            println("runCl::processListener::onTextAvailable: event.text = ${event.text}, event.exitCode = ${event.exitCode}, outputType = $outputType")
            output.addElement(event to outputType)
        }

        override fun processTerminated(event: ProcessEvent) {
            println("runCl::processListener::processTerminated: event.text = ${event.text}, event.exitCode = ${event.exitCode}")
            output.addElement(event to null)
            onComplete?.invoke(output.toList())
        }

        override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
            println("runCl::processListener::processWillTerminate: event.text = ${event.text}, event.exitCode = ${event.exitCode}, willBeDestroyed = $willBeDestroyed")
            output.addElement(event to null)
        }

        override fun startNotified(event: ProcessEvent) {
            println("runCl::processListener::startNotified: event.text = ${event.text}, event.exitCode = ${event.exitCode}")
            output.addElement(event to null)
        }
    })
    processHandler.startNotify()
}
