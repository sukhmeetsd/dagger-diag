package com.daggerdiag.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.toUElement
import javax.swing.Icon

/**
 * Line marker provider for @ContributesAndroidInjector annotation.
 *
 * This annotation is used in Dagger-Android to generate subcomponents for Activities/Fragments.
 * Shows a gutter icon on the abstract method that will generate the subcomponent.
 */
class ContributesAndroidInjectorLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.android.ContributesAndroidInjector"

    override fun getName(): String = "Dagger @ContributesAndroidInjector"

    override fun getIcon(): Icon = AllIcons.Gutter.ImplementingMethod

    override fun getId(): String = "ContributesAndroidInjectorLineMarker"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        // This annotation is applied to methods, not classes
        // So we need custom logic
        val uMethod = element.toUElement()?.uastParent as? UMethod ?: return null

        val annotation = uMethod.uAnnotations.find {
            it.qualifiedName == annotationFqn
        } ?: return null

        // Get the return type (the Activity/Fragment class)
        val returnType = uMethod.returnType?.canonicalText ?: return null
        val className = returnType.substringAfterLast('.')
        val project = element.project

        return LineMarkerInfo(
            element,
            element.textRange,
            getIcon(),
            { "Navigate to generated $className subcomponent" },
            { _, _ ->
                // Open tool window and trigger analysis
                val toolWindowManager = com.intellij.openapi.wm.ToolWindowManager.getInstance(project)
                val toolWindow = toolWindowManager.getToolWindow("Dagger Diagram")
                if (toolWindow != null) {
                    toolWindow.show()
                    val analysisService = com.daggerdiag.services.DaggerAnalysisService.getInstance(project)
                    if (analysisService.getGraph() == null && !analysisService.isAnalyzing()) {
                        analysisService.analyzeAsync()
                    }
                }
            },
            GutterIconRenderer.Alignment.LEFT,
            { getName() }
        )
    }
}
