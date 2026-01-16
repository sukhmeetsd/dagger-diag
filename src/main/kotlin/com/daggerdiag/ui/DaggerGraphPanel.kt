package com.daggerdiag.ui

import com.daggerdiag.models.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * Custom panel for rendering Dagger dependency graph
 */
class DaggerGraphPanel(
    private val project: Project,
    private val graph: DaggerGraph
) : JPanel() {

    private val nodePositions = mutableMapOf<DaggerNode, Point>()
    private val nodeShapes = mutableMapOf<DaggerNode, Shape>()
    private var hoveredNode: DaggerNode? = null
    private var selectedNode: DaggerNode? = null

    companion object {
        private const val NODE_RADIUS = 50
        private const val NODE_SPACING = 150
        private const val PADDING = 100

        private val COMPONENT_COLOR = JBColor(Color(108, 158, 248), Color(72, 118, 208))
        private val MODULE_COLOR = JBColor(Color(102, 187, 106), Color(62, 147, 66))
        private val PROVISION_COLOR = JBColor(Color(255, 167, 38), Color(215, 127, 0))
        private val INJECTION_COLOR = JBColor(Color(239, 83, 80), Color(199, 43, 40))
        private val EDGE_COLOR = JBColor(Color(150, 150, 150), Color(100, 100, 100))
        private val HOVER_COLOR = JBColor(Color(255, 235, 59), Color(215, 195, 19))
        private val SELECTED_COLOR = JBColor(Color(33, 150, 243), Color(13, 110, 203))
    }

    init {
        background = JBColor.background()
        calculateLayout()
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                handleClick(e.point)
            }

            override fun mouseMoved(e: MouseEvent) {
                handleHover(e.point)
            }
        })

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                handleHover(e.point)
            }
        })
    }

    /**
     * Calculate layout positions for all nodes
     */
    private fun calculateLayout() {
        val allNodes = mutableListOf<DaggerNode>()
        allNodes.addAll(graph.components)
        allNodes.addAll(graph.modules)
        allNodes.addAll(graph.provisions)
        allNodes.addAll(graph.injections)

        if (allNodes.isEmpty()) return

        // Use a hierarchical layout
        var currentY = PADDING
        var currentX = PADDING

        // Layout components at the top
        graph.components.forEachIndexed { index, component ->
            val x = PADDING + (index % 3) * (NODE_SPACING * 2)
            val y = currentY + (index / 3) * (NODE_SPACING * 2)
            nodePositions[component] = Point(x, y)
        }

        if (graph.components.isNotEmpty()) {
            currentY += ((graph.components.size + 2) / 3) * (NODE_SPACING * 2) + NODE_SPACING
        }

        // Layout modules below components
        graph.modules.forEachIndexed { index, module ->
            val x = PADDING + (index % 4) * (NODE_SPACING + 80)
            val y = currentY + (index / 4) * NODE_SPACING
            nodePositions[module] = Point(x, y)
        }

        if (graph.modules.isNotEmpty()) {
            currentY += ((graph.modules.size + 3) / 4) * NODE_SPACING + NODE_SPACING
        }

        // Layout provisions below modules
        graph.provisions.forEachIndexed { index, provision ->
            val x = PADDING + (index % 5) * 140
            val y = currentY + (index / 5) * 120
            nodePositions[provision] = Point(x, y)
        }

        if (graph.provisions.isNotEmpty()) {
            currentY += ((graph.provisions.size + 4) / 5) * 120 + NODE_SPACING
        }

        // Layout injections at the bottom
        graph.injections.forEachIndexed { index, injection ->
            val x = PADDING + (index % 6) * 120
            val y = currentY + (index / 6) * 100
            nodePositions[injection] = Point(x, y)
        }

        // Calculate preferred size
        val maxX = nodePositions.values.maxOfOrNull { it.x } ?: 800
        val maxY = nodePositions.values.maxOfOrNull { it.y } ?: 600
        preferredSize = Dimension(maxX + PADDING * 2, maxY + PADDING * 2)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // Draw edges first
        drawEdges(g2d)

        // Draw nodes
        drawNodes(g2d)

        // Draw legend
        drawLegend(g2d)
    }

    /**
     * Draw all edges
     */
    private fun drawEdges(g2d: Graphics2D) {
        g2d.color = EDGE_COLOR
        g2d.stroke = BasicStroke(2f)

        graph.edges.forEach { edge ->
            val fromPos = nodePositions[edge.from]
            val toPos = nodePositions[edge.to]

            if (fromPos != null && toPos != null) {
                // Draw line
                g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y)

                // Draw arrow head
                drawArrowHead(g2d, fromPos, toPos)

                // Draw label if present
                edge.label?.let { label ->
                    val midX = (fromPos.x + toPos.x) / 2
                    val midY = (fromPos.y + toPos.y) / 2
                    g2d.color = JBColor.foreground()
                    g2d.font = Font("Arial", Font.PLAIN, 10)
                    g2d.drawString(label, midX + 5, midY - 5)
                    g2d.color = EDGE_COLOR
                }
            }
        }
    }

    /**
     * Draw arrow head at the end of an edge
     */
    private fun drawArrowHead(g2d: Graphics2D, from: Point, to: Point) {
        val angle = kotlin.math.atan2((to.y - from.y).toDouble(), (to.x - from.x).toDouble())
        val arrowLength = 10
        val arrowWidth = 5

        val x1 = to.x - arrowLength * cos(angle - PI / 6)
        val y1 = to.y - arrowLength * sin(angle - PI / 6)
        val x2 = to.x - arrowLength * cos(angle + PI / 6)
        val y2 = to.y - arrowLength * sin(angle + PI / 6)

        g2d.drawLine(to.x, to.y, x1.toInt(), y1.toInt())
        g2d.drawLine(to.x, to.y, x2.toInt(), y2.toInt())
    }

    /**
     * Draw all nodes
     */
    private fun drawNodes(g2d: Graphics2D) {
        nodeShapes.clear()

        // Draw each type of node
        graph.components.forEach { drawNode(g2d, it, COMPONENT_COLOR, "C") }
        graph.modules.forEach { drawNode(g2d, it, MODULE_COLOR, "M") }
        graph.provisions.forEach { drawNode(g2d, it, PROVISION_COLOR, "P") }
        graph.injections.forEach { drawNode(g2d, it, INJECTION_COLOR, "I") }
    }

    /**
     * Draw a single node
     */
    private fun drawNode(g2d: Graphics2D, node: DaggerNode, color: Color, typeLabel: String) {
        val pos = nodePositions[node] ?: return

        val shape = when (node) {
            is ComponentNode -> Rectangle2D.Double(
                (pos.x - NODE_RADIUS).toDouble(),
                (pos.y - NODE_RADIUS).toDouble(),
                (NODE_RADIUS * 2).toDouble(),
                (NODE_RADIUS * 2).toDouble()
            )
            else -> Ellipse2D.Double(
                (pos.x - NODE_RADIUS).toDouble(),
                (pos.y - NODE_RADIUS).toDouble(),
                (NODE_RADIUS * 2).toDouble(),
                (NODE_RADIUS * 2).toDouble()
            )
        }

        nodeShapes[node] = shape

        // Fill shape
        g2d.color = when {
            node == selectedNode -> SELECTED_COLOR
            node == hoveredNode -> HOVER_COLOR
            else -> color
        }
        g2d.fill(shape)

        // Draw border
        g2d.color = JBColor.foreground()
        g2d.stroke = BasicStroke(2f)
        g2d.draw(shape)

        // Draw text
        g2d.font = Font("Arial", Font.BOLD, 12)
        val fm = g2d.fontMetrics
        val text = node.name
        val textWidth = fm.stringWidth(text)
        val textHeight = fm.height

        // Draw type label
        g2d.drawString(typeLabel, pos.x - 5, pos.y - 10)

        // Draw node name
        g2d.font = Font("Arial", Font.PLAIN, 11)
        val lines = wrapText(text, 15)
        var yOffset = pos.y + textHeight / 2 - (lines.size * textHeight) / 2
        lines.forEach { line ->
            val lineWidth = g2d.fontMetrics.stringWidth(line)
            g2d.drawString(line, pos.x - lineWidth / 2, yOffset)
            yOffset += textHeight
        }
    }

    /**
     * Wrap text to fit in node
     */
    private fun wrapText(text: String, maxLength: Int): List<String> {
        if (text.length <= maxLength) return listOf(text)

        val words = text.split(" ", "_", ".")
        val lines = mutableListOf<String>()
        var currentLine = ""

        words.forEach { word ->
            if (currentLine.length + word.length <= maxLength) {
                currentLine += if (currentLine.isEmpty()) word else " $word"
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    /**
     * Draw legend
     */
    private fun drawLegend(g2d: Graphics2D) {
        val legendX = width - 200
        val legendY = 20

        g2d.color = JBColor.background()
        g2d.fillRect(legendX - 10, legendY - 10, 190, 140)
        g2d.color = JBColor.foreground()
        g2d.drawRect(legendX - 10, legendY - 10, 190, 140)

        g2d.font = Font("Arial", Font.BOLD, 12)
        g2d.drawString("Legend", legendX, legendY + 10)

        val items = listOf(
            Triple("Component", COMPONENT_COLOR, true),
            Triple("Module", MODULE_COLOR, false),
            Triple("Provides", PROVISION_COLOR, false),
            Triple("Injection", INJECTION_COLOR, false)
        )

        var y = legendY + 30
        items.forEach { (label, color, isSquare) ->
            g2d.color = color
            if (isSquare) {
                g2d.fillRect(legendX, y - 10, 15, 15)
            } else {
                g2d.fillOval(legendX, y - 10, 15, 15)
            }

            g2d.color = JBColor.foreground()
            g2d.font = Font("Arial", Font.PLAIN, 11)
            g2d.drawString(label, legendX + 25, y + 3)

            y += 25
        }
    }

    /**
     * Handle mouse click on a node
     */
    private fun handleClick(point: Point) {
        val clickedNode = findNodeAtPoint(point)

        if (clickedNode != null) {
            selectedNode = clickedNode
            navigateToSource(clickedNode)
            repaint()
        }
    }

    /**
     * Handle mouse hover over a node
     */
    private fun handleHover(point: Point) {
        val hoveredNodeNew = findNodeAtPoint(point)

        if (hoveredNodeNew != hoveredNode) {
            hoveredNode = hoveredNodeNew
            cursor = if (hoveredNode != null) {
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            } else {
                Cursor.getDefaultCursor()
            }
            repaint()
        }
    }

    /**
     * Find node at a given point
     */
    private fun findNodeAtPoint(point: Point): DaggerNode? {
        return nodeShapes.entries.firstOrNull { (_, shape) ->
            shape.contains(point)
        }?.key
    }

    /**
     * Navigate to the source code of a node
     */
    private fun navigateToSource(node: DaggerNode) {
        val filePath = node.filePath ?: return
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath) ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)

        fileEditorManager.openFile(virtualFile, true)

        // TODO: Navigate to specific line number
        // This requires accessing the editor and scrolling to the line
    }
}

/**
 * Create a scrollable wrapper for the graph panel
 */
fun createGraphScrollPane(project: Project, graph: DaggerGraph): JBScrollPane {
    val graphPanel = DaggerGraphPanel(project, graph)
    return JBScrollPane(graphPanel).apply {
        horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    }
}
