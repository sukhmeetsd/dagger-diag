# Installation Guide

## Prerequisites

Before you begin, make sure you have:

- **Java Development Kit (JDK) 17 or later**
  - Check with: `java -version`
  - Download from: https://adoptium.net/

- **Internet connection** (for downloading dependencies)

- **IntelliJ IDEA 2024.2 or later**
  - Download from: https://www.jetbrains.com/idea/download/

## Step 1: Clone the Repository

```bash
git clone https://github.com/sukhmeetsd/dagger-diag.git
cd dagger-diag
```

## Step 2: Build the Plugin

### Option A: Using Gradle Wrapper (Recommended)

On **Linux/Mac**:
```bash
./gradlew buildPlugin
```

On **Windows**:
```cmd
gradlew.bat buildPlugin
```

This will:
1. Download all necessary dependencies
2. Compile the Kotlin code
3. Build the plugin
4. Create a ZIP file at: `build/distributions/dagger-diag-1.0.0.zip`

**First build may take 2-5 minutes** as Gradle downloads dependencies.

### Option B: Using System Gradle

If you have Gradle installed system-wide:

```bash
gradle buildPlugin
```

## Step 3: Install in IntelliJ IDEA

### Method 1: Install from Disk (Recommended)

1. Open **IntelliJ IDEA**

2. Go to **Settings/Preferences**:
   - Windows/Linux: `File` → `Settings` or press `Ctrl+Alt+S`
   - Mac: `IntelliJ IDEA` → `Preferences` or press `Cmd+,`

3. Navigate to **Plugins** in the left sidebar

4. Click the **⚙️ (gear icon)** at the top

5. Select **Install Plugin from Disk...**

6. Browse to: `build/distributions/dagger-diag-1.0.0.zip`

7. Click **OK**

8. Click **Restart IDE** when prompted

### Method 2: Run in Development Mode (For Testing)

If you want to test without installing:

```bash
./gradlew runIde
```

This opens a new IntelliJ instance with the plugin pre-installed in a sandbox environment.

## Step 4: Verify Installation

After restarting IntelliJ:

1. Go to **Settings** → **Plugins** → **Installed**

2. Look for **"Dagger Dependency Visualizer"** in the list

3. It should show as enabled (checked)

## Step 5: Use the Plugin

### Quick Test

1. Open any Kotlin project that uses Dagger

2. Press **`Ctrl+Alt+D`** (or **`Cmd+Alt+D`** on Mac)

3. Click **"Analyze Project"**

4. You should see the dependency graph!

### Alternative Methods

- **Via Menu**: `Tools` → `Show Dagger Diagram`
- **Via Tool Window**: `View` → `Tool Windows` → `Dagger Diagram`
- **From Editor**: Right-click in a file with Dagger annotations → `Analyze Dagger Component`

## Troubleshooting

### Build Issues

**Problem**: `Plugin [id: 'org.jetbrains.kotlin.jvm'] was not found`

**Solution**: Make sure you have internet connection. Gradle needs to download plugins on first build.

---

**Problem**: `Could not find org.jetbrains.intellij`

**Solution**:
1. Delete the `.gradle` folder in your project
2. Run `./gradlew clean`
3. Try `./gradlew buildPlugin` again

---

**Problem**: Gradle wrapper doesn't execute (`./gradlew` not found)

**Solution**:
1. Make sure you're in the project directory
2. On Linux/Mac, the file should be executable: `chmod +x gradlew`
3. Or use system Gradle: `gradle buildPlugin`

### Installation Issues

**Problem**: Plugin doesn't appear after installation

**Solution**:
1. Make sure you restarted IntelliJ IDEA completely (not just closed windows)
2. Check `Settings` → `Plugins` → `Installed` to confirm it's there
3. If present but disabled, click the checkbox to enable it

---

**Problem**: "Plugin requires restart" but nothing changes

**Solution**:
1. Go to `File` → `Exit` (not just close window)
2. Restart IntelliJ completely
3. Check again in Settings → Plugins

### Runtime Issues

**Problem**: "No Dagger components or modules found"

**Solution**:
- Make sure your project actually uses Dagger 2
- Check that you have `@Component` or `@Module` annotations in Kotlin files
- Verify Dagger dependencies are in your `build.gradle.kts`

---

**Problem**: Plugin crashes or freezes

**Solution**:
1. Check IntelliJ logs: `Help` → `Show Log in Finder/Explorer`
2. Look for errors mentioning "daggerdiag"
3. Report the issue with the error log

## Building for Distribution

If you want to publish or share the plugin:

### Create Signed Plugin (Optional)

```bash
export CERTIFICATE_CHAIN="<your-cert>"
export PRIVATE_KEY="<your-key>"
export PRIVATE_KEY_PASSWORD="<your-password>"
./gradlew signPlugin
```

### Publish to JetBrains Marketplace (Optional)

```bash
export PUBLISH_TOKEN="<your-token>"
./gradlew publishPlugin
```

## System Requirements

- **Operating System**: Windows, macOS, or Linux
- **JDK**: 17 or later
- **IntelliJ IDEA**: 2024.2 or later (Community or Ultimate)
- **Disk Space**: ~50MB for the plugin
- **RAM**: 2GB minimum (for IntelliJ, plugin is lightweight)

## Supported Dagger Versions

This plugin works with:
- Dagger 2.x (all versions)
- Hilt (Dagger wrapper)

It analyzes source code directly, so it works regardless of Dagger version.

## Supported Languages

Currently supported:
- ✅ Kotlin

Planned support:
- ⏳ Java (coming soon)

## Next Steps

Once installed:
1. Read the [README.md](README.md) for usage instructions
2. Try the [example project](examples/sample-android-app/)
3. Check out [CONTRIBUTING.md](CONTRIBUTING.md) if you want to contribute

## Getting Help

- **GitHub Issues**: https://github.com/sukhmeetsd/dagger-diag/issues
- **Documentation**: See [README.md](README.md)

## Quick Reference

| Action | Command |
|--------|---------|
| Build plugin | `./gradlew buildPlugin` |
| Run in sandbox | `./gradlew runIde` |
| Clean build | `./gradlew clean` |
| List tasks | `./gradlew tasks` |
| Check code | `./gradlew check` |

Happy coding! 🚀
