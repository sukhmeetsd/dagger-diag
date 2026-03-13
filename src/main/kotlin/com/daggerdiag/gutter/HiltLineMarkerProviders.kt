package com.daggerdiag.gutter

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import javax.swing.Icon

/**
 * Line marker provider for @HiltAndroidApp annotation.
 *
 * This marks the application class entry point for Hilt.
 * Links to the root ApplicationC component graph.
 */
class HiltAndroidAppLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.hilt.android.HiltAndroidApp"

    override fun getName(): String = "Hilt @HiltAndroidApp"

    override fun getIcon(): Icon = AllIcons.Nodes.HomeFolder

    override fun getId(): String = "HiltAndroidAppLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        return "Navigate to Hilt ApplicationC graph"
    }
}

/**
 * Line marker provider for @AndroidEntryPoint annotation.
 *
 * This marks Activities, Fragments, Services, etc. that use Hilt injection.
 * Maps to the appropriate generated component (e.g., ActivityC for Activities).
 */
class AndroidEntryPointLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.hilt.android.AndroidEntryPoint"

    override fun getName(): String = "Hilt @AndroidEntryPoint"

    override fun getIcon(): Icon = AllIcons.Actions.Lightning

    override fun getId(): String = "AndroidEntryPointLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        val componentType = inferHiltComponentType(targetClass)
        return "Navigate to Hilt $componentType graph"
    }

    private fun inferHiltComponentType(targetClass: UClass): String {
        // Get superclass and interfaces from the Java PSI
        val psiClass = targetClass.javaPsi
        val superTypes = mutableListOf<String>()

        psiClass.superClass?.qualifiedName?.let { superTypes.add(it) }
        psiClass.interfaces.forEach { it.qualifiedName?.let { name -> superTypes.add(name) } }

        return when {
            superTypes.any { it.contains("Activity") } -> "ActivityComponent"
            superTypes.any { it.contains("Fragment") } -> "FragmentComponent"
            superTypes.any { it.contains("Service") } -> "ServiceComponent"
            superTypes.any { it.contains("BroadcastReceiver") } -> "BroadcastReceiverComponent"
            superTypes.any { it.contains("View") } -> "ViewComponent"
            else -> "Component"
        }
    }
}

/**
 * Line marker provider for @HiltViewModel annotation.
 *
 * Marks ViewModels that are injected via Hilt.
 */
class HiltViewModelLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.hilt.android.lifecycle.HiltViewModel"

    override fun getName(): String = "Hilt @HiltViewModel"

    override fun getIcon(): Icon = AllIcons.Nodes.AbstractClass

    override fun getId(): String = "HiltViewModelLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        return "Navigate to Hilt ViewModelComponent graph"
    }
}

/**
 * Line marker provider for @DefineComponent annotation.
 *
 * Marks custom Hilt component definitions.
 */
class DefineComponentLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.hilt.DefineComponent"

    override fun getName(): String = "Hilt @DefineComponent"

    override fun getIcon(): Icon = AllIcons.Nodes.Interface

    override fun getId(): String = "DefineComponentLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        return "Navigate to custom Hilt component: ${targetClass.name}"
    }
}
