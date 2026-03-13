package com.daggerdiag.ui

import com.daggerdiag.models.*
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.*
import java.awt.event.*
import java.awt.geom.*
import javax.swing.JPanel
import javax.swing.ToolTipManager
import kotlin.math.*

/**
 * Custom panel for rendering Dagger dependency graph with zoom, pan, and improved layout
 * Prepared for future drag-and-drop functionality
 */
class DaggerGraphPanel(
    private val project: Project,
    private val graph: DaggerGraph
) : JPanel() {

    // Node positioning and rendering
    private val nodePositions = mutableMapOf<DaggerNode, Point2D.Double>()
    private val nodeShapes = mutableMapOf<DaggerNode, Shape>()
    private var hoveredNode: DaggerNode? = null
    private var selectedNode: DaggerNode? = null
    private val highlightedEdges = mutableSetOf<DaggerEdge>()
    private val highlightedNodes = mutableSetOf<DaggerNode>()

    // Zoom and pan state
    private var zoomLevel = 1.0
    private var panX = 0.0
    private var panY = 0.0
    private var lastMousePoint: Point? = null
    private var isPanning = false

    // Layout parameters (dynamic based on graph size)
    private var nodeRadius = 60.0
    private var horizontalSpacing = 200.0
    private var verticalSpacing = 180.0
    private val padding = 150.0

    companion object {
        private const val MIN_ZOOM = 0.1
        private const val MAX_ZOOM = 3.0
        private const val ZOOM_STEP = 0.1

        private val COMPONENT_COLOR = JBColor(Color(108, 158, 248), Color(72, 118, 208))
        private val MODULE_COLOR = JBColor(Color(102, 187, 106), Color(62, 147, 66))
        private val PROVISION_COLOR = JBColor(Color(255, 167, 38), Color(215, 127, 0))
        private val INJECTION_COLOR = JBColor(Color(239, 83, 80), Color(199, 43, 40))
        private val EDGE_COLOR = JBColor(Color(150, 150, 150, 128), Color(100, 100, 100, 128))
        private val HIGHLIGHTED_EDGE_COLOR = JBColor(Color(255, 152, 0, 230), Color(255, 167, 38, 230))
        private val HOVER_COLOR = JBColor(Color(255, 235, 59), Color(215, 195, 19))
        private val SELECTED_COLOR = JBColor(Color(33, 150, 243), Color(13, 110, 203))
        private val TEXT_BACKGROUND = JBColor(Color(255, 255, 255, 220), Color(60, 63, 65, 220))
    }

    init {
        background = JBColor.background()

        // Enable tooltips
        ToolTipManager.sharedInstance().registerComponent(this)

        // Calculate optimal layout
        calculateDynamicSpacing()
        calculateLayout()

        // Mouse listeners for interaction
        setupMouseListeners()
    }

    /**
     * Calculate spacing based on graph size to avoid congestion
     */
    private fun calculateDynamicSpacing() {
        val totalNodes = graph.components.size + graph.modules.size +
                        graph.provisions.size + graph.injections.size

        when {
            totalNodes > 200 -> {
                nodeRadius = 50.0
                horizontalSpacing = 160.0
                verticalSpacing = 150.0
            }
            totalNodes > 100 -> {
                nodeRadius = 55.0
                horizontalSpacing = 170.0
                verticalSpacing = 160.0
            }
            totalNodes > 50 -> {
                nodeRadius = 60.0
                horizontalSpacing = 190.0
                verticalSpacing = 170.0
            }
            totalNodes > 20 -> {
                nodeRadius = 65.0
                horizontalSpacing = 210.0
                verticalSpacing = 180.0
            }
            else -> {
                nodeRadius = 70.0
                horizontalSpacing = 240.0
                verticalSpacing = 200.0
            }
        }
    }

    /**
     * Calculate improved hierarchical layout
     */
    private fun calculateLayout() {
        val allNodes = graph.components + graph.modules + graph.provisions + graph.injections
        if (allNodes.isEmpty()) return

        var currentY = padding

        // Layer 1: Components (top)
        if (graph.components.isNotEmpty()) {
            val componentsPerRow = max(1, ceil(sqrt(graph.components.size.toDouble())).toInt())
            graph.components.forEachIndexed { index, component ->
                val col = index % componentsPerRow
                val row = index / componentsPerRow
                val x = padding + col * horizontalSpacing * 1.5
                val y = currentY + row * verticalSpacing * 1.2
                nodePositions[component] = Point2D.Double(x, y)
            }
            currentY += (ceil(graph.components.size.toDouble() / componentsPerRow) * verticalSpacing * 1.2) + verticalSpacing
        }

        // Layer 2: Modules
        if (graph.modules.isNotEmpty()) {
            val modulesPerRow = max(1, min(6, ceil(sqrt(graph.modules.size.toDouble() * 1.5)).toInt()))
            graph.modules.forEachIndexed { index, module ->
                val col = index % modulesPerRow
                val row = index / modulesPerRow
                val x = padding + col * horizontalSpacing
                val y = currentY + row * verticalSpacing
                nodePositions[module] = Point2D.Double(x, y)
            }
            currentY += (ceil(graph.modules.size.toDouble() / modulesPerRow) * verticalSpacing) + verticalSpacing * 1.5
        }

        // Layer 3: Provisions (can be many - use more columns)
        if (graph.provisions.isNotEmpty()) {
            // Calculate columns based on graph size - aim for roughly square layout
            val provisionsPerRow = when {
                graph.provisions.size > 200 -> min(20, ceil(sqrt(graph.provisions.size.toDouble() * 1.2)).toInt())
                graph.provisions.size > 100 -> min(15, ceil(sqrt(graph.provisions.size.toDouble() * 1.3)).toInt())
                graph.provisions.size > 50 -> min(12, ceil(sqrt(graph.provisions.size.toDouble() * 1.5)).toInt())
                else -> min(10, ceil(sqrt(graph.provisions.size.toDouble() * 2)).toInt())
            }
            graph.provisions.forEachIndexed { index, provision ->
                val col = index % provisionsPerRow
                val row = index / provisionsPerRow
                val x = padding + col * horizontalSpacing * 0.85
                val y = currentY + row * verticalSpacing * 0.8
                nodePositions[provision] = Point2D.Double(x, y)
            }
            currentY += (ceil(graph.provisions.size.toDouble() / provisionsPerRow) * verticalSpacing * 0.8) + verticalSpacing * 1.5
        }

        // Layer 4: Injections (bottom - often the most numerous)
        if (graph.injections.isNotEmpty()) {
            // Calculate columns based on graph size - use many more columns for large graphs
            val injectionsPerRow = when {
                graph.injections.size > 200 -> min(20, ceil(sqrt(graph.injections.size.toDouble() * 1.2)).toInt())
                graph.injections.size > 100 -> min(15, ceil(sqrt(graph.injections.size.toDouble() * 1.3)).toInt())
                graph.injections.size > 50 -> min(12, ceil(sqrt(graph.injections.size.toDouble() * 1.5)).toInt())
                else -> min(10, ceil(sqrt(graph.injections.size.toDouble() * 2)).toInt())
            }
            graph.injections.forEachIndexed { index, injection ->
                val col = index % injectionsPerRow
                val row = index / injectionsPerRow
                val x = padding + col * horizontalSpacing * 0.85
                val y = currentY + row * verticalSpacing * 0.8
                nodePositions[injection] = Point2D.Double(x, y)
            }
            currentY += (ceil(graph.injections.size.toDouble() / injectionsPerRow) * verticalSpacing * 0.8) + padding
        }

        // Calculate canvas size
        val maxX = nodePositions.values.maxOfOrNull { it.x } ?: 1000.0
        val maxY = nodePositions.values.maxOfOrNull { it.y } ?: 1000.0
        preferredSize = Dimension((maxX + padding * 2).toInt(), (maxY + padding * 2).toInt())
    }

    /**
     * Setup mouse listeners for zoom, pan, and interaction
     */
    private fun setupMouseListeners() {
        // Mouse wheel for zooming
        addMouseWheelListener { e ->
            val oldZoom = zoomLevel
            if (e.wheelRotation < 0) {
                zoomLevel = min(MAX_ZOOM, zoomLevel + ZOOM_STEP)
            } else {
                zoomLevel = max(MIN_ZOOM, zoomLevel - ZOOM_STEP)
            }

            // Zoom towards mouse position
            if (zoomLevel != oldZoom) {
                val scale = zoomLevel / oldZoom
                panX = e.x - scale * (e.x - panX)
                panY = e.y - scale * (e.y - panY)
                repaint()
            }
        }

        // Mouse drag for panning
        val mouseAdapter = object : MouseAdapter() {
            private var dragStartPoint: Point? = null
            private var clickedOnNode = false

            override fun mousePressed(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON2) {
                    // Middle mouse for panning
                    isPanning = true
                    lastMousePoint = e.point
                    cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
                } else if (e.button == MouseEvent.BUTTON1) {
                    // Left click - check if clicking on a node
                    dragStartPoint = e.point
                    val transformedPoint = Point2D.Double(
                        (e.point.x - panX) / zoomLevel,
                        (e.point.y - panY) / zoomLevel
                    )
                    clickedOnNode = findNodeAtPoint(transformedPoint) != null

                    if (!clickedOnNode) {
                        // Not on a node, prepare for panning
                        isPanning = true
                        lastMousePoint = e.point
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && dragStartPoint != null) {
                    val dx = e.point.x - dragStartPoint!!.x
                    val dy = e.point.y - dragStartPoint!!.y
                    val distance = sqrt((dx * dx + dy * dy).toDouble())

                    // If mouse didn't move much, treat as click
                    if (distance < 5.0 && clickedOnNode) {
                        handleClick(e.point)
                    }

                    dragStartPoint = null
                    clickedOnNode = false
                }

                if (isPanning) {
                    isPanning = false
                    lastMousePoint = null
                    cursor = Cursor.getDefaultCursor()
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (isPanning && lastMousePoint != null) {
                    val dx = e.x - lastMousePoint!!.x
                    val dy = e.y - lastMousePoint!!.y
                    panX += dx
                    panY += dy
                    lastMousePoint = e.point
                    repaint()
                }
            }

            override fun mouseMoved(e: MouseEvent) {
                handleHover(e.point)
            }
        }

        addMouseListener(mouseAdapter)
        addMouseMotionListener(mouseAdapter)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

        // Apply zoom and pan transformation
        val originalTransform = g2d.transform
        g2d.translate(panX, panY)
        g2d.scale(zoomLevel, zoomLevel)

        // Draw graph
        drawEdges(g2d)
        drawNodes(g2d)

        // Restore transform for UI elements
        g2d.transform = originalTransform

        // Draw UI elements (legend, zoom indicator)
        drawLegend(g2d)
        drawZoomIndicator(g2d)
    }

    /**
     * Draw edges with improved styling and hover highlighting
     */
    private fun drawEdges(g2d: Graphics2D) {
        // First draw normal edges
        g2d.color = EDGE_COLOR
        g2d.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

        graph.edges.forEach { edge ->
            if (!highlightedEdges.contains(edge)) {
                val fromPos = nodePositions[edge.from]
                val toPos = nodePositions[edge.to]

                if (fromPos != null && toPos != null) {
                    // Draw curved line for better visibility
                    val path = QuadCurve2D.Double()
                    val ctrlX = (fromPos.x + toPos.x) / 2
                    val ctrlY = (fromPos.y + toPos.y) / 2 + 20 // Slight curve
                    path.setCurve(fromPos.x, fromPos.y, ctrlX, ctrlY, toPos.x, toPos.y)
                    g2d.draw(path)

                    // Draw arrow head
                    drawArrowHead(g2d, fromPos, toPos)
                }
            }
        }

        // Then draw highlighted edges on top with different styling
        if (highlightedEdges.isNotEmpty()) {
            g2d.color = HIGHLIGHTED_EDGE_COLOR
            g2d.stroke = BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

            highlightedEdges.forEach { edge ->
                val fromPos = nodePositions[edge.from]
                val toPos = nodePositions[edge.to]

                if (fromPos != null && toPos != null) {
                    // Draw curved line with thicker stroke
                    val path = QuadCurve2D.Double()
                    val ctrlX = (fromPos.x + toPos.x) / 2
                    val ctrlY = (fromPos.y + toPos.y) / 2 + 20
                    path.setCurve(fromPos.x, fromPos.y, ctrlX, ctrlY, toPos.x, toPos.y)
                    g2d.draw(path)

                    // Draw larger arrow head
                    drawArrowHead(g2d, fromPos, toPos, larger = true)
                }
            }
        }
    }

    /**
     * Draw arrow head
     */
    private fun drawArrowHead(g2d: Graphics2D, from: Point2D.Double, to: Point2D.Double, larger: Boolean = false) {
        val angle = atan2(to.y - from.y, to.x - from.x)
        val arrowLength = if (larger) 16.0 else 12.0
        val arrowAngle = PI / 6

        val x1 = to.x - arrowLength * cos(angle - arrowAngle)
        val y1 = to.y - arrowLength * sin(angle - arrowAngle)
        val x2 = to.x - arrowLength * cos(angle + arrowAngle)
        val y2 = to.y - arrowLength * sin(angle + arrowAngle)

        val arrowHead = Path2D.Double()
        arrowHead.moveTo(to.x, to.y)
        arrowHead.lineTo(x1, y1)
        arrowHead.lineTo(x2, y2)
        arrowHead.closePath()

        g2d.fill(arrowHead)
    }

    /**
     * Draw nodes with improved labels
     */
    private fun drawNodes(g2d: Graphics2D) {
        nodeShapes.clear()

        // Draw in order: edges are already drawn, now nodes on top
        graph.components.forEach { drawNode(g2d, it, COMPONENT_COLOR) }
        graph.modules.forEach { drawNode(g2d, it, MODULE_COLOR) }
        graph.provisions.forEach { drawNode(g2d, it, PROVISION_COLOR) }
        graph.injections.forEach { drawNode(g2d, it, INJECTION_COLOR) }
    }

    /**
     * Draw a single node with improved text rendering
     */
    private fun drawNode(g2d: Graphics2D, node: DaggerNode, color: Color) {
        val pos = nodePositions[node] ?: return

        // Create shape
        val shape = when (node) {
            is ComponentNode -> RoundRectangle2D.Double(
                pos.x - nodeRadius, pos.y - nodeRadius,
                nodeRadius * 2, nodeRadius * 2,
                20.0, 20.0
            )
            else -> Ellipse2D.Double(
                pos.x - nodeRadius, pos.y - nodeRadius,
                nodeRadius * 2, nodeRadius * 2
            )
        }

        nodeShapes[node] = shape

        // Fill node
        g2d.color = when {
            node == selectedNode -> SELECTED_COLOR
            node == hoveredNode -> HOVER_COLOR
            highlightedNodes.contains(node) -> color.brighter()
            else -> color
        }
        g2d.fill(shape)

        // Draw border
        g2d.color = when {
            node == selectedNode -> SELECTED_COLOR.darker()
            node == hoveredNode -> HOVER_COLOR.darker()
            highlightedNodes.contains(node) -> HIGHLIGHTED_EDGE_COLOR
            else -> color.darker()
        }
        g2d.stroke = if (highlightedNodes.contains(node)) BasicStroke(3.5f) else BasicStroke(2.5f)
        g2d.draw(shape)

        // Draw label with background
        drawNodeLabel(g2d, node, pos)
    }

    /**
     * Draw node label with improved formatting and background
     */
    private fun drawNodeLabel(g2d: Graphics2D, node: DaggerNode, pos: Point2D.Double) {
        // Get display name
        val displayName = getNodeDisplayName(node)

        // Choose font size based on zoom
        val fontSize = (11 * min(1.2, zoomLevel)).toInt()
        g2d.font = Font("SansSerif", Font.BOLD, fontSize)

        val fm = g2d.fontMetrics
        // Increased max width from 1.8 to 2.2 times nodeRadius for better readability
        val lines = smartWrapText(displayName, fm, nodeRadius * 2.2)

        if (lines.isEmpty()) return

        // Calculate text block dimensions
        val lineHeight = fm.height
        val textBlockHeight = lines.size * lineHeight
        val maxLineWidth = lines.maxOf { fm.stringWidth(it) }

        // Draw semi-transparent background for readability
        val textBgPadding = 4.0
        val bgRect = Rectangle2D.Double(
            pos.x - maxLineWidth / 2 - textBgPadding,
            pos.y - textBlockHeight / 2 - textBgPadding,
            maxLineWidth + textBgPadding * 2,
            textBlockHeight + textBgPadding * 2
        )
        g2d.color = TEXT_BACKGROUND
        g2d.fill(bgRect)

        // Draw text
        g2d.color = JBColor.foreground()
        var yOffset = pos.y - textBlockHeight / 2 + fm.ascent

        lines.forEach { line ->
            val lineWidth = fm.stringWidth(line)
            g2d.drawString(line, (pos.x - lineWidth / 2).toFloat(), yOffset.toFloat())
            yOffset += lineHeight
        }
    }

    /**
     * Get better display name for nodes
     */
    private fun getNodeDisplayName(node: DaggerNode): String {
        return when (node) {
            is ProvisionNode -> {
                // For provisions, show method name without "provide" prefix
                val name = node.name.removePrefix("provide").removePrefix("get")
                if (name.isNotEmpty()) name else node.name
            }
            is InjectionNode -> {
                // For injections, show field name and containing class
                "${node.containingClass.substringAfterLast('.')}.${node.name}"
            }
            else -> node.name
        }
    }

    /**
     * Smart text wrapping based on pixel width
     */
    private fun smartWrapText(text: String, fm: FontMetrics, maxWidth: Double): List<String> {
        if (fm.stringWidth(text) <= maxWidth) {
            return listOf(text)
        }

        val lines = mutableListOf<String>()
        val words = text.split(Regex("(?=[A-Z])|_|\\.")).filter { it.isNotEmpty() }
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine$word"
            if (fm.stringWidth(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }
                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        // Limit to 4 lines max, with better truncation
        return if (lines.size > 4) {
            lines.take(3) + listOf(lines.drop(3).joinToString("").take(15) + "...")
        } else {
            lines
        }
    }

    /**
     * Draw legend
     */
    private fun drawLegend(g2d: Graphics2D) {
        val legendX = width - 210
        val legendY = 20

        // Background
        g2d.color = JBColor(Color(255, 255, 255, 230), Color(60, 63, 65, 230))
        g2d.fillRoundRect(legendX - 10, legendY - 10, 200, 150, 10, 10)
        g2d.color = JBColor.foreground()
        g2d.stroke = BasicStroke(1.5f)
        g2d.drawRoundRect(legendX - 10, legendY - 10, 200, 150, 10, 10)

        g2d.font = Font("SansSerif", Font.BOLD, 13)
        g2d.drawString("Legend", legendX, legendY + 15)

        val items = listOf(
            Triple("Component", COMPONENT_COLOR, true),
            Triple("Module", MODULE_COLOR, false),
            Triple("Provides", PROVISION_COLOR, false),
            Triple("Injection", INJECTION_COLOR, false)
        )

        var y = legendY + 40
        items.forEach { (label, color, isSquare) ->
            g2d.color = color
            if (isSquare) {
                g2d.fillRoundRect(legendX, y - 12, 18, 18, 4, 4)
            } else {
                g2d.fillOval(legendX, y - 12, 18, 18)
            }

            g2d.color = JBColor.foreground()
            g2d.font = Font("SansSerif", Font.PLAIN, 12)
            g2d.drawString(label, legendX + 30, y + 3)

            y += 28
        }
    }

    /**
     * Draw zoom indicator
     */
    private fun drawZoomIndicator(g2d: Graphics2D) {
        val zoomText = "Zoom: ${(zoomLevel * 100).toInt()}%"
        g2d.font = Font("SansSerif", Font.PLAIN, 11)
        val fm = g2d.fontMetrics
        val textWidth = fm.stringWidth(zoomText)

        val x = width - textWidth - 20
        val y = height - 30

        g2d.color = JBColor(Color(255, 255, 255, 200), Color(60, 63, 65, 200))
        g2d.fillRoundRect(x - 8, y - fm.ascent - 4, textWidth + 16, fm.height + 8, 6, 6)

        g2d.color = JBColor.foreground()
        g2d.drawString(zoomText, x, y)
    }

    /**
     * Handle click with zoom transformation
     */
    private fun handleClick(point: Point) {
        val transformedPoint = Point2D.Double(
            (point.x - panX) / zoomLevel,
            (point.y - panY) / zoomLevel
        )

        val clickedNode = findNodeAtPoint(transformedPoint)
        if (clickedNode != null) {
            selectedNode = clickedNode
            navigateToSource(clickedNode)
            repaint()
        }
    }

    /**
     * Handle hover with zoom transformation and path highlighting
     */
    private fun handleHover(point: Point) {
        val transformedPoint = Point2D.Double(
            (point.x - panX) / zoomLevel,
            (point.y - panY) / zoomLevel
        )

        val newHoveredNode = findNodeAtPoint(transformedPoint)

        if (newHoveredNode != hoveredNode) {
            hoveredNode = newHoveredNode

            // Update highlighted edges and nodes based on hover
            highlightedEdges.clear()
            highlightedNodes.clear()

            if (hoveredNode != null) {
                computeHighlightedPath(hoveredNode!!)
            }

            cursor = if (hoveredNode != null && !isPanning) {
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            } else if (isPanning) {
                Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
            } else {
                Cursor.getDefaultCursor()
            }
            repaint()
        }
    }

    /**
     * Compute which edges and nodes to highlight for the given node
     */
    private fun computeHighlightedPath(node: DaggerNode) {
        when (node) {
            is InjectionNode -> {
                // For injections, highlight all provisions that flow into this injection
                graph.edges.forEach { edge ->
                    if (edge.to == node && edge.type == EdgeType.CONSUMES_DEPENDENCY) {
                        highlightedEdges.add(edge)
                        highlightedNodes.add(edge.from)

                        // Also highlight edges that flow into those provisions (recursive)
                        addUpstreamPath(edge.from)
                    }
                }
            }
            is ProvisionNode -> {
                // For provisions, highlight all injections that consume this provision
                graph.edges.forEach { edge ->
                    if (edge.from == node && edge.type == EdgeType.CONSUMES_DEPENDENCY) {
                        highlightedEdges.add(edge)
                        highlightedNodes.add(edge.to)
                    }
                }

                // Also highlight provisions that this provision depends on
                graph.edges.forEach { edge ->
                    if (edge.to == node && edge.type == EdgeType.CONSUMES_DEPENDENCY) {
                        highlightedEdges.add(edge)
                        highlightedNodes.add(edge.from)
                        addUpstreamPath(edge.from)
                    }
                }
            }
            is ModuleNode -> {
                // For modules, highlight all provisions in the module
                graph.edges.forEach { edge ->
                    if (edge.from == node && edge.type == EdgeType.PROVIDES_DEPENDENCY) {
                        highlightedEdges.add(edge)
                        highlightedNodes.add(edge.to)
                    }
                }
            }
            is ComponentNode -> {
                // For components, highlight connected modules
                graph.edges.forEach { edge ->
                    if (edge.from == node && edge.type == EdgeType.COMPONENT_MODULE) {
                        highlightedEdges.add(edge)
                        highlightedNodes.add(edge.to)
                    }
                }
            }
        }
    }

    /**
     * Recursively add upstream dependencies to the highlighted path
     */
    private fun addUpstreamPath(node: DaggerNode, visited: MutableSet<DaggerNode> = mutableSetOf()) {
        if (visited.contains(node)) return
        visited.add(node)

        graph.edges.forEach { edge ->
            if (edge.to == node && (edge.type == EdgeType.CONSUMES_DEPENDENCY || edge.type == EdgeType.PROVIDES_DEPENDENCY)) {
                highlightedEdges.add(edge)
                highlightedNodes.add(edge.from)
                addUpstreamPath(edge.from, visited)
            }
        }
    }

    /**
     * Find node at point
     */
    private fun findNodeAtPoint(point: Point2D.Double): DaggerNode? {
        return nodeShapes.entries.firstOrNull { (_, shape) ->
            shape.contains(point)
        }?.key
    }

    /**
     * Navigate to source with line number support
     */
    private fun navigateToSource(node: DaggerNode) {
        val filePath = node.filePath ?: return
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath) ?: return
        val lineNumber = (node.lineNumber ?: 1) - 1 // Convert to 0-based

        val descriptor = OpenFileDescriptor(
            project,
            virtualFile,
            lineNumber,
            0
        )
        descriptor.navigate(true)
    }

    /**
     * Get tooltip for node
     */
    override fun getToolTipText(event: MouseEvent): String? {
        val transformedPoint = Point2D.Double(
            (event.x - panX) / zoomLevel,
            (event.y - panY) / zoomLevel
        )

        val node = findNodeAtPoint(transformedPoint)
        return node?.let { buildTooltip(it) }
    }

    /**
     * Build detailed tooltip
     */
    private fun buildTooltip(node: DaggerNode): String {
        return when (node) {
            is ComponentNode -> {
                val modules = if (node.modules.isNotEmpty())
                    node.modules.joinToString(", ") { it.substringAfterLast('.') }
                else "None"
                "<html><b>Component:</b> ${node.name}<br>" +
                        "<b>Modules:</b> $modules<br>" +
                        "<b>Scope:</b> ${node.scope ?: "Unscoped"}</html>"
            }
            is ModuleNode -> {
                "<html><b>Module:</b> ${node.name}<br>" +
                        "<b>Provides:</b> ${node.provides.size} dependencies</html>"
            }
            is ProvisionNode -> {
                val params = node.parameters.joinToString(", ") { it.type.substringAfterLast('.') }
                "<html><b>Provides:</b> ${node.returnType.substringAfterLast('.')}<br>" +
                        "<b>Method:</b> ${node.name}<br>" +
                        "${if (params.isNotEmpty()) "<b>Params:</b> $params<br>" else ""}" +
                        "<b>Scope:</b> ${node.scope ?: "Unscoped"}</html>"
            }
            is InjectionNode -> {
                "<html><b>Injection:</b> ${node.name}<br>" +
                        "<b>Type:</b> ${node.type.substringAfterLast('.')}<br>" +
                        "<b>Class:</b> ${node.containingClass.substringAfterLast('.')}</html>"
            }
        }
    }

    /**
     * Reset view to fit all nodes
     */
    fun resetView() {
        zoomLevel = 1.0
        panX = 0.0
        panY = 0.0
        repaint()
    }

    /**
     * Zoom to fit all content
     */
    fun zoomToFit() {
        val contentBounds = calculateContentBounds()
        if (contentBounds.width > 0 && contentBounds.height > 0) {
            val xScale = (width - 100) / contentBounds.width
            val yScale = (height - 100) / contentBounds.height
            zoomLevel = min(xScale, yScale).coerceIn(MIN_ZOOM, MAX_ZOOM)

            panX = (width - contentBounds.width * zoomLevel) / 2 - contentBounds.x * zoomLevel
            panY = (height - contentBounds.height * zoomLevel) / 2 - contentBounds.y * zoomLevel

            repaint()
        }
    }

    /**
     * Calculate bounding box of all content
     */
    private fun calculateContentBounds(): Rectangle2D.Double {
        if (nodePositions.isEmpty()) {
            return Rectangle2D.Double(0.0, 0.0, 0.0, 0.0)
        }

        val minX = nodePositions.values.minOf { it.x } - nodeRadius - padding
        val minY = nodePositions.values.minOf { it.y } - nodeRadius - padding
        val maxX = nodePositions.values.maxOf { it.x } + nodeRadius + padding
        val maxY = nodePositions.values.maxOf { it.y } + nodeRadius + padding

        return Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY)
    }
}

/**
 * Create scrollable graph panel with controls
 */
fun createGraphScrollPane(project: Project, graph: DaggerGraph): JBScrollPane {
    val graphPanel = DaggerGraphPanel(project, graph)

    // Auto zoom to fit on creation
    javax.swing.SwingUtilities.invokeLater {
        graphPanel.zoomToFit()
    }

    return JBScrollPane(graphPanel).apply {
        horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    }
}
