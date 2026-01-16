package com.daggerdiag.actions

import com.daggerdiag.services.DaggerAnalysisService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

/**
 * Action to analyze Dagger component from the current editor
 */
class AnalyzeFromEditorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? KtFile ?: return

        // Check if file contains Dagger annotations
        val hasDaggerAnnotations = psiFile.declarations.filterIsInstance<KtClass>().any { ktClass ->
            ktClass.annotationEntries.any { annotation ->
                val name = annotation.shortName?.asString()
                name?.contains("Component") == true || name?.contains("Module") == true
            }
        }

        if (!hasDaggerAnnotations) {
            // Show notification
            return
        }

        // Trigger analysis
        val analysisService = DaggerAnalysisService.getInstance(project)
        analysisService.analyzeAsync().thenAccept {
            // Show tool window
            val toolWindowManager = ToolWindowManager.getInstance(project)
            val toolWindow = toolWindowManager.getToolWindow("Dagger Diagram")
            toolWindow?.show()
        }
    }

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabled = psiFile is KtFile
    }
}
