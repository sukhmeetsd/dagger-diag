# Changelog

All notable changes to the Dagger Dependency Visualizer plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-01-17

### Added
- Initial release of Dagger Dependency Visualizer
- Interactive graph visualization of Dagger dependency injection
- Support for @Component, @Module, @Provides, @Inject, @Binds annotations
- Clickable navigation from diagram to source code
- Tool window with analysis UI
- Keyboard shortcut (Ctrl+Alt+D / Cmd+Alt+D) for quick access
- Context menu action for analyzing Dagger components
- Automatic cache invalidation on file changes
- Support for scopes (@Singleton, custom scopes)
- Support for qualifiers (@Named, custom qualifiers)
- Hierarchical graph layout
- Color-coded nodes (Components, Modules, Provisions, Injections)

### Changed
- Updated to IntelliJ Platform Gradle Plugin 2.1.0 for better performance
- Migrated from deprecated `org.jetbrains.intellij` to `org.jetbrains.intellij.platform`
- Updated Kotlin to 2.0.21
- Disabled searchable options building to reduce build time
- Disabled instrumentation to reduce build size
- Removed unused jgrapht dependencies (~200MB reduction)

### Fixed
- Extended version compatibility to support IntelliJ 2024.2 through 2025.3 and all future versions
- Reduced build download size from ~1GB to ~600MB
- Optimized build configuration for faster compilation

### Technical
- Minimum IntelliJ version: 2024.2 (build 242)
- Maximum IntelliJ version: None (supports all future versions)
- Minimum JDK version: 17
- Plugin size: ~50MB (reduced from ~100MB)
- Kotlin version: 2.0.21
- IntelliJ Platform Gradle Plugin: 2.1.0

## [Unreleased]

### Planned
- Java support (currently Kotlin-only)
- Zoom and pan controls for large graphs
- Filter nodes by scope or module
- Search functionality within diagram
- Export diagram as image/SVG
- Detect and highlight dependency cycles
- Subcomponent visualization improvements
- Multi-module project support enhancements
