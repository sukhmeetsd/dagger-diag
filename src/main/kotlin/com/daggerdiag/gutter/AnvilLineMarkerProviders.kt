package com.daggerdiag.gutter

import com.intellij.icons.AllIcons
import org.jetbrains.uast.UClass
import javax.swing.Icon

/**
 * Line marker provider for @MergeComponent annotation (Anvil).
 *
 * Anvil's @MergeComponent automatically merges modules and component interfaces.
 */
class MergeComponentLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "com.squareup.anvil.annotations.MergeComponent"

    override fun getName(): String = "Anvil @MergeComponent"

    override fun getIcon(): Icon = AllIcons.Vcs.Merge

    override fun getId(): String = "AnvilMergeComponentLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        return "Navigate to Anvil merged component graph: ${targetClass.name}"
    }
}

/**
 * Line marker provider for @MergeSubcomponent annotation (Anvil).
 */
class MergeSubcomponentLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "com.squareup.anvil.annotations.MergeSubcomponent"

    override fun getName(): String = "Anvil @MergeSubcomponent"

    override fun getIcon(): Icon = AllIcons.Hierarchy.Subtypes

    override fun getId(): String = "AnvilMergeSubcomponentLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        return "Navigate to Anvil merged subcomponent graph: ${targetClass.name}"
    }
}

/**
 * Line marker provider for @ContributesTo annotation (Anvil).
 *
 * @ContributesTo marks modules or interfaces to be contributed to a specific component scope.
 */
class ContributesToLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "com.squareup.anvil.annotations.ContributesTo"

    override fun getName(): String = "Anvil @ContributesTo"

    override fun getIcon(): Icon = AllIcons.Vcs.Push

    override fun getId(): String = "AnvilContributesToLineMarker"

    override fun getTooltipText(targetClass: UClass): String {
        return "Navigate to component where ${targetClass.name} is contributed"
    }
}
