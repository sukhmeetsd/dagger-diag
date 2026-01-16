package com.daggerdiag.models

import com.intellij.psi.PsiElement

/**
 * Represents a node in the Dagger dependency graph
 */
sealed class DaggerNode {
    abstract val name: String
    abstract val qualifiedName: String
    abstract val psiElement: PsiElement?
    abstract val filePath: String?
    abstract val lineNumber: Int?
}

/**
 * Represents a Dagger Component
 */
data class ComponentNode(
    override val name: String,
    override val qualifiedName: String,
    override val psiElement: PsiElement?,
    override val filePath: String?,
    override val lineNumber: Int?,
    val modules: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val scope: String? = null
) : DaggerNode()

/**
 * Represents a Dagger Module
 */
data class ModuleNode(
    override val name: String,
    override val qualifiedName: String,
    override val psiElement: PsiElement?,
    override val filePath: String?,
    override val lineNumber: Int?,
    val includes: List<String> = emptyList(),
    val provides: List<ProvisionNode> = emptyList()
) : DaggerNode()

/**
 * Represents a @Provides method or @Inject constructor
 */
data class ProvisionNode(
    override val name: String,
    override val qualifiedName: String,
    override val psiElement: PsiElement?,
    override val filePath: String?,
    override val lineNumber: Int?,
    val returnType: String,
    val parameters: List<DependencyParameter> = emptyList(),
    val scope: String? = null,
    val qualifier: String? = null
) : DaggerNode()

/**
 * Represents an injection point (@Inject field or constructor parameter)
 */
data class InjectionNode(
    override val name: String,
    override val qualifiedName: String,
    override val psiElement: PsiElement?,
    override val filePath: String?,
    override val lineNumber: Int?,
    val type: String,
    val qualifier: String? = null,
    val containingClass: String
) : DaggerNode()

/**
 * Represents a dependency parameter in a @Provides method
 */
data class DependencyParameter(
    val name: String,
    val type: String,
    val qualifier: String? = null
)

/**
 * Represents an edge (relationship) in the Dagger graph
 */
data class DaggerEdge(
    val from: DaggerNode,
    val to: DaggerNode,
    val type: EdgeType,
    val label: String? = null
)

/**
 * Types of relationships in the Dagger graph
 */
enum class EdgeType {
    COMPONENT_MODULE,      // Component includes Module
    MODULE_INCLUDES,       // Module includes another Module
    PROVIDES_DEPENDENCY,   // Module provides a dependency
    CONSUMES_DEPENDENCY,   // Class/Method consumes a dependency
    COMPONENT_DEPENDENCY,  // Component depends on another Component
    SUBCOMPONENT          // Subcomponent relationship
}

/**
 * Represents the complete Dagger dependency graph
 */
data class DaggerGraph(
    val components: List<ComponentNode> = emptyList(),
    val modules: List<ModuleNode> = emptyList(),
    val provisions: List<ProvisionNode> = emptyList(),
    val injections: List<InjectionNode> = emptyList(),
    val edges: List<DaggerEdge> = emptyList()
) {
    /**
     * Find all provisions that provide a specific type
     */
    fun findProvisionsForType(type: String): List<ProvisionNode> {
        return provisions.filter { it.returnType == type }
    }

    /**
     * Find all injections of a specific type
     */
    fun findInjectionsForType(type: String): List<InjectionNode> {
        return injections.filter { it.type == type }
    }

    /**
     * Get the module that provides a specific provision
     */
    fun getModuleForProvision(provision: ProvisionNode): ModuleNode? {
        return modules.find { module ->
            module.provides.any { it.qualifiedName == provision.qualifiedName }
        }
    }

    /**
     * Get all components that include a specific module
     */
    fun getComponentsForModule(module: ModuleNode): List<ComponentNode> {
        return components.filter { component ->
            component.modules.contains(module.qualifiedName)
        }
    }
}
