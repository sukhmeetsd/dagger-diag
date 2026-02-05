package com.daggerdiag.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
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

        return LineMarkerInfo(
            element,
            element.textRange,
            getIcon(),
            { "Navigate to generated $className subcomponent" },
            { event, _ ->
                // Show notification for POC
                com.intellij.notification.NotificationGroupManager.getInstance()
                    .getNotificationGroup("Dagger Diagram")
                    .createNotification(
                        "Navigating to $className subcomponent",
                        com.intellij.notification.NotificationType.INFORMATION
                    )
                    .notify(event.project!!)
            },
            GutterIconRenderer.Alignment.LEFT,
            { getName() }
        )
    }
}
