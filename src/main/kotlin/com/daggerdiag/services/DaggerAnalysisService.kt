package com.daggerdiag.services

import com.daggerdiag.analyzers.DaggerAnalyzer
import com.daggerdiag.models.DaggerGraph
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.application.ApplicationManager
import java.util.concurrent.CompletableFuture

/**
 * Service that manages Dagger analysis for a project
 */
@Service(Service.Level.PROJECT)
class DaggerAnalysisService(private val project: Project) {

    @Volatile
    private var cachedGraph: DaggerGraph? = null

    @Volatile
    private var analyzing = false

    /**
     * Get the cached Dagger graph or analyze if not available
     */
    fun getGraph(): DaggerGraph? {
        return cachedGraph
    }

    /**
     * Analyze the project asynchronously
     */
    fun analyzeAsync(): CompletableFuture<DaggerGraph> {
        val future = CompletableFuture<DaggerGraph>()

        if (analyzing) {
            // Return cached graph or wait
            cachedGraph?.let { future.complete(it) } ?: future.completeExceptionally(
                IllegalStateException("Analysis already in progress")
            )
            return future
        }

        analyzing = true

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val analyzer = DaggerAnalyzer(project)
                val graph = analyzer.analyze()
                cachedGraph = graph
                future.complete(graph)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            } finally {
                analyzing = false
            }
        }

        return future
    }

    /**
     * Clear the cached graph
     */
    fun invalidateCache() {
        cachedGraph = null
    }

    /**
     * Check if analysis is in progress
     */
    fun isAnalyzing(): Boolean = analyzing

    companion object {
        fun getInstance(project: Project): DaggerAnalysisService {
            return project.getService(DaggerAnalysisService::class.java)
        }
    }
}
