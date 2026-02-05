# POC Task 1: Gutter Icon Implementation Summary

## Overview

This POC implements comprehensive gutter icon support for Dagger, Hilt, and Anvil annotations as the first phase of the Scabbard-inspired enhancements.

## What Was Implemented

### Base Infrastructure

**`DaggerLineMarkerProvider.kt`** - Abstract base class providing:
- `LineMarkerProviderDescriptor` implementation for Settings UI integration
- UAST (Unified Abstract Syntax Tree) support for both Kotlin and Java
- Leaf element targeting (annotation name identifiers only) for optimal performance
- Common navigation handler (currently shows notifications as POC)
- Standardized icon and tooltip rendering

### Dagger Annotations (4 providers)

1. **`ComponentLineMarkerProvider.kt`** - `@dagger.Component`
   - Icon: Class icon
   - Tooltip: "Navigate to Component dependency graph"

2. **`SubcomponentLineMarkerProvider.kt`** - `@dagger.Subcomponent`
   - Icon: Hierarchy icon
   - Tooltip: "Navigate to Subcomponent dependency graph"

3. **`ModuleLineMarkerProvider.kt`** - `@dagger.Module`
   - Icon: Module icon
   - Tooltip: "Navigate to Module provisions"

4. **`ContributesAndroidInjectorLineMarkerProvider.kt`** - `@dagger.android.ContributesAndroidInjector`
   - Icon: Implementing method icon
   - Tooltip: "Navigate to generated [ClassName] subcomponent"
   - Special logic: Applied to methods, extracts return type class name

### Hilt Annotations (4 providers in `HiltLineMarkerProviders.kt`)

1. **`HiltAndroidAppLineMarkerProvider`** - `@dagger.hilt.android.HiltAndroidApp`
   - Tooltip: "Navigate to ApplicationC component"

2. **`AndroidEntryPointLineMarkerProvider`** - `@dagger.hilt.android.AndroidEntryPoint`
   - Smart inference: Determines component type from superclass
   - ActivityComponent, FragmentComponent, ViewComponent, ServiceComponent
   - Tooltip: "Navigate to [InferredType]Component"

3. **`HiltViewModelLineMarkerProvider`** - `@dagger.hilt.android.lifecycle.HiltViewModel`
   - Tooltip: "Navigate to ViewModelComponent"

4. **`DefineComponentLineMarkerProvider`** - `@dagger.hilt.DefineComponent`
   - Tooltip: "Navigate to custom Hilt component"

### Anvil Annotations (3 providers in `AnvilLineMarkerProviders.kt`)

1. **`MergeComponentLineMarkerProvider`** - `@com.squareup.anvil.annotations.MergeComponent`
   - Icon: Merge icon
   - Tooltip: "Navigate to merged Component"

2. **`MergeSubcomponentLineMarkerProvider`** - `@com.squareup.anvil.annotations.MergeSubcomponent`
   - Icon: Hierarchy icon
   - Tooltip: "Navigate to merged Subcomponent"

3. **`ContributesToLineMarkerProvider`** - `@com.squareup.anvil.annotations.ContributesTo`
   - Icon: Push icon (indicating contribution/injection)
   - Tooltip: "Navigate to contributed scope [ScopeName]"

## Plugin Registration

All 11 LineMarkerProviders registered in `plugin.xml`:
- Each provider registered twice (once for Kotlin, once for Java)
- 22 total registrations using `codeInsight.lineMarkerProvider` extension point
- Notification group "Dagger Diagram" registered for navigation messages

## Technical Highlights

### Cross-Language Support via UAST
```kotlin
val uClass = element.toUElement()?.uastParent as? UClass ?: return null
val annotation = uClass.uAnnotations.find {
    it.qualifiedName == annotationFqn
} ?: return null
```

### Performance Optimization
Only processes leaf elements (annotation name identifiers):
```kotlin
private fun isAnnotationNameIdentifier(element: PsiElement): Boolean {
    // Kotlin: KtAnnotationEntry.typeReference.typeElement.navigationElement
    // Java: PsiAnnotation.nameReferenceElement
}
```

### Smart Type Inference (AndroidEntryPoint)
```kotlin
private fun inferComponentType(uClass: UClass): String {
    val superName = uClass.javaPsi.superClass?.qualifiedName ?: ""
    return when {
        superName.contains("Activity") -> "Activity"
        superName.contains("Fragment") -> "Fragment"
        // ...
    }
}
```

## Testing Instructions

Due to network connectivity issues, the build couldn't be completed. To test this POC:

### 1. Build the Plugin
```bash
./gradlew buildPlugin
```

### 2. Run in Development IDE
```bash
./gradlew runIde
```

### 3. Verify Gutter Icons Appear

Open the e-commerce example files and verify gutter icons appear on:

**`examples/ecommerce-app/src/main/kotlin/com/example/ecommerce/di/AppComponent.kt`**
- Line 13: `@Singleton` and `@Component` annotations → Should show Component icon

**`examples/ecommerce-app/src/main/kotlin/com/example/ecommerce/di/NetworkModule.kt`**
- Line 9: `@Module` annotation → Should show Module icon

**`examples/ecommerce-app/src/main/kotlin/com/example/ecommerce/ui/MainActivity.kt`**
- Check if `@AndroidEntryPoint` is present → Should show AndroidEntryPoint icon

**Test all example activity files:**
```bash
find examples/ecommerce-app -name "*Activity.kt" -type f
```

### 4. Test Icon Functionality
- Click gutter icons → Should show balloon notification
- Hover over icons → Should show descriptive tooltip
- Check Settings → Editor → General → Gutter Icons → Should see all 11 providers listed

### 5. Test Settings Integration
- Settings → Editor → General → Gutter Icons
- Toggle individual providers on/off
- Verify icons appear/disappear accordingly

## What's NOT Implemented Yet

1. **Actual Graph Navigation** - Currently shows notification POC only
   - Need to integrate with existing `DaggerAnalysisService`
   - Open tool window with component-specific graph
   - Highlight selected element in graph

2. **Multi-Graph Selection** - When Component has multiple scopes/graphs
   - Show popup menu to choose which graph to open
   - Remember last selection per component

3. **Component.Factory/Builder Support** - Gutter icons on nested interfaces
   - `@Component.Factory` and `@Component.Builder` annotations
   - Navigate to parent component graph

4. **@Inject Usage Navigation** (Task 2) - Not started
   - Gutter icons on `@Inject` constructors
   - Gutter icons on `@Provides` and `@Binds` methods
   - "Find Usages" integration

5. **Compile-Time Processor** (Task 3) - Not started
   - Dagger SPI `BindingGraphPlugin` implementation
   - Graph generation at build time

6. **Custom Visual Design** (Task 4) - Not started
   - GraphViz styling and colors
   - Node shapes and edge styles

7. **Tool Window with JCEF** (Task 5) - Not started
   - Embedded SVG viewer
   - Bidirectional navigation

8. **Build-Time Index** (Task 6) - Not started
   - JSON index generation
   - Auto-reload on rebuild

## Files Changed

```
src/main/kotlin/com/daggerdiag/gutter/
├── DaggerLineMarkerProvider.kt                      (130 lines)
├── ComponentLineMarkerProvider.kt                   (29 lines)
├── SubcomponentLineMarkerProvider.kt                (29 lines)
├── ModuleLineMarkerProvider.kt                      (29 lines)
├── ContributesAndroidInjectorLineMarkerProvider.kt  (62 lines)
├── HiltLineMarkerProviders.kt                       (142 lines)
└── AnvilLineMarkerProviders.kt                      (111 lines)

src/main/resources/META-INF/plugin.xml               (+35 lines)
```

**Total**: 8 files changed, 572 insertions(+)

## Commit

```
commit 67cdf25
Author: Claude Code
Date: Thu Feb 5 23:22:05 2026 +0000

Add comprehensive gutter icon support for Dagger/Hilt/Anvil annotations
```

## Next Steps

**Immediate (complete Task 1)**:
1. Verify build succeeds when network is restored
2. Test all gutter icons with example code
3. Implement actual graph navigation (replace notification POC)
4. Add Component.Factory/Builder support

**Future Tasks (in priority order)**:
1. Task 2: @Inject usage navigation
2. Task 3: Compile-time processor with Dagger SPI
3. Task 4: Custom visual design
4. Task 6: Build-time index generation
5. Task 5: Tool window with JCEF
