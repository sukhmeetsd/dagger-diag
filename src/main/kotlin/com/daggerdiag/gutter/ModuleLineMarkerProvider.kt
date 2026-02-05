package com.daggerdiag.gutter

import com.intellij.icons.AllIcons
import javax.swing.Icon

/**
 * Line marker provider for @Module annotations.
 */
class ModuleLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.Module"

    override fun getName(): String = "Dagger @Module"

    override fun getIcon(): Icon = AllIcons.Nodes.Module

    override fun getId(): String = "DaggerModuleLineMarker"
}
