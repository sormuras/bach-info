# bach-info

Bach's default repository for external asset information files.

## External Modules Locator

An external modules locator information file maps a set of Java module names to their external modular JAR file locations.

A named external modules locator uses the `<NAME>.modules-locator.properties` file name pattern.
Such files are stored in the [.bach/external-modules](.bach/external-modules) folder.
A locator is imported from this default repository by running `bach import <NAME>`.

## External Tool Directory

A tool directory information file lists all files composing a complete tool installation.
Each file information maps a local file name to its remote file location.

A named tool directory file uses the `<NAME>.tool-directory.properties` file name pattern.
Such files are stored in [.bach/external-tools](.bach/external-tools) folder.
A tool directory information file and all  are installed from this default repository by running `bach install <NAME>`.

## Example Usage

View:
- [junit@5.9.1](.bach/external-modules/junit@5.9.1.modules-locator.properties)
- [jreleaser@1.2.0](.bach/external-tools/jreleaser@1.2.0.tool-directory.properties)

Use:
```shell
> bach import junit@5.9.1
> bach load-module org.junit.jupiter
> bach load-module org.junit.jupiter.api
> bach load-module org.junit.jupiter.engine
...
> bach load-modules org.junit.platform.console
```

Result:
```text
.bach/external-modules
.bach/external-tools
.bach/external-tools/google-java-format@1.15.0
.bach/external-tools/google-java-format@1.15.0/google-java-format-1.15.0-all-deps.jar
.bach/external-tools/google-java-format@1.15.0/README.md
.bach/external-tools/google-java-format@1.15.0.tool-directory.properties
```
