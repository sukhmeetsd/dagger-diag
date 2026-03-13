package com.daggerdiag.gutter

import com.intellij.icons.AllIcons
import javax.swing.Icon

/**
 * Line marker provider for @Subcomponent annotations.
 */
class SubcomponentLineMarkerProvider : DaggerLineMarkerProvider() {

    override val annotationFqn: String = "dagger.Subcomponent"

    override fun getName(): String = "Dagger @Subcomponent"

    override fun getIcon(): Icon = AllIcons.Hierarchy.Subtypes

    override fun getId(): String = "DaggerSubcomponentLineMarker"
}
