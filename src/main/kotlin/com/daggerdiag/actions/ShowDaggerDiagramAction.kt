package com.daggerdiag.actions

import com.daggerdiag.services.DaggerAnalysisService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Action to show the Dagger diagram tool window
 */
class ShowDaggerDiagramAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Get the tool window and show it
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Dagger Diagram")

        toolWindow?.let {
            it.show()

            // Trigger analysis if not already done
            val analysisService = DaggerAnalysisService.getInstance(project)
            if (analysisService.getGraph() == null && !analysisService.isAnalyzing()) {
                analysisService.analyzeAsync()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        // Enable action only when a project is open
        e.presentation.isEnabled = e.project != null
    }
}
