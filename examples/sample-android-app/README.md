# Sample Android App with Dagger

This is a sample project demonstrating how the Dagger Dependency Visualizer plugin works.

## Project Structure

```
src/main/kotlin/com/example/app/
├── di/
│   ├── AppComponent.kt       # Main application component
│   ├── NetworkModule.kt      # Network dependencies
│   ├── DatabaseModule.kt     # Database dependencies
│   └── AppModule.kt          # Application-level dependencies
├── data/
│   ├── Repository.kt         # Data repository interface
│   ├── RepositoryImpl.kt     # Repository implementation
│   └── ApiService.kt         # API service
└── ui/
    └── MainActivity.kt       # Main activity with injection
```

## How to Use

1. Open this project in IntelliJ IDEA
2. Install the Dagger Dependency Visualizer plugin
3. Press `Ctrl+Alt+D` or go to Tools → Show Dagger Diagram
4. Click "Analyze Project"

## Expected Visualization

The plugin will show:

- **AppComponent** (blue square) at the top
  - Connected to NetworkModule, DatabaseModule, and AppModule

- **NetworkModule** (green circle)
  - Provides OkHttpClient
  - Provides Retrofit (depends on OkHttpClient)
  - Provides ApiService (depends on Retrofit)

- **DatabaseModule** (green circle)
  - Provides RoomDatabase
  - Provides UserDao (depends on RoomDatabase)

- **AppModule** (green circle)
  - Provides Repository (depends on ApiService and UserDao)

- **MainActivity** (red circle)
  - Injects Repository

## Dependency Flow

```
AppComponent
    ├─→ NetworkModule
    │       ├─→ OkHttpClient
    │       ├─→ Retrofit ──→ (uses OkHttpClient)
    │       └─→ ApiService ──→ (uses Retrofit)
    │
    ├─→ DatabaseModule
    │       ├─→ RoomDatabase
    │       └─→ UserDao ──→ (uses RoomDatabase)
    │
    └─→ AppModule
            └─→ Repository ──→ (uses ApiService, UserDao)

MainActivity ──→ (injects Repository)
```

## Click Navigation

Try clicking on nodes in the diagram:
- Click on `provideRetrofit` → jumps to NetworkModule.kt:15
- Click on `Repository` injection → jumps to MainActivity.kt:8
- Click on `AppComponent` → jumps to AppComponent.kt:1
