# Dagger Diagram Visualization Guide

## Overview

The Dagger Dependency Visualizer now includes powerful visualization features designed to handle large, complex dependency graphs with ease.

## New Features

### ✨ Zoom & Pan

**Mouse Wheel Zoom**
- Scroll **up** to zoom in (max 300%)
- Scroll **down** to zoom out (min 10%)
- Zooms toward your mouse cursor for intuitive navigation

**Panning**
- **Middle mouse button** + drag to pan around
- **Ctrl + Left click** + drag to pan
- Great for exploring large diagrams

**Zoom Indicator**
- Bottom-right corner shows current zoom level
- Real-time update as you zoom

### 📐 Improved Layout

**Dynamic Spacing**
- Automatically adjusts based on graph size:
  - **Small graphs** (< 20 nodes): Spacious layout for clarity
  - **Medium graphs** (20-50 nodes): Balanced spacing
  - **Large graphs** (50-100 nodes): Compact but readable
  - **Very large graphs** (100+ nodes): Maximum density

**Better Organization**
- Components: Square grid at top
- Modules: 6 columns max
- **Provisions: 8 columns max** (handles many @Provides)
- **Injections: 8 columns max** (handles many @Inject)

### 🏷️ Clearer Labels

**Smart Text Display**
- **Provisions**: Shows method name without "provide" prefix
  - `provideRetrofit` → `Retrofit`
  - `getApiService` → `ApiService`
- **Injections**: Shows class + field name
  - `MainActivity.repository`
  - `UserViewModel.database`
- **Components/Modules**: Full name

**Intelligent Wrapping**
- Splits text on capital letters (camelCase aware)
- Wraps by `_` and `.` characters
- Maximum 3 lines with ellipsis
- Background rectangle for contrast

**Better Readability**
- Semi-transparent text backgrounds
- Larger fonts
- High-quality antialiasing
- Scales with zoom level

### 💡 Rich Tooltips

Hover over any node to see detailed information:

**Component**
```
Component: AppComponent
Modules: NetworkModule, DatabaseModule
Scope: Singleton
```

**Module**
```
Module: NetworkModule
Provides: 5 dependencies
```

**Provision**
```
Provides: Retrofit
Method: provideRetrofit
Params: OkHttpClient, Gson
Scope: Singleton
```

**Injection**
```
Injection: repository
Type: UserRepository
Class: MainActivity
```

### 🎯 Precise Navigation

**Click to Jump**
- Click any node to navigate to source
- Now jumps to **exact line number**
- Opens file and scrolls to definition

**Visual Feedback**
- Hover: Node turns yellow
- Selected: Node turns blue
- Hand cursor indicates clickable items

### 🎨 Visual Enhancements

**Curved Edges**
- Gentle curves instead of straight lines
- Reduces visual clutter
- Better arrow heads

**Color Coding**
- 🔷 **Blue squares**: Components
- 🟢 **Green circles**: Modules
- 🟠 **Orange circles**: Provisions
- 🔴 **Red circles**: Injections

**Modern Styling**
- Rounded corners on components
- Semi-transparent edges
- Smooth gradients
- Professional appearance

## How to Use

### Getting Started

1. **Open the diagram**: Press `Ctrl+Alt+D` (or `Cmd+Alt+D` on Mac)
2. **Analyze project**: Click "Analyze Project" button
3. **View diagram**: Automatically zooms to fit all content

### Navigation Controls

| Action | Method |
|--------|--------|
| **Zoom in** | Scroll wheel up |
| **Zoom out** | Scroll wheel down |
| **Pan** | Middle mouse + drag<br>Ctrl + Left click + drag |
| **Select node** | Left click |
| **View details** | Hover for tooltip |
| **Jump to code** | Click node |

### For Large Graphs

If you have many provisions/injections (50+):

1. **Start zoomed out**: See the big picture
2. **Zoom into areas**: Focus on specific modules
3. **Use tooltips**: Get info without zooming in too much
4. **Pan around**: Explore different sections

### Tips & Tricks

**Finding Specific Dependencies**
1. Zoom out to see all provisions
2. Scan for the type you're looking for
3. Hover for details
4. Click to jump to implementation

**Understanding Flow**
1. Start at components (top, blue squares)
2. Follow edges to modules (green circles)
3. See what each module provides (orange circles)
4. Check who injects what (red circles, bottom)

**Reading Labels**
- Labels automatically wrap long names
- Zoom in if text is too small
- Use tooltips for full details

## Architecture (For Future Enhancements)

The visualization is built with extensibility in mind:

### Ready for Drag-and-Drop

```kotlin
// Node positions stored as Point2D.Double
private val nodePositions = mutableMapOf<DaggerNode, Point2D.Double>()

// Mouse adapter with TODO for dragging
override fun mouseDragged(e: MouseEvent) {
    if (isPanning && lastMousePoint != null) {
        // ... panning code ...
    }
    // TODO: Add node dragging here in future
}
```

**What's needed for drag-and-drop:**
1. Add `isDragging` state flag
2. Store `draggedNode: DaggerNode?`
3. In `mouseDragged`: Update node position
4. In `mouseReleased`: Snap to grid or save position
5. Add "Reset Layout" button

### Extensible Transform System

```kotlin
// Proper coordinate transformation
val transformedPoint = Point2D.Double(
    (point.x - panX) / zoomLevel,
    (point.y - panY) / zoomLevel
)
```

Supports:
- Multi-touch gestures (future)
- Rotation (future)
- 3D transformations (future)

### Performance Optimized

- Shapes cached and reused
- Efficient repainting
- Smart bounds calculation
- Lazy tooltip evaluation

## Troubleshooting

### Graph Still Looks Congested

**Try this:**
1. Zoom out more (scroll down)
2. Check if you have 100+ provisions
3. Use "Zoom to Fit" feature
4. Consider refactoring to fewer, larger modules

### Labels Overlapping

**Solutions:**
1. Zoom in slightly
2. The dynamic layout should prevent this
3. If persistent, report as bug with graph size

### Can't Find a Node

**Search strategy:**
1. Zoom to fit (see everything)
2. Pan to the layer you need:
   - Top = Components
   - Upper middle = Modules
   - Lower middle = Provisions
   - Bottom = Injections
3. Look for the color coding

### Zoom Too Sensitive

**Adjust your approach:**
- Smaller scroll movements for fine control
- Use pan instead of zoom for navigation
- Current zoom shown in bottom-right

## Performance Notes

**Graph Size Limits**
- ✅ **Up to 50 nodes**: Excellent performance
- ✅ **50-100 nodes**: Good performance
- ⚠️ **100-200 nodes**: May slow on zoom/pan
- ❌ **200+ nodes**: Consider filtering

**Memory Usage**
- Each node: ~1KB
- 100 nodes: ~100KB
- Edges add minimal overhead

## Future Enhancements

### Planned Features

- [ ] **Drag-and-drop nodes** - Rearrange graph manually
- [ ] **Filter by scope** - Show only @Singleton, etc.
- [ ] **Filter by module** - Hide/show specific modules
- [ ] **Search** - Find nodes by name
- [ ] **Export** - Save diagram as PNG/SVG
- [ ] **Minimap** - Bird's-eye view in corner
- [ ] **Highlight paths** - Show dependency chains
- [ ] **Cycle detection** - Highlight circular deps
- [ ] **Subcomponent support** - Better visualization

### Code is Ready

The architecture supports these features:
- Node position management ✅
- Transform system ✅
- Mouse event handling ✅
- Efficient rendering ✅

Just need to implement the UI and logic!

## Feedback

Found an issue or have a suggestion?

- **GitHub Issues**: https://github.com/sukhmeetsd/dagger-diag/issues
- **Feature Requests**: Tag with "enhancement"
- **Bug Reports**: Include graph size and screenshot

## Summary

The visualization now handles:
- ✅ Large graphs (100+ nodes)
- ✅ Complex layouts
- ✅ Clear, readable labels
- ✅ Smooth zoom and pan
- ✅ Precise navigation
- ✅ Rich information display

**Enjoy exploring your Dagger dependencies!** 🎉
