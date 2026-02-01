# Contributing to Enumeratum

Thank you for your interest in contributing to Enumeratum! This guide will help you get started with the development environment and contribution workflow.

## Table of Contents

1. [Development Environment Setup](#development-environment-setup)
2. [Building and Testing](#building-and-testing)
3. [IDE Setup](#ide-setup)
4. [Understanding the Project Structure](#understanding-the-project-structure)
5. [Working with Macros](#working-with-macros)
6. [Contribution Workflow](#contribution-workflow)
7. [Code Style](#code-style)

## Development Environment Setup

### Prerequisites

- **Java**: Version 11 or higher (Java 11 is used in CI)
- **SBT**: The project uses SBT as the build tool
- **Scala**: The project supports Scala 2.12.21, 2.13.18, and 3.3.7

### Quick Start

The simplest way to get started is to follow the workflow used by CI:

```bash
# Clone the repository
git clone https://github.com/lloydmeta/enumeratum.git
cd enumeratum

# Build and test with a specific Scala version
sbt "++2.13.18 test:compile"
sbt "++2.13.18 test"

# Or test across all supported Scala versions
sbt "+test"
```

### The `useLocalVersion` Flag

The project uses a special system property `-Denumeratum.useLocalVersion` that changes how dependencies are resolved:

**What it does:**
- When enabled, projects depend on local (unpublished) versions of `enumeratum-macros` and other core modules
- When disabled (default), projects use the published versions from Maven Central

**Why it's needed:**
- Primarily for testing macro changes that haven't been published yet
- Ensures that when you modify the macro code, your tests use your local changes rather than the published artifacts
- Essential when working on the core functionality of Enumeratum

**How to use it:**

```bash
# Enable for a single SBT session
sbt -Denumeratum.useLocalVersion test

# Or set it as an environment variable for multiple sessions
export SBT_OPTS="-Denumeratum.useLocalVersion"
sbt test
```

**Note:** The CI system always uses this flag (set via `SBT_OPTS` in `.github/workflows/ci.yml`) to ensure all tests run against the current codebase.

## Building and Testing

### Compiling the Project

```bash
# Compile for a specific Scala version
sbt "++2.13.18 compile"

# Compile test sources
sbt "++2.13.18 test:compile"

# Generate API documentation
sbt "++2.13.18 test:doc"

# Compile for all Scala versions
sbt "+compile"
```

### Running Tests

```bash
# Run all tests for a specific Scala version
sbt "++2.13.18 test"

# Run tests for a specific module
sbt "enumeratum-core/test"

# Run tests across all Scala versions (as CI does)
sbt "+test"

# Run tests with local version (important when modifying macros)
sbt -Denumeratum.useLocalVersion "++2.13.18 test"
```

### Testing Specific Platforms

Enumeratum supports JVM, ScalaJS, and ScalaNative. To test specific platforms:

```bash
# Test JVM version
sbt "coreJVM/test"

# Test ScalaJS version
sbt "coreJS/test"

# Test ScalaNative version
sbt "coreNative/test"
```

### Code Coverage

Code coverage is generated for Scala 2.13 only:

```bash
sbt coverage "++2.13.18 test" coverageReport coverageAggregate
```

Coverage reports will be available in `target/scala-2.13/scoverage-report/`.

### Formatting

The project uses Scalafmt for code formatting:

```bash
# Check if code is properly formatted
sbt scalafmtCheck scalafmtSbtCheck

# Format all code
sbt scalafmtAll scalafmtSbt
```

**Always format your code before committing!**

## IDE Setup

### IntelliJ IDEA

⚠️ **Known Limitations**: The project maintainer does not currently use IntelliJ IDEA for this project, and there are known issues with project import, particularly around dependency resolution.

**Common Issues:**
- Unresolved dependencies errors during import
- Problems with `enumeratum-play / update` task
- Issues with `ssExtractDependencies` (a custom task)
- Incorrect module setup when importing via BSP

**Workarounds:**

1. **Use command-line SBT for building and testing** rather than relying on IDE build:
   ```bash
   # Build once to download dependencies
   sbt -Denumeratum.useLocalVersion "++2.13.18 compile"
   ```

2. **Import the project after initial compilation:**
   - Run `sbt compile` first from the command line
   - Then import the project into IntelliJ using the SBT importer
   - If you encounter errors, try importing with BSP instead

3. **Run tests via SBT shell** within IntelliJ rather than using the IDE's test runner

4. **For specific Scala versions:**
   - The project may import more successfully for Scala 2.13 than other versions
   - Try setting `scalaVersion := "2.13.18"` temporarily if import fails

### VS Code / Metals

The project should work better with VS Code and Metals:

1. Install the Metals extension
2. Open the project folder
3. Metals should automatically detect the SBT build and import it
4. Use the build target selector to choose specific Scala versions

### Alternative: Command-Line Development

Many contributors successfully use command-line tools with a text editor:

```bash
# Keep SBT running in one terminal
sbt

# In the SBT shell, run tests continuously
~test

# Or use specific version
++2.13.18
~test
```

## Understanding the Project Structure

### Module Organization

```
enumeratum/
├── macros/                              # Compile-time value discovery
│   ├── src/main/scala-2/               # Scala 2.x macro implementations
│   └── src/main/scala-3/               # Scala 3.x macro implementations
├── enumeratum-core/                     # Core Enum and ValueEnum traits
├── enumeratum-test/                     # Shared test models
├── enumeratum-core-jvm-tests/           # Generated test project
├── enumeratum-{integration}/            # Various library integrations:
│   ├── enumeratum-circe/               # Circe JSON support
│   ├── enumeratum-play/                # Play Framework support
│   ├── enumeratum-cats/                # Cats typeclass instances
│   └── ...                             # And many more
├── benchmarking/                        # JMH benchmarks
└── build.sbt                           # Main build definition
```

### Cross-Platform Support

Most modules use `crossProject` with `CrossType.Pure`, meaning the same source code is shared across JVM, ScalaJS, and ScalaNative. Platform-specific code (if needed) goes in version-specific directories.

### Version-Specific Code

When code differs between Scala versions, it's organized in version-specific directories:

```
src/main/scala/           # Shared code for all versions
src/main/scala-2/         # Scala 2.x only
src/main/scala-3/         # Scala 3.x only
```

## Working with Macros

The `macros` module is a core dependency that provides compile-time discovery of enum values via `findValues`.

### Testing Macro Changes

**Important:** When you modify macro code, you must use `-Denumeratum.useLocalVersion`:

```bash
# Always use this flag when working on macros
sbt -Denumeratum.useLocalVersion "++2.13.18 test"
```

Without this flag, tests will use the published macro artifacts instead of your local changes, and you won't see the effect of your modifications.

### Scala 2 vs Scala 3 Macros

Macro implementations are completely different between Scala 2 and 3:

- **Scala 2**: Uses `scala.reflect.macros` API (`/macros/src/main/scala-2/`)
- **Scala 3**: Uses `scala.quoted` API (`/macros/src/main/scala-3/`)

When modifying macro logic, you typically need to update both implementations.

## Contribution Workflow

### Before You Start

1. **Check existing issues**: Look for existing issues or create a new one to discuss your proposed changes
2. **For new integrations**: Please open an issue first to discuss whether the integration is a good fit for the project

### Development Process

1. **Fork and clone** the repository
2. **Create a feature branch** from `master`:
   ```bash
   git checkout -b feature/my-contribution
   ```

3. **Make your changes**:
   - Write code following the existing style
   - Add tests for new functionality
   - Update documentation if needed

4. **Test your changes**:
   ```bash
   # Test with the version you're targeting
   sbt -Denumeratum.useLocalVersion "++2.13.18 test"
   
   # Ideally test across all versions
   sbt -Denumeratum.useLocalVersion "+test"
   ```

5. **Format your code**:
   ```bash
   sbt scalafmtAll scalafmtSbt
   ```

6. **Commit and push**:
   ```bash
   git add .
   git commit -m "Description of your changes"
   git push origin feature/my-contribution
   ```

7. **Create a Pull Request** on GitHub

### What to Include in Your PR

- Clear description of what the change does
- Reference to any related issues
- Tests that verify the new functionality
- Updated documentation if you're adding new features
- Confirmation that `sbt +test` passes
- Confirmation that code is formatted (`sbt scalafmtCheck`)

## Code Style

### Formatting

The project uses Scalafmt 3.8.6 with configuration in `.scalafmt.conf`:

- Max column: 100
- Style: defaultWithAlign
- Dialect: Scala 2.13 syntax targeting Scala 3

### Compiler Settings

- **Fatal warnings**: `-Xfatal-warnings` is enabled, so all warnings are treated as errors
- **Scala 3 requirement**: The `-Yretain-trees` flag is required for ValueEnums

### General Guidelines

- Follow the existing code style in the module you're working on
- Add comments only when they clarify complex logic (match the style of existing comments)
- Use existing libraries when possible; avoid adding new dependencies to core modules
- Prefer immutability and functional programming patterns
- Ensure your code works across all supported Scala versions unless it's version-specific

## Testing Locally Like CI Does

To replicate what CI does locally:

```bash
# Set the same environment variable CI uses
export SBT_OPTS="-Denumeratum.useLocalVersion"

# For Scala 2.13 (includes formatting checks and coverage)
sbt "++2.13.18 scalafmtCheck" "++2.13.18 scalafmtSbtCheck"
sbt "++2.13.18 test:compile" "++2.13.18 test:doc"
sbt coverage "++2.13.18 test" coverageReport coverageAggregate

# For other Scala versions
sbt "++2.12.21 test:compile" "++2.12.21 test:doc" "++2.12.21 test"
sbt "++3.3.7 test:compile" "++3.3.7 test:doc" "++3.3.7 test"
```

## Getting Help

- **Issues**: For bugs, feature requests, or questions, open an issue on [GitHub](https://github.com/lloydmeta/enumeratum/issues)
- **Gitter**: Join the conversation on [Gitter](https://gitter.im/lloydmeta/enumeratum)
- **Documentation**: Check the main [README](README.md) for usage examples and integration guides

## License

By contributing to Enumeratum, you agree that your contributions will be licensed under the MIT License.
