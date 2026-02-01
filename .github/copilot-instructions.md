# GitHub Copilot Instructions for Enumeratum

This is a type-safe enumeration library for Scala supporting Scala 2.12, 2.13, 3.3 across JVM, ScalaJS, and ScalaNative.

**IMPORTANT**: Read [AGENTS.md](../AGENTS.md) for comprehensive project documentation.

## Quick Context

- **Build**: SBT with cross-compilation
- **Formatting**: Scalafmt (run `sbt scalafmtAll`)
- **Testing**: ScalaTest + ScalaCheck
- **CI**: GitHub Actions with Java 11

## Key Patterns

### Sealed Trait Enum
```scala
sealed trait MyEnum extends EnumEntry
object MyEnum extends Enum[MyEnum] {
  val values = findValues
  case object Value1 extends MyEnum
}
```

### ValueEnum
```scala
sealed abstract class MyEnum(val value: Int) extends IntEnumEntry
object MyEnum extends IntEnum[MyEnum] {
  val values = findValues
  case object Value1 extends MyEnum(1)
}
```

## Critical Rules

1. **Version-Specific Code**: Use `/src/main/scala-2/` and `/src/main/scala-3/` directories
2. **Macro Testing**: Test macro changes with: `sbt -Denumeratum.useLocalVersion test`
3. **Cross-Platform**: Most modules use `CrossType.Pure` (shared JVM/JS/Native source)
4. **Fatal Warnings**: `-Xfatal-warnings` is enabled; all warnings must be fixed
5. **Format Before Commit**: Always run `sbt scalafmtAll` before committing

## Common Commands

```bash
# Test all versions
sbt "+test"

# Test specific version
sbt "++2.13.18 test"

# Format code
sbt scalafmtAll

# Test with local macros
sbt -Denumeratum.useLocalVersion test
```

## File Locations

- Core: `/enumeratum-core/src/main/scala/enumeratum/`
- Macros: `/macros/src/main/scala-{2,3}/enumeratum/`
- Tests: `/enumeratum-core/src/test/scala/enumeratum/`
- Build: `/build.sbt`

For complete details, see [AGENTS.md](../AGENTS.md).
