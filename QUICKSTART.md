# Quick Start Guide

## What You Have Now

Your Dagger Dependency Visualizer plugin is ready to build and install! ✅

## Files Created

```
dagger-diag/
├── 📄 README.md              - Full documentation
├── 📄 INSTALLATION.md        - Detailed installation guide
├── 📄 CONTRIBUTING.md        - Developer guide
├── 📄 LICENSE                - MIT License
├── 📄 build.gradle.kts       - Gradle build configuration
├── 📄 settings.gradle.kts    - Gradle settings
├── 📄 gradle.properties      - Gradle properties
├── 🔧 gradlew                - Gradle wrapper (Linux/Mac)
├── 🔧 gradlew.bat            - Gradle wrapper (Windows)
├── 📁 gradle/wrapper/        - Wrapper files
├── 📁 src/                   - Plugin source code
│   ├── main/kotlin/com/daggerdiag/
│   │   ├── actions/          - IDE actions
│   │   ├── analyzers/        - Code analysis
│   │   ├── listeners/        - Event listeners
│   │   ├── models/           - Data models
│   │   ├── services/         - Background services
│   │   ├── toolwindow/       - Tool window UI
│   │   └── ui/               - Graph visualization
│   └── resources/
│       ├── META-INF/plugin.xml
│       └── icons/
└── 📁 examples/              - Sample projects
```

## Installation Steps (On Your Machine)

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/sukhmeetsd/dagger-diag.git
cd dagger-diag
```

### 2️⃣ Build the Plugin

Make sure you have:
- **Java 17+** installed (`java -version`)
- **Internet connection** (to download dependencies)

Then run:

**On Linux/Mac:**
```bash
./gradlew buildPlugin
```

**On Windows:**
```cmd
gradlew.bat buildPlugin
```

This creates: `build/distributions/dagger-diag-1.0.0.zip`

### 3️⃣ Install in IntelliJ

1. Open **IntelliJ IDEA**
2. Go to **Settings** → **Plugins** (Ctrl+Alt+S / Cmd+,)
3. Click **⚙️** → **Install Plugin from Disk...**
4. Select `build/distributions/dagger-diag-1.0.0.zip`
5. Click **OK** and **Restart**

### 4️⃣ Use the Plugin

After restart:

1. Open a Kotlin project with Dagger
2. Press **Ctrl+Alt+D** (Cmd+Alt+D on Mac)
3. Click **"Analyze Project"**
4. See your dependency graph! 🎉

## Features Recap

✅ **Analyzes Dagger Code**
- Finds all `@Component`, `@Module`, `@Provides`, `@Inject` annotations
- Extracts scopes, qualifiers, and dependencies

✅ **Visualizes Dependencies**
- Interactive graph with color-coded nodes
- Blue squares = Components
- Green circles = Modules
- Orange circles = Provisions
- Red circles = Injections

✅ **Clickable Navigation**
- Click any node to jump to source code
- Hover to highlight

✅ **Smart Caching**
- Automatically refreshes when files change
- Fast async analysis

## Testing Without Installing

Want to test before installing?

```bash
./gradlew runIde
```

This opens a **sandbox IntelliJ** with your plugin pre-installed!

## Troubleshooting

**Build fails?**
- Check you have Java 17+: `java -version`
- Make sure you have internet connection
- Try: `./gradlew clean buildPlugin`

**Plugin doesn't appear?**
- Make sure you restarted IntelliJ completely
- Check Settings → Plugins → Installed

**More help:** See [INSTALLATION.md](INSTALLATION.md)

## What the Plugin Does

### Example Dagger Code

```kotlin
@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}

@Module
class NetworkModule {
    @Provides
    fun provideRetrofit(): Retrofit = ...
}

class MainActivity {
    @Inject lateinit var retrofit: Retrofit
}
```

### Generated Diagram Shows

```
┌─────────────┐
│AppComponent │ (Blue square)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│NetworkModule│ (Green circle)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│provideRetro │ (Orange circle)
└──────┬──────┘
       │
       ↓
┌─────────────┐
│MainActivity │ (Red circle)
└─────────────┘
```

**Click any box → Jumps to code!**

## Next Steps

1. **Try it:** Build and install on your machine
2. **Test it:** Open a Dagger project and generate a diagram
3. **Share it:** Show your team!
4. **Improve it:** See [CONTRIBUTING.md](CONTRIBUTING.md) to contribute

## Keyboard Shortcuts

| Action | Shortcut |
|--------|----------|
| Show Dagger Diagram | `Ctrl+Alt+D` (Mac: `Cmd+Alt+D`) |
| Open Tool Window | `View` → `Tool Windows` → `Dagger Diagram` |

## Sample Project

Check out `examples/sample-android-app/` for a complete example!

## Support

- **📖 Documentation:** [README.md](README.md)
- **🔧 Installation:** [INSTALLATION.md](INSTALLATION.md)
- **🤝 Contributing:** [CONTRIBUTING.md](CONTRIBUTING.md)
- **🐛 Issues:** https://github.com/sukhmeetsd/dagger-diag/issues

---

**Ready to visualize your Dagger dependencies? Let's go! 🚀**
