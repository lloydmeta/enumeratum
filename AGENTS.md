# Enumeratum - AI Agent Guide

This document provides comprehensive guidance for AI agents (Claude, GitHub Copilot, etc.) working on the Enumeratum codebase.

## Project Overview

**Enumeratum** is a type-safe enumeration library for Scala that provides:
- Exhaustive pattern matching with compiler warnings
- 4x faster performance than stdlib Enumeration
- Zero dependencies in core
- Full ScalaJS, ScalaNative, and JVM support
- Rich integrations with popular Scala libraries

**Current Version**: 1.9.4-SNAPSHOT (stable: 1.9.3)
**License**: MIT
**Scala Versions**: 2.12.21, 2.13.18, 3.3.7
**Main Branch**: `master`

## Architecture

### Module Structure

```
enumeratum/
├── macros/                    # Compile-time value discovery (core dependency)
├── enumeratum-core/           # Base Enum and ValueEnum traits
├── enumeratum-test/           # Shared test models
└── enumeratum-{integration}/  # Library integrations (play, circe, cats, etc.)
```

### Three-Layer Architecture

1. **Macros Layer** (`/macros`)
   - Compile-time discovery of enum values via `findValues`
   - Scala 2: Uses `scala.reflect.macros`
   - Scala 3: Uses `scala.quoted` API
   - Version-specific: `/src/main/scala-2/` and `/src/main/scala-3/`

2. **Core Layer** (`/enumeratum-core`)
   - `EnumEntry`: Base trait with `entryName`
   - `Enum[A]`: String-based lookup (withName, indexOf)
   - `ValueEnum[ValueType, EntryType]`: Value-based lookup (withValue)
   - Specialized: IntEnum, LongEnum, ShortEnum, ByteEnum, CharEnum, StringEnum

3. **Integration Layer** (various modules)
   - JSON: play-json, circe, argonaut, json4s
   - Web: play (bindables, forms)
   - Database: slick, quill, doobie, reactivemongo
   - Typeclasses: cats, scalacheck

### Cross-Platform Support

Most modules use `crossProject(JSPlatform, JVMPlatform, NativePlatform)` with `CrossType.Pure`:
- JVM/JS/Native share identical source code
- No platform-specific code in most modules
- Exceptions: Play (JVM only), some integrations (JVM+JS only)

## Development Conventions

### Code Style

**Formatter**: Scalafmt 3.8.6 (config: `.scalafmt.conf`)
```bash
sbt scalafmtAll scalafmtSbt
```

**Settings**:
- Max column: 100
- Style: defaultWithAlign
- Dialect: Scala 2.13 syntax targeting Scala 3 (Scala213Source3)

### Compiler Flags

**Always**: `-Xfatal-warnings` (all warnings are errors)

**Scala 2 Key Flags**:
- `-language:higherKinds`, `-language:implicitConversions`
- `-Ywarn-dead-code`, `-Ywarn-value-discard`
- `-Xlint`

**Scala 3 Key Flags**:
- `-Yretain-trees` (REQUIRED for ValueEnums to work)

### Naming Conventions

**Projects**:
- Main modules: `enumeratum-{feature}` (e.g., `enumeratum-circe`)
- Aggregates: `{feature}-aggregate` (e.g., `circe-aggregate`)
- Cross-projects auto-suffixed: `coreJVM`, `coreJS`, `coreNative`

**Packages**:
- Main: `enumeratum`
- Values: `enumeratum.values`

**Traits**:
- Base: `Enum[A]`, `ValueEnum[V, E]`
- Entries: `EnumEntry`, `IntEnumEntry`, etc.
- Integrations: Library-prefixed (e.g., `PlayEnum`, `CirceEnum`)

### Version-Specific Code

**Source Layout**:
```
/src/main/scala/        # Common code (all versions)
/src/main/scala-2/      # Scala 2.x only
/src/main/scala-3/      # Scala 3.x only
/compat/src/main/scala-2.12/  # Scala 2.12 specific
/compat/src/main/scala-2.13/  # Scala 2.13+ specific
```

**Pattern**: Isolate version-specific code in separate directories rather than using version checks.

## Common Patterns

### Sealed Trait Enum Pattern

```scala
sealed trait MyEnum extends EnumEntry
object MyEnum extends Enum[MyEnum] {
  val values = findValues  // Macro discovers case objects at compile-time

  case object Value1 extends MyEnum
  case object Value2 extends MyEnum
}
```

### ValueEnum Pattern

```scala
sealed abstract class MyEnum(val value: Int) extends IntEnumEntry
object MyEnum extends IntEnum[MyEnum] {
  val values = findValues  // Validates uniqueness at compile-time

  case object Value1 extends MyEnum(1)
  case object Value2 extends MyEnum(2)
}
```

### Name Transformations

Stackable traits for automatic name transformation:
```scala
sealed trait Color extends EnumEntry with Snakecase
// or: Uppercase, Lowercase, UpperSnakecase, Hyphencase, etc.
```

**Important**: All transformations cached in lazy vals for performance.

### Error Handling

- **Throwing**: `withName`, `withValue`
- **Option**: `withNameOption`, `withValueOpt`
- **Either**: `withNameEither`, `withValueEither` (returns `NoSuchMember[A]`)

## Build System (SBT)

### Key Files

- `/build.sbt` - Main build (898 lines)
- `/project/plugins.sbt` - SBT plugins
- `/project/Versions.scala` - Version management
- `/project/CoreJVMTest.scala` - Generated test logic

### Cross-Compilation

```bash
# Test specific version
sbt "++2.13.18 test"

# Test all versions
sbt "+test"

# Compile for all platforms
sbt "coreJVM/test" "coreJS/test" "coreNative/test"
```

### Local Version Testing

To test with local macro changes (important when modifying macros):
```bash
sbt -Denumeratum.useLocalVersion "++2.13.18 test"
```

This is set in CI via `SBT_OPTS`.

### Publishing

**Per-Module**:
```bash
sbt "project circe-aggregate" +clean +publishSigned
```

**All Modules**:
```bash
sbt +publishSigned
```

## Testing

### Framework

- **ScalaTest** 3.2.19 with `AnyFunSpec` and `Matchers`
- **ScalaCheck** 1.18.0 for property testing

### Test Structure

**Core Tests**:
- Manual: `/enumeratum-core/src/test/scala/enumeratum/`
- Generated: `/enumeratum-core-jvm-tests/` (100 random enums via macro)

**Integration Tests**:
- Each module has `/src/test/` directory
- Uses shared models from `/enumeratum-test/`

**Version-Specific Tests**:
- `/src/test/scala-2/` - Scala 2 only
- `/src/test/scala-3/` - Scala 3 only
- `/compat/src/test/scala-2.13/`, etc.

### Running Tests

```bash
# All tests for a version
sbt "++2.13.18 test"

# Specific module
sbt "circe/test"

# With coverage (Scala 2.13 only)
sbt coverage "++2.13.18 test" coverageReport coverageAggregate

# Cross-platform
sbt "coreJVM/test" "coreJS/test" "coreNative/test"
```

### Code Coverage

- **Tool**: Scoverage (only on JVM, Scala 2.13)
- **Exclusions**: Macros and internal utilities
  ```scala
  coverageExcludedPackages := """enumeratum\.EnumMacros;enumeratum\.ContextUtils;enumeratum\.ValueEnumMacros"""
  ```

## CI/CD (GitHub Actions)

### Workflow

**File**: `.github/workflows/ci.yml`

**Matrix**:
- Java 11
- Scala: 2.12.21, 2.13.18, 3.3.7

**Environment**:
```yaml
SCALAJS_TEST_OPT: full  # Full optimization for JS tests
SBT_OPTS: -Denumeratum.useLocalVersion  # Test with local macros
```

### CI Steps (for all versions)

1. Checkout
2. Setup Scala + Java 11
3. Cache Coursier dependencies
4. **For Scala 2.13.18**:
   - Format check: `scalafmtCheck`, `scalafmtSbtCheck`
   - Compile: `test:compile`, `test:doc`
   - Coverage: `coverage`, `test`, `coverageReport`, `coverageAggregate`
   - Upload to Codecov
5. **For other versions**:
   - Compile: `test:compile`, `test:doc`
   - Test: `test`

## Critical Gotchas

### 1. Macro Changes Require Local Testing

When modifying `/macros/`, ALWAYS test with:
```bash
sbt -Denumeratum.useLocalVersion test
```

Without this flag, tests may use published macro artifacts instead of your changes.

### 2. Scala 3 Requires `-Yretain-trees`

ValueEnums break without this flag. It's already in build.sbt for Scala 3, but important when adding new modules.

### 3. Cross-Version Compatibility

When modifying core traits:
- Check both `/src/main/scala-2/` AND `/src/main/scala-3/`
- Update macro implementations in both versions
- Test with `sbt "+test"` for all versions

### 4. JSON4S on Scala 3

JSON4S integration is **disabled** on Scala 3 due to upstream compatibility issues. Don't try to fix without checking json4s status.

### 5. Performance-Critical Paths

- `withName` and `withValue` are hot paths
- All lookups use pre-built maps (lazy vals)
- Name transformations cached in lazy vals
- Never add synchronization (unlike stdlib Enumeration)

### 6. Compile-Time Uniqueness

ValueEnums enforce unique values at compile-time via macros. Changes to value validation must work in both Scala 2 and 3 macro implementations.

### 7. CrossType.Pure

Most projects use `CrossType.Pure` meaning JVM/JS/Native share **identical** source. Platform-specific code must go in separate projects or compat layers.

## Common Workflows

### Adding a New Integration Module

1. **Create crossproject**:
   ```scala
   lazy val newIntegration = crossProject(JSPlatform, JVMPlatform, NativePlatform)
     .crossType(CrossType.Pure)
     .in(file("enumeratum-new-integration"))
     .dependsOn(core)
   ```

2. **Add to integrationProjectRefs** in build.sbt

3. **Create aggregate**:
   ```scala
   lazy val `new-integration-aggregate` =
     aggregateProject("new-integration", newIntegrationJVM, newIntegrationJS)
   ```

4. **Implement integration trait**:
   ```scala
   package enumeratum

   trait NewIntegrationEnum[A <: EnumEntry] { this: Enum[A] =>
     // Integration-specific functionality
   }
   ```

5. **Add tests** using models from `enumeratum-test`

6. **Update README.md** with usage examples

7. **Test all versions**: `sbt "project new-integration-aggregate" +test`

### Modifying Core Enums

1. **Read existing code**:
   ```bash
   # Core enum trait
   /enumeratum-core/src/main/scala/enumeratum/Enum.scala

   # Value enum trait
   /enumeratum-core/src/main/scala/enumeratum/values/ValueEnum.scala
   ```

2. **Check version-specific code**:
   ```bash
   /enumeratum-core/src/main/scala-2/
   /enumeratum-core/src/main/scala-3/
   ```

3. **Update both macro implementations**:
   ```bash
   /macros/src/main/scala-2/enumeratum/EnumMacros.scala
   /macros/src/main/scala-3/enumeratum/EnumMacros.scala
   ```

4. **Test thoroughly**:
   ```bash
   sbt -Denumeratum.useLocalVersion "+test"
   ```

### Fixing a Bug

1. **Reproduce** with a test case
2. **Identify** which layer: macros, core, or integration
3. **Check version-specific** code if behavior differs across Scala versions
4. **Fix** in all relevant places (Scala 2 and 3 if needed)
5. **Test** across all versions: `sbt "+test"`
6. **Check coverage** (2.13): `sbt coverage "++2.13.18 test" coverageReport`
7. **Format**: `sbt scalafmtAll`

### Optimizing Performance

1. **Benchmark first**:
   ```bash
   sbt "+benchmarking/'jmh:run -i 10 -wi 10 -f3 -t 1 YourBenchmark'"
   ```

2. **Check hot paths**:
   - `withName`, `withValue`: O(1) map lookups
   - `values`: Already pre-computed
   - Name transformations: Cached in lazy vals

3. **Compare with stdlib**:
   See `/benchmarking/` for JMH benchmark setup

## File Locations Quick Reference

### Core Files
- Base enum trait: `/enumeratum-core/src/main/scala/enumeratum/Enum.scala`
- Base entry trait: `/enumeratum-core/src/main/scala/enumeratum/EnumEntry.scala`
- Value enum trait: `/enumeratum-core/src/main/scala/enumeratum/values/ValueEnum.scala`
- Value entry traits: `/enumeratum-core/src/main/scala/enumeratum/values/ValueEnumEntry.scala`

### Macros
- Scala 2 enum macros: `/macros/src/main/scala-2/enumeratum/EnumMacros.scala`
- Scala 3 enum macros: `/macros/src/main/scala-3/enumeratum/EnumMacros.scala`
- Scala 2 value macros: `/macros/src/main/scala-2/enumeratum/ValueEnumMacros.scala`
- Scala 3 value macros: `/macros/src/main/scala-3/enumeratum/ValueEnumMacros.scala`

### Tests
- Core enum tests: `/enumeratum-core/src/test/scala/enumeratum/EnumSpec.scala`
- Value enum tests: `/enumeratum-core/src/test/scala/enumeratum/values/ValueEnumSpec.scala`
- Test models: `/enumeratum-test/src/main/scala/`
- Generated tests: `/enumeratum-core-jvm-tests/`

### Configuration
- Build: `/build.sbt`
- Formatter: `/.scalafmt.conf`
- CI: `/.github/workflows/ci.yml`
- Plugins: `/project/plugins.sbt`

## Key Design Principles

1. **No Dependencies**: Core has zero external dependencies
2. **No Reflection**: All discovery at compile-time via macros
3. **Type Safety**: Exhaustive pattern matching, compile-time validation
4. **Performance**: Pre-computed maps, lazy vals, no synchronization
5. **Cross-Platform**: Pure source sharing across JVM/JS/Native
6. **Integration Friendly**: Mix-in traits for library-specific functionality
7. **Backward Compatible**: Careful version management, deprecation warnings

## Common Questions

### Q: Why separate macro implementations for Scala 2 and 3?
A: Macro APIs are completely different. Scala 2 uses reflection API, Scala 3 uses quotes API.

### Q: Why use lazy vals everywhere?
A: Avoids initialization order issues. Enums can be used before companion object fully initialized.

### Q: Why no JSON integration in core?
A: Zero dependencies principle. All integrations are optional, separate modules.

### Q: Can I add new value types (e.g., BigDecimalEnum)?
A: Yes, follow pattern in `/enumeratum-core/src/main/scala/enumeratum/values/`. Ensure compile-time uniqueness validation in macros.

### Q: How does cross-compilation work?
A: SBT crossProject plugin. Most modules use CrossType.Pure (shared source). Version-specific code in `/scala-2/` and `/scala-3/` directories.

### Q: Why is coverage only on Scala 2.13?
A: Scoverage has best support for 2.13. Coverage on JS/Native is unreliable.

## Support and Resources

- **Repository**: https://github.com/lloydmeta/enumeratum
- **README**: Comprehensive user guide with all examples
- **Issues**: GitHub Issues for bug reports and feature requests
- **License**: MIT

---

**Last Updated**: 2026-02-01 (for version 1.9.4-SNAPSHOT)
