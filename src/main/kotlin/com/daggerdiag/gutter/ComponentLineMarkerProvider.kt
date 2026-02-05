package com.daggerdiag.gutter

import com.intellij.icons.AllIcons
import javax.swing.Icon

/**
 * Line marker provider for @Component annotations.
 *
 * Shows a gutter icon next to @Component-annotated interfaces/classes
 * that navigates to the dependency graph visualization.
 */
class ComponentLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.Component"

    override fun getName(): String = "Dagger @Component"

    override fun getIcon(): Icon = AllIcons.Nodes.Class

    override fun getId(): String = "DaggerComponentLineMarker"
}
