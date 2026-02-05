package com.daggerdiag.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.uast.*
import javax.swing.Icon

/**
 * Base class for Dagger-related line marker providers.
 *
 * Follows IntelliJ best practices:
 * - Uses LineMarkerProviderDescriptor for Settings integration
 * - Targets leaf PSI elements only (annotation name identifier)
 * - Supports both Kotlin and Java via UAST
 * - Lazy icon loading for performance
 */
abstract class DaggerLineMarkerProvider : LineMarkerProviderDescriptor() {

    /**
     * The qualified name of the annotation this provider handles
     * e.g., "dagger.Component", "dagger.hilt.android.HiltAndroidApp"
     */
    abstract val annotationFqn: String

    /**
     * Human-readable name for Settings UI
     */
    abstract override fun getName(): String

    /**
     * Icon for the gutter (lazy-loaded)
     */
    abstract fun getIcon(): Icon

    /**
     * Get the line marker info for a PSI element.
     * Only processes annotation name identifiers to avoid performance issues.
     */
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        // Only process leaf elements (identifiers) as per IntelliJ best practices
        if (!isAnnotationNameIdentifier(element)) {
            return null
        }

        val annotation = findAnnotation(element) ?: return null

        if (!matchesAnnotation(annotation)) {
            return null
        }

        val targetClass = findTargetClass(annotation) ?: return null

        return createLineMarkerInfo(
            element = element,
            annotation = annotation,
            targetClass = targetClass
        )
    }

    /**
     * Check if the element is an annotation name identifier
     */
    private fun isAnnotationNameIdentifier(element: PsiElement): Boolean {
        // For Kotlin: check if it's the name identifier of an annotation entry
        val ktAnnotation = PsiTreeUtil.getParentOfType(element, KtAnnotationEntry::class.java)
        if (ktAnnotation != null) {
            return element == ktAnnotation.typeReference?.typeElement?.navigationElement
        }

        // For Java/UAST: check if it's part of an annotation
        val uElement = element.toUElement()
        return uElement is UAnnotation || uElement?.uastParent is UAnnotation
    }

    /**
     * Find the annotation from the element
     */
    private fun findAnnotation(element: PsiElement): UAnnotation? {
        val uElement = element.toUElement()

        // Direct annotation
        if (uElement is UAnnotation) {
            return uElement
        }

        // Parent is annotation
        val parent = uElement?.uastParent
        if (parent is UAnnotation) {
            return parent
        }

        // Kotlin: get annotation from parent
        val ktAnnotation = PsiTreeUtil.getParentOfType(element, KtAnnotationEntry::class.java)
        if (ktAnnotation != null) {
            return ktAnnotation.toUElement() as? UAnnotation
        }

        return null
    }

    /**
     * Check if the annotation matches what this provider handles
     */
    private fun matchesAnnotation(annotation: UAnnotation): Boolean {
        val qualifiedName = annotation.qualifiedName ?: return false
        return qualifiedName == annotationFqn ||
               qualifiedName.endsWith(".${annotationFqn.substringAfterLast('.')}")
    }

    /**
     * Find the class that the annotation is applied to
     */
    private fun findTargetClass(annotation: UAnnotation): UClass? {
        val sourcePsi = annotation.sourcePsi ?: return null

        // Find Kotlin class
        val ktClass = PsiTreeUtil.getParentOfType(sourcePsi, KtClass::class.java)
        if (ktClass != null) {
            return ktClass.toUElement() as? UClass
        }

        // Find Java class via UAST
        val uElement = annotation.uastParent
        var current: UElement? = uElement
        while (current != null) {
            if (current is UClass) {
                return current
            }
            current = current.uastParent
        }

        return null
    }

    /**
     * Create the actual line marker info with navigation
     */
    private fun createLineMarkerInfo(
        element: PsiElement,
        annotation: UAnnotation,
        targetClass: UClass
    ): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            element,
            element.textRange,
            getIcon(),
            { getTooltipText(targetClass) },
            { event, _ -> handleNavigation(event.project!!, targetClass, annotation) },
            GutterIconRenderer.Alignment.LEFT,
            { getName() }
        )
    }

    /**
     * Get tooltip text for the gutter icon
     */
    protected open fun getTooltipText(targetClass: UClass): String {
        return "Navigate to Dagger graph for ${targetClass.name}"
    }

    /**
     * Handle navigation when the gutter icon is clicked.
     * Subclasses can override for custom behavior.
     */
    protected open fun handleNavigation(
        project: Project,
        targetClass: UClass,
        annotation: UAnnotation
    ) {
        // Default: Show the current visualization tool window
        // In POC, we'll just show a notification
        showGraphNavigation(project, targetClass)
    }

    /**
     * Show graph navigation (POC implementation)
     */
    private fun showGraphNavigation(project: Project, targetClass: UClass) {
        // For POC: Open the existing tool window
        val toolWindowManager = com.intellij.openapi.wm.ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Dagger Diagram")

        if (toolWindow != null) {
            toolWindow.show()

            // In future: Navigate to specific component graph
            // For now: Just show notification
            com.intellij.notification.NotificationGroupManager.getInstance()
                .getNotificationGroup("Dagger Diagram")
                .createNotification(
                    "Navigating to graph for ${targetClass.name}",
                    com.intellij.notification.NotificationType.INFORMATION
                )
                .notify(project)
        }
    }

    /**
     * Required by LineMarkerProviderDescriptor but not used since we override getLineMarkerInfo
     */
    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        // Not used - we override getLineMarkerInfo directly
    }
}
