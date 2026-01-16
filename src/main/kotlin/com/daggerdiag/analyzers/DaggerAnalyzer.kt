package com.daggerdiag.analyzers

import com.daggerdiag.models.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.asJava.toLightElements
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName

/**
 * Analyzes a project to extract Dagger dependency information
 */
class DaggerAnalyzer(private val project: Project) {

    companion object {
        // Dagger annotation names
        private const val COMPONENT_ANNOTATION = "dagger.Component"
        private const val MODULE_ANNOTATION = "dagger.Module"
        private const val PROVIDES_ANNOTATION = "dagger.Provides"
        private const val INJECT_ANNOTATION = "javax.inject.Inject"
        private const val SUBCOMPONENT_ANNOTATION = "dagger.Subcomponent"
        private const val BINDS_ANNOTATION = "dagger.Binds"

        // Qualifier annotations
        private const val NAMED_ANNOTATION = "javax.inject.Named"
        private const val QUALIFIER_ANNOTATION = "javax.inject.Qualifier"

        // Scope annotations
        private const val SINGLETON_ANNOTATION = "javax.inject.Singleton"
        private const val SCOPE_ANNOTATION = "javax.inject.Scope"
    }

    /**
     * Analyze the entire project and build the Dagger graph
     */
    fun analyze(): DaggerGraph {
        val components = mutableListOf<ComponentNode>()
        val modules = mutableListOf<ModuleNode>()
        val provisions = mutableListOf<ProvisionNode>()
        val injections = mutableListOf<InjectionNode>()
        val edges = mutableListOf<DaggerEdge>()

        // Find all Dagger components
        findComponents().forEach { component ->
            components.add(component)

            // Add edges from component to its modules
            component.modules.forEach { moduleFqn ->
                val module = modules.find { it.qualifiedName == moduleFqn }
                if (module != null) {
                    edges.add(DaggerEdge(component, module, EdgeType.COMPONENT_MODULE))
                }
            }
        }

        // Find all Dagger modules
        findModules().forEach { module ->
            modules.add(module)
            provisions.addAll(module.provides)

            // Add edges for module includes
            module.includes.forEach { includedModuleFqn ->
                val includedModule = modules.find { it.qualifiedName == includedModuleFqn }
                if (includedModule != null) {
                    edges.add(DaggerEdge(module, includedModule, EdgeType.MODULE_INCLUDES))
                }
            }

            // Add edges for provisions
            module.provides.forEach { provision ->
                edges.add(DaggerEdge(module, provision, EdgeType.PROVIDES_DEPENDENCY, provision.returnType))
            }
        }

        // Find all injection points
        injections.addAll(findInjectionPoints())

        // Build dependency edges (provision -> injection)
        buildDependencyEdges(provisions, injections, edges)

        return DaggerGraph(components, modules, provisions, injections, edges)
    }

    /**
     * Find all @Component annotated classes
     */
    private fun findComponents(): List<ComponentNode> {
        val components = mutableListOf<ComponentNode>()

        val scope = GlobalSearchScope.projectScope(project)
        val ktFiles = PsiTreeUtil.findChildrenOfType(
            PsiManager.getInstance(project).findDirectory(project.baseDir),
            KtFile::class.java
        )

        ktFiles.forEach { file ->
            file.declarations.filterIsInstance<KtClass>().forEach { ktClass ->
                ktClass.annotationEntries.forEach { annotation ->
                    val fqName = annotation.typeReference?.text
                    if (fqName?.contains("Component") == true) {
                        components.add(parseComponent(ktClass))
                    }
                }
            }
        }

        return components
    }

    /**
     * Parse a @Component annotated class
     */
    private fun parseComponent(ktClass: KtClass): ComponentNode {
        val name = ktClass.name ?: "Unknown"
        val qualifiedName = ktClass.fqName?.asString() ?: name
        val filePath = ktClass.containingFile.virtualFile?.path
        val lineNumber = getLineNumber(ktClass)

        val modules = mutableListOf<String>()
        val dependencies = mutableListOf<String>()
        var scope: String? = null

        // Extract modules from annotation
        ktClass.annotationEntries.forEach { annotation ->
            if (annotation.shortName?.asString()?.contains("Component") == true) {
                annotation.valueArguments.forEach { arg ->
                    when (arg.getArgumentName()?.asName?.asString()) {
                        "modules" -> {
                            extractClassReferences(arg.getArgumentExpression()).forEach {
                                modules.add(it)
                            }
                        }
                        "dependencies" -> {
                            extractClassReferences(arg.getArgumentExpression()).forEach {
                                dependencies.add(it)
                            }
                        }
                    }
                }
            }
        }

        // Extract scope
        scope = findScope(ktClass)

        return ComponentNode(name, qualifiedName, ktClass, filePath, lineNumber, modules, dependencies, scope)
    }

    /**
     * Find all @Module annotated classes
     */
    private fun findModules(): List<ModuleNode> {
        val modules = mutableListOf<ModuleNode>()

        val ktFiles = PsiTreeUtil.findChildrenOfType(
            PsiManager.getInstance(project).findDirectory(project.baseDir),
            KtFile::class.java
        )

        ktFiles.forEach { file ->
            file.declarations.filterIsInstance<KtClass>().forEach { ktClass ->
                ktClass.annotationEntries.forEach { annotation ->
                    if (annotation.shortName?.asString()?.contains("Module") == true) {
                        modules.add(parseModule(ktClass))
                    }
                }
            }
        }

        return modules
    }

    /**
     * Parse a @Module annotated class
     */
    private fun parseModule(ktClass: KtClass): ModuleNode {
        val name = ktClass.name ?: "Unknown"
        val qualifiedName = ktClass.fqName?.asString() ?: name
        val filePath = ktClass.containingFile.virtualFile?.path
        val lineNumber = getLineNumber(ktClass)

        val includes = mutableListOf<String>()
        val provides = mutableListOf<ProvisionNode>()

        // Extract includes from annotation
        ktClass.annotationEntries.forEach { annotation ->
            if (annotation.shortName?.asString() == "Module") {
                annotation.valueArguments.forEach { arg ->
                    if (arg.getArgumentName()?.asName?.asString() == "includes") {
                        extractClassReferences(arg.getArgumentExpression()).forEach {
                            includes.add(it)
                        }
                    }
                }
            }
        }

        // Find all @Provides methods
        ktClass.declarations.filterIsInstance<KtNamedFunction>().forEach { function ->
            function.annotationEntries.forEach { annotation ->
                val annotationName = annotation.shortName?.asString()
                if (annotationName == "Provides" || annotationName == "Binds") {
                    provides.add(parseProvision(function))
                }
            }
        }

        return ModuleNode(name, qualifiedName, ktClass, filePath, lineNumber, includes, provides)
    }

    /**
     * Parse a @Provides method
     */
    private fun parseProvision(function: KtNamedFunction): ProvisionNode {
        val name = function.name ?: "Unknown"
        val qualifiedName = function.fqName?.asString() ?: name
        val filePath = function.containingFile.virtualFile?.path
        val lineNumber = getLineNumber(function)

        val returnType = function.typeReference?.text ?: "Unknown"
        val parameters = mutableListOf<DependencyParameter>()
        var scope: String? = null
        var qualifier: String? = null

        // Extract parameters
        function.valueParameters.forEach { param ->
            val paramName = param.name ?: "unknown"
            val paramType = param.typeReference?.text ?: "Unknown"
            val paramQualifier = findQualifier(param)
            parameters.add(DependencyParameter(paramName, paramType, paramQualifier))
        }

        // Extract scope and qualifier
        scope = findScope(function)
        qualifier = findQualifier(function)

        return ProvisionNode(name, qualifiedName, function, filePath, lineNumber, returnType, parameters, scope, qualifier)
    }

    /**
     * Find all @Inject annotated fields and constructors
     */
    private fun findInjectionPoints(): List<InjectionNode> {
        val injections = mutableListOf<InjectionNode>()

        val ktFiles = PsiTreeUtil.findChildrenOfType(
            PsiManager.getInstance(project).findDirectory(project.baseDir),
            KtFile::class.java
        )

        ktFiles.forEach { file ->
            file.declarations.filterIsInstance<KtClass>().forEach { ktClass ->
                val className = ktClass.fqName?.asString() ?: ktClass.name ?: "Unknown"

                // Check constructor injection
                ktClass.primaryConstructor?.annotationEntries?.forEach { annotation ->
                    if (annotation.shortName?.asString() == "Inject") {
                        ktClass.primaryConstructor?.valueParameters?.forEach { param ->
                            val paramName = param.name ?: "unknown"
                            val paramType = param.typeReference?.text ?: "Unknown"
                            val qualifier = findQualifier(param)
                            val filePath = param.containingFile.virtualFile?.path
                            val lineNumber = getLineNumber(param)

                            injections.add(
                                InjectionNode(
                                    paramName,
                                    "$className.$paramName",
                                    param,
                                    filePath,
                                    lineNumber,
                                    paramType,
                                    qualifier,
                                    className
                                )
                            )
                        }
                    }
                }

                // Check field injection
                ktClass.declarations.filterIsInstance<KtProperty>().forEach { property ->
                    property.annotationEntries.forEach { annotation ->
                        if (annotation.shortName?.asString() == "Inject") {
                            val propName = property.name ?: "unknown"
                            val propType = property.typeReference?.text ?: "Unknown"
                            val qualifier = findQualifier(property)
                            val filePath = property.containingFile.virtualFile?.path
                            val lineNumber = getLineNumber(property)

                            injections.add(
                                InjectionNode(
                                    propName,
                                    "$className.$propName",
                                    property,
                                    filePath,
                                    lineNumber,
                                    propType,
                                    qualifier,
                                    className
                                )
                            )
                        }
                    }
                }
            }
        }

        return injections
    }

    /**
     * Build edges connecting provisions to their consumers
     */
    private fun buildDependencyEdges(
        provisions: List<ProvisionNode>,
        injections: List<InjectionNode>,
        edges: MutableList<DaggerEdge>
    ) {
        // Connect provisions to injections of the same type
        provisions.forEach { provision ->
            injections.filter { it.type == provision.returnType }.forEach { injection ->
                edges.add(DaggerEdge(provision, injection, EdgeType.CONSUMES_DEPENDENCY, provision.returnType))
            }
        }

        // Connect provisions that depend on other provisions
        provisions.forEach { consumer ->
            consumer.parameters.forEach { param ->
                provisions.filter { it.returnType == param.type }.forEach { provider ->
                    edges.add(DaggerEdge(provider, consumer, EdgeType.CONSUMES_DEPENDENCY, param.type))
                }
            }
        }
    }

    /**
     * Extract class references from an expression (e.g., [Foo::class, Bar::class])
     */
    private fun extractClassReferences(expression: KtExpression?): List<String> {
        val classes = mutableListOf<String>()

        when (expression) {
            is KtCollectionLiteralExpression -> {
                expression.getInnerExpressions().forEach { inner ->
                    if (inner is KtClassLiteralExpression) {
                        inner.receiverExpression?.text?.let { classes.add(it) }
                    }
                }
            }
            is KtClassLiteralExpression -> {
                expression.receiverExpression?.text?.let { classes.add(it) }
            }
        }

        return classes
    }

    /**
     * Find scope annotation on an element
     */
    private fun findScope(element: KtAnnotated): String? {
        element.annotationEntries.forEach { annotation ->
            val name = annotation.shortName?.asString()
            if (name == "Singleton" || name?.endsWith("Scope") == true) {
                return name
            }
        }
        return null
    }

    /**
     * Find qualifier annotation on an element
     */
    private fun findQualifier(element: KtAnnotated): String? {
        element.annotationEntries.forEach { annotation ->
            val name = annotation.shortName?.asString()
            if (name == "Named") {
                // Extract value from @Named annotation
                annotation.valueArguments.firstOrNull()?.let { arg ->
                    return arg.getArgumentExpression()?.text?.trim('"')
                }
            }
            // Check for custom qualifiers
            if (name != null && name != "Inject" && name != "Provides") {
                return name
            }
        }
        return null
    }

    /**
     * Get line number of a PSI element
     */
    private fun getLineNumber(element: PsiElement): Int? {
        val document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile)
        return document?.getLineNumber(element.textOffset)?.plus(1)
    }
}
