# Contributing to Dagger Dependency Visualizer

Thank you for your interest in contributing to the Dagger Dependency Visualizer plugin!

## Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/yourusername/dagger-diag-plugin.git
   cd dagger-diag-plugin
   ```

3. Build the project:
   ```bash
   ./gradlew buildPlugin
   ```

4. Run the plugin in a sandbox IntelliJ instance:
   ```bash
   ./gradlew runIde
   ```

## Development Setup

### Prerequisites

- JDK 17 or later
- IntelliJ IDEA (Community or Ultimate)
- Basic knowledge of:
  - Kotlin
  - IntelliJ Platform SDK
  - Dagger 2

### Project Structure

```
dagger-diag-plugin/
├── src/main/kotlin/com/daggerdiag/
│   ├── actions/          # IDE actions (menu items, shortcuts)
│   ├── analyzers/        # Code analysis logic
│   ├── listeners/        # Event listeners
│   ├── models/           # Data models
│   ├── services/         # Project services
│   ├── toolwindow/       # Tool window UI
│   └── ui/               # Custom UI components
├── src/main/resources/
│   ├── META-INF/
│   │   └── plugin.xml    # Plugin descriptor
│   └── icons/            # Plugin icons
├── build.gradle.kts      # Build configuration
└── README.md
```

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported
2. Create a new issue with:
   - Clear description of the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - IntelliJ version and plugin version
   - Sample code if applicable

### Suggesting Features

1. Open an issue describing the feature
2. Explain the use case and benefits
3. Discuss implementation approach

### Submitting Pull Requests

1. Create a feature branch:
   ```bash
   git checkout -b feature/my-new-feature
   ```

2. Make your changes following the coding standards

3. Test your changes:
   ```bash
   ./gradlew check
   ./gradlew runIde
   ```

4. Commit with clear messages:
   ```bash
   git commit -m "Add feature: description"
   ```

5. Push to your fork:
   ```bash
   git push origin feature/my-new-feature
   ```

6. Open a pull request with:
   - Clear description of changes
   - Link to related issues
   - Screenshots if UI changes

## Coding Standards

### Kotlin Style

- Follow official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs

### Code Organization

- Keep classes focused and single-purpose
- Extract complex logic into separate functions
- Use data classes for models

### Comments

- Add comments for complex logic
- Use KDoc for public APIs
- Keep comments up-to-date with code changes

## Testing

Currently, the plugin doesn't have automated tests, but we're working on it!

Manual testing checklist:
- [ ] Plugin loads without errors
- [ ] Analysis completes on sample project
- [ ] Diagram renders correctly
- [ ] Clicking nodes navigates to source
- [ ] Tool window shows/hides properly
- [ ] No memory leaks during repeated analysis

## Architecture Guidelines

### Analyzers

The `DaggerAnalyzer` class:
- Uses PSI (Program Structure Interface) to parse code
- Should handle both Kotlin and Java (future)
- Must be thread-safe

### Models

Data classes in `models/`:
- Immutable (use `val`, not `var`)
- Include all necessary metadata
- Support equality checks

### UI

Custom Swing components in `ui/`:
- Should be responsive
- Handle large datasets efficiently
- Use IntelliJ's color scheme

### Services

Project-level services:
- Use `@Service` annotation
- Cache data appropriately
- Invalidate cache on file changes

## Common Tasks

### Adding Support for a New Dagger Annotation

1. Update `DaggerAnalyzer.kt` constants
2. Add parsing logic in appropriate method
3. Create model if needed
4. Update graph building logic
5. Update UI rendering if needed

### Improving Layout Algorithm

Edit `DaggerGraphPanel.calculateLayout()`:
- Consider node types and relationships
- Minimize edge crossings
- Ensure readability

### Adding a New Action

1. Create action class in `actions/`
2. Extend `AnAction`
3. Override `actionPerformed()` and `update()`
4. Register in `plugin.xml`

## Release Process

(For maintainers)

1. Update version in `build.gradle.kts`
2. Update CHANGELOG.md
3. Create git tag
4. Build plugin: `./gradlew buildPlugin`
5. Upload to JetBrains Marketplace

## Questions?

Feel free to:
- Open an issue for discussion
- Reach out to maintainers
- Check existing issues and PRs

## Code of Conduct

Be respectful, constructive, and professional in all interactions.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
