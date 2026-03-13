# E-Commerce Sample App - Dagger Visualization Demo

This is a comprehensive example demonstrating complex Dagger dependency injection with **50+ provisions and usages**.

## Architecture

This sample app follows clean architecture with multiple layers:

- **Network Layer**: Retrofit, OkHttp, API services
- **Database Layer**: Room, DAOs
- **Repository Layer**: Data management
- **Domain Layer**: Use cases
- **Presentation Layer**: ViewModels, UI components

## Dagger Structure

### Components
- `AppComponent` - Application-level dependencies
- Subcomponents for features (planned)

### Modules
- `NetworkModule` - HTTP client, Retrofit, API services
- `DatabaseModule` - Room database, DAOs
- `RepositoryModule` - Repository implementations
- `UseCaseModule` - Business logic use cases
- `ViewModelModule` - ViewModels for UI

## Graph Size

- **Components**: 1 main component
- **Modules**: 5 modules
- **@Provides methods**: ~50
- **@Inject usages**: ~50

This creates a large, realistic dependency graph perfect for testing the visualization plugin.

## How to Use

1. Open this directory in IntelliJ IDEA
2. Install the Dagger Dependency Visualizer plugin
3. Press `Ctrl+Alt+D` (or `Cmd+Alt+D` on Mac)
4. Click "Analyze Project"
5. See the large dependency graph with zoom/pan controls

## Dependencies Flow

```
AppComponent
    ├─→ NetworkModule (12 provisions)
    ├─→ DatabaseModule (8 provisions)
    ├─→ RepositoryModule (10 provisions)
    ├─→ UseCaseModule (15 provisions)
    └─→ ViewModelModule (10 provisions)

Total: ~55 provisions + corresponding injections
```
