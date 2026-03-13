package com.daggerdiag.listeners

import com.daggerdiag.services.DaggerAnalysisService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

/**
 * Listener that invalidates the Dagger graph cache when relevant files change
 */
class DaggerFileChangeListener(private val project: Project) : BulkFileListener {

    override fun after(events: MutableList<out VFileEvent>) {
        val hasRelevantChanges = events.any { event ->
            val path = event.path
            path.endsWith(".kt") || path.endsWith(".java")
        }

        if (hasRelevantChanges) {
            val analysisService = DaggerAnalysisService.getInstance(project)
            analysisService.invalidateCache()
        }
    }
}
