package com.daggerdiag.analyzers

import com.daggerdiag.models.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*

/**
 * Analyzes a project to extract Dagger dependency information
 * Compatible with Kotlin K2 mode
 */
class DaggerAnalyzer(private val project: Project) {

    companion object {
        // Dagger annotation names
        private const val COMPONENT_ANNOTATION = "Component"
        private const val MODULE_ANNOTATION = "Module"
        private const val PROVIDES_ANNOTATION = "Provides"
        private const val INJECT_ANNOTATION = "Inject"
        private const val SUBCOMPONENT_ANNOTATION = "Subcomponent"
        private const val BINDS_ANNOTATION = "Binds"

        // Hilt annotation names
        private const val HILT_ANDROID_APP = "HiltAndroidApp"
        private const val ANDROID_ENTRY_POINT = "AndroidEntryPoint"
        private const val HILT_VIEW_MODEL = "HiltViewModel"
        private const val DEFINE_COMPONENT = "DefineComponent"
        private const val INSTALL_IN = "InstallIn"

        // Anvil annotation names
        private const val MERGE_COMPONENT = "MergeComponent"
        private const val MERGE_SUBCOMPONENT = "MergeSubcomponent"
        private const val CONTRIBUTES_TO = "ContributesTo"

        // Qualifier annotations
        private const val NAMED_ANNOTATION = "Named"
        private const val QUALIFIER_ANNOTATION = "Qualifier"

        // Scope annotations
        private const val SINGLETON_ANNOTATION = "Singleton"
        private const val SCOPE_ANNOTATION = "Scope"

        // Component-like annotations (Hilt, Anvil, etc.)
        private val COMPONENT_ANNOTATIONS = setOf(
            COMPONENT_ANNOTATION,
            SUBCOMPONENT_ANNOTATION,
            HILT_ANDROID_APP,
            ANDROID_ENTRY_POINT,
            DEFINE_COMPONENT,
            MERGE_COMPONENT,
            MERGE_SUBCOMPONENT
        )

        // Module-like annotations
        private val MODULE_ANNOTATIONS = setOf(
            MODULE_ANNOTATION,
            CONTRIBUTES_TO
        )
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

        // Find all Kotlin files in the project
        val kotlinFiles = findKotlinFiles()

        // Find all Dagger components
        kotlinFiles.forEach { file ->
            findComponentsInFile(file).forEach { component ->
                components.add(component)
            }
        }

        // Find all Dagger modules
        kotlinFiles.forEach { file ->
            findModulesInFile(file).forEach { module ->
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
        }

        // Add edges from components to modules
        components.forEach { component ->
            component.modules.forEach { moduleFqn ->
                val module = modules.find { it.qualifiedName == moduleFqn }
                if (module != null) {
                    edges.add(DaggerEdge(component, module, EdgeType.COMPONENT_MODULE))
                }
            }
        }

        // Find all injection points
        kotlinFiles.forEach { file ->
            injections.addAll(findInjectionPointsInFile(file))
        }

        // Build dependency edges (provision -> injection)
        buildDependencyEdges(provisions, injections, edges)

        return DaggerGraph(components, modules, provisions, injections, edges)
    }

    /**
     * Find all Kotlin files in the project
     */
    private fun findKotlinFiles(): List<KtFile> {
        val kotlinFiles = mutableListOf<KtFile>()
        val psiManager = PsiManager.getInstance(project)

        // Try multiple approaches to find Kotlin files

        // Approach 1: Use FileTypeIndex with allScope (includes all project content)
        try {
            val allScope = GlobalSearchScope.allScope(project)
            val indexedFiles = FileTypeIndex.getFiles(KotlinFileType.INSTANCE, allScope)
            indexedFiles.forEach { virtualFile ->
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile is KtFile && !kotlinFiles.contains(psiFile)) {
                    kotlinFiles.add(psiFile)
                }
            }
        } catch (e: Exception) {
            // Fallback if FileTypeIndex fails
        }

        // Approach 2: Traverse project content roots directly
        if (kotlinFiles.isEmpty()) {
            val projectRootManager = ProjectRootManager.getInstance(project)

            // Search in content roots
            projectRootManager.contentRoots.forEach { contentRoot ->
                findKotlinFilesInDirectory(contentRoot, psiManager, kotlinFiles)
            }

            // Also search in source roots
            projectRootManager.contentSourceRoots.forEach { sourceRoot ->
                findKotlinFilesInDirectory(sourceRoot, psiManager, kotlinFiles)
            }
        }

        // Approach 3: If still empty, try searching from project base directory
        if (kotlinFiles.isEmpty()) {
            project.basePath?.let { basePath ->
                val baseDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(basePath)
                baseDir?.let { findKotlinFilesInDirectory(it, psiManager, kotlinFiles) }
            }
        }

        return kotlinFiles.distinctBy { it.virtualFilePath }
    }

    /**
     * Recursively find Kotlin files in a directory
     */
    private fun findKotlinFilesInDirectory(
        directory: VirtualFile,
        psiManager: PsiManager,
        kotlinFiles: MutableList<KtFile>
    ) {
        VfsUtilCore.visitChildrenRecursively(directory, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (!file.isDirectory && file.extension == "kt") {
                    val psiFile = psiManager.findFile(file)
                    if (psiFile is KtFile) {
                        kotlinFiles.add(psiFile)
                    }
                }
                // Skip build directories and hidden directories
                if (file.isDirectory && (file.name == "build" || file.name.startsWith("."))) {
                    return false
                }
                return true
            }
        })
    }

    /**
     * Find all @Component, @HiltAndroidApp, @AndroidEntryPoint, @MergeComponent annotated classes in a file
     */
    private fun findComponentsInFile(file: KtFile): List<ComponentNode> {
        val components = mutableListOf<ComponentNode>()

        file.declarations.filterIsInstance<KtClass>().forEach { ktClass ->
            ktClass.annotationEntries.forEach { annotation ->
                val annotationName = annotation.shortName?.asString()
                // Check if annotation is any component-like annotation
                if (COMPONENT_ANNOTATIONS.any { annotationName?.contains(it) == true }) {
                    components.add(parseComponent(ktClass))
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
        val qualifiedName = getQualifiedName(ktClass) ?: name
        val filePath = ktClass.containingFile.virtualFile?.path
        val lineNumber = getLineNumber(ktClass)

        val modules = mutableListOf<String>()
        val dependencies = mutableListOf<String>()
        var scope: String? = null

        // Extract modules from annotation
        ktClass.annotationEntries.forEach { annotation ->
            val annotationName = annotation.shortName?.asString()
            // Check all component-like annotations
            if (COMPONENT_ANNOTATIONS.any { annotationName?.contains(it) == true }) {
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
     * Find all @Module and @ContributesTo annotated classes in a file
     */
    private fun findModulesInFile(file: KtFile): List<ModuleNode> {
        val modules = mutableListOf<ModuleNode>()

        file.declarations.filterIsInstance<KtClass>().forEach { ktClass ->
            ktClass.annotationEntries.forEach { annotation ->
                val annotationName = annotation.shortName?.asString()
                // Check if annotation is any module-like annotation
                if (MODULE_ANNOTATIONS.any { annotationName?.contains(it) == true }) {
                    modules.add(parseModule(ktClass))
                }
            }
        }

        // Also check for object declarations (common in Kotlin modules)
        file.declarations.filterIsInstance<KtObjectDeclaration>().forEach { ktObject ->
            ktObject.annotationEntries.forEach { annotation ->
                val annotationName = annotation.shortName?.asString()
                if (MODULE_ANNOTATIONS.any { annotationName?.contains(it) == true }) {
                    // Convert KtObjectDeclaration to KtClass for parsing
                    // Objects are treated as singleton classes
                    modules.add(parseModuleFromObject(ktObject))
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
        val qualifiedName = getQualifiedName(ktClass) ?: name
        val filePath = ktClass.containingFile.virtualFile?.path
        val lineNumber = getLineNumber(ktClass)

        val includes = mutableListOf<String>()
        val provides = mutableListOf<ProvisionNode>()

        // Extract includes from annotation
        ktClass.annotationEntries.forEach { annotation ->
            val annotationName = annotation.shortName?.asString()
            if (MODULE_ANNOTATIONS.any { annotationName?.contains(it) == true }) {
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
                if (annotationName == PROVIDES_ANNOTATION || annotationName == BINDS_ANNOTATION) {
                    provides.add(parseProvision(function))
                }
            }
        }

        return ModuleNode(name, qualifiedName, ktClass, filePath, lineNumber, includes, provides)
    }

    /**
     * Parse a @Module annotated object declaration
     */
    private fun parseModuleFromObject(ktObject: KtObjectDeclaration): ModuleNode {
        val name = ktObject.name ?: "Unknown"
        val qualifiedName = getQualifiedName(ktObject) ?: name
        val filePath = ktObject.containingFile.virtualFile?.path
        val lineNumber = getLineNumber(ktObject)

        val includes = mutableListOf<String>()
        val provides = mutableListOf<ProvisionNode>()

        // Extract includes from annotation
        ktObject.annotationEntries.forEach { annotation ->
            val annotationName = annotation.shortName?.asString()
            if (MODULE_ANNOTATIONS.any { annotationName?.contains(it) == true }) {
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
        ktObject.declarations.filterIsInstance<KtNamedFunction>().forEach { function ->
            function.annotationEntries.forEach { annotation ->
                val annotationName = annotation.shortName?.asString()
                if (annotationName == PROVIDES_ANNOTATION || annotationName == BINDS_ANNOTATION) {
                    provides.add(parseProvision(function))
                }
            }
        }

        // Use ktObject as the element (note: ModuleNode expects KtClass, but we can pass any PsiElement)
        return ModuleNode(name, qualifiedName, ktObject as PsiElement, filePath, lineNumber, includes, provides)
    }

    /**
     * Parse a @Provides method
     */
    private fun parseProvision(function: KtNamedFunction): ProvisionNode {
        val name = function.name ?: "Unknown"
        val qualifiedName = getQualifiedName(function) ?: name
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
     * Find all @Inject annotated fields and constructors in a file
     */
    private fun findInjectionPointsInFile(file: KtFile): List<InjectionNode> {
        val injections = mutableListOf<InjectionNode>()

        file.declarations.filterIsInstance<KtClass>().forEach { ktClass ->
            val className = getQualifiedName(ktClass) ?: ktClass.name ?: "Unknown"

            // Check constructor injection
            ktClass.primaryConstructor?.annotationEntries?.forEach { annotation ->
                if (annotation.shortName?.asString() == INJECT_ANNOTATION) {
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
                    if (annotation.shortName?.asString() == INJECT_ANNOTATION) {
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
            if (name == SINGLETON_ANNOTATION || name?.endsWith("Scope") == true) {
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
            if (name == NAMED_ANNOTATION) {
                // Extract value from @Named annotation
                annotation.valueArguments.firstOrNull()?.let { arg ->
                    return arg.getArgumentExpression()?.text?.trim('"')
                }
            }
            // Check for custom qualifiers
            if (name != null && name != INJECT_ANNOTATION && name != PROVIDES_ANNOTATION) {
                return name
            }
        }
        return null
    }

    /**
     * Get qualified name of a PSI element (K2-compatible)
     */
    private fun getQualifiedName(element: PsiElement): String? {
        return when (element) {
            is KtClass -> {
                val packageName = (element.containingFile as? KtFile)?.packageFqName?.asString()
                val className = element.name
                if (packageName != null && className != null) {
                    "$packageName.$className"
                } else {
                    className
                }
            }
            is KtObjectDeclaration -> {
                val packageName = (element.containingFile as? KtFile)?.packageFqName?.asString()
                val objectName = element.name
                if (packageName != null && objectName != null) {
                    "$packageName.$objectName"
                } else {
                    objectName
                }
            }
            is KtNamedFunction -> {
                val containingClass = PsiTreeUtil.getParentOfType(element, KtClass::class.java)
                val containingObject = PsiTreeUtil.getParentOfType(element, KtObjectDeclaration::class.java)
                val classQualifiedName = containingClass?.let { getQualifiedName(it) }
                    ?: containingObject?.let { getQualifiedName(it) }
                val functionName = element.name
                if (classQualifiedName != null && functionName != null) {
                    "$classQualifiedName.$functionName"
                } else {
                    functionName
                }
            }
            else -> null
        }
    }

    /**
     * Get line number of a PSI element
     */
    private fun getLineNumber(element: PsiElement): Int? {
        val document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile)
        return document?.getLineNumber(element.textOffset)?.plus(1)
    }
}
