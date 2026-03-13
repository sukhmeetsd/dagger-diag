package com.daggerdiag.toolwindow

import com.daggerdiag.models.DaggerGraph
import com.daggerdiag.services.DaggerAnalysisService
import com.daggerdiag.ui.createGraphScrollPane
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * Factory for creating the Dagger Diagram tool window
 */
class DaggerDiagramToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowContent = DaggerDiagramToolWindowContent(project)
        val content = ContentFactory.getInstance().createContent(
            toolWindowContent.getContent(),
            "",
            false
        )
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean = true
}

/**
 * Content for the tool window
 */
class DaggerDiagramToolWindowContent(private val project: Project) {

    private val mainPanel = JBPanel<JBPanel<*>>(BorderLayout())
    private val analysisService = DaggerAnalysisService.getInstance(project)

    init {
        setupUI()
    }

    fun getContent(): JPanel = mainPanel

    private fun setupUI() {
        // Show initial state
        val cachedGraph = analysisService.getGraph()

        if (cachedGraph != null) {
            showGraph(cachedGraph)
        } else {
            showWelcomeScreen()
        }
    }

    private fun showWelcomeScreen() {
        mainPanel.removeAll()

        val welcomePanel = JBPanel<JBPanel<*>>()
        welcomePanel.layout = BoxLayout(welcomePanel, BoxLayout.Y_AXIS)

        val titleLabel = JBLabel("Dagger Dependency Visualizer", SwingConstants.CENTER)
        titleLabel.font = titleLabel.font.deriveFont(18f)
        titleLabel.alignmentX = JPanel.CENTER_ALIGNMENT

        val descLabel = JBLabel("Analyze your project to visualize Dagger dependencies", SwingConstants.CENTER)
        descLabel.alignmentX = JPanel.CENTER_ALIGNMENT

        val analyzeButton = JButton("Analyze Project")
        analyzeButton.alignmentX = JPanel.CENTER_ALIGNMENT
        analyzeButton.addActionListener {
            analyzeProject()
        }

        welcomePanel.add(JPanel().apply { add(titleLabel) })
        welcomePanel.add(JPanel().apply { add(descLabel) })
        welcomePanel.add(JPanel().apply { add(analyzeButton) })

        mainPanel.add(welcomePanel, BorderLayout.CENTER)
        mainPanel.revalidate()
        mainPanel.repaint()
    }

    private fun showLoadingScreen() {
        mainPanel.removeAll()

        val loadingPanel = JBPanel<JBPanel<*>>()
        loadingPanel.layout = BoxLayout(loadingPanel, BoxLayout.Y_AXIS)

        val label = JBLabel("Analyzing Dagger dependencies...", SwingConstants.CENTER)
        label.alignmentX = JPanel.CENTER_ALIGNMENT

        loadingPanel.add(JPanel().apply { add(label) })
        mainPanel.add(loadingPanel, BorderLayout.CENTER)
        mainPanel.revalidate()
        mainPanel.repaint()
    }

    private fun showGraph(graph: DaggerGraph) {
        mainPanel.removeAll()

        // Add toolbar
        val toolbar = createToolbar()
        mainPanel.add(toolbar, BorderLayout.NORTH)

        // Add graph visualization
        if (graph.components.isEmpty() && graph.modules.isEmpty()) {
            val noDataLabel = JBLabel("No Dagger components or modules found in project", SwingConstants.CENTER)
            mainPanel.add(noDataLabel, BorderLayout.CENTER)
        } else {
            val graphScrollPane = createGraphScrollPane(project, graph)
            mainPanel.add(graphScrollPane, BorderLayout.CENTER)
        }

        mainPanel.revalidate()
        mainPanel.repaint()
    }

    private fun createToolbar(): JPanel {
        val toolbar = JBPanel<JBPanel<*>>(BorderLayout())

        val refreshButton = JButton("Refresh")
        refreshButton.addActionListener {
            analyzeProject()
        }

        val statsLabel = analysisService.getGraph()?.let { graph ->
            JBLabel(
                "Components: ${graph.components.size} | " +
                        "Modules: ${graph.modules.size} | " +
                        "Provisions: ${graph.provisions.size} | " +
                        "Injections: ${graph.injections.size}"
            )
        } ?: JBLabel("")

        toolbar.add(refreshButton, BorderLayout.WEST)
        toolbar.add(statsLabel, BorderLayout.CENTER)

        return toolbar
    }

    private fun analyzeProject() {
        showLoadingScreen()

        analysisService.analyzeAsync()
            .thenAccept { graph ->
                javax.swing.SwingUtilities.invokeLater {
                    showGraph(graph)
                }
            }
            .exceptionally { throwable ->
                javax.swing.SwingUtilities.invokeLater {
                    showError(throwable.message ?: "Unknown error occurred")
                }
                null
            }
    }

    private fun showError(message: String) {
        mainPanel.removeAll()

        val errorPanel = JBPanel<JBPanel<*>>()
        errorPanel.layout = BoxLayout(errorPanel, BoxLayout.Y_AXIS)

        val errorLabel = JBLabel("Error: $message", SwingConstants.CENTER)
        errorLabel.alignmentX = JPanel.CENTER_ALIGNMENT

        val retryButton = JButton("Retry")
        retryButton.alignmentX = JPanel.CENTER_ALIGNMENT
        retryButton.addActionListener {
            analyzeProject()
        }

        errorPanel.add(JPanel().apply { add(errorLabel) })
        errorPanel.add(JPanel().apply { add(retryButton) })

        mainPanel.add(errorPanel, BorderLayout.CENTER)
        mainPanel.revalidate()
        mainPanel.repaint()
    }
}
