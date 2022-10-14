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

## Usage Example

Take a look at the following two external asset information files:

- [junit@5.9.1](.bach/external-modules/junit@5.9.1.modules-locator.properties)
- [jreleaser@1.2.0](.bach/external-tools/jreleaser@1.2.0.tool-directory.properties)

### Import an external modules locator

```shell
bach import junit@5.9.1
```

```text
.bach/external-modules/junit@5.9.1.modules-locator.properties
```

### Load an external module

More correct: Load an external modular JAR file.

```shell
bach load-module org.junit.jupiter.api
```

```text
.bach/external-modules/junit@5.9.1.modules-locator.properties
.bach/external-modules/org.junit.jupiter.api.jar
```

### Load external module tree

More correct: Load a tree of external modular JAR files starting from given root modules.
A root module is usually also an aggregator module, like the `java.se` module is one.

```shell
...
bach load-modules org.junit.jupiter
```

```text
.bach/external-modules/junit@5.9.1.modules-locator.properties
.bach/external-modules/org.apiguardian.api.jar
.bach/external-modules/org.junit.jupiter.api.jar
.bach/external-modules/org.junit.jupiter.engine.jar
.bach/external-modules/org.junit.jupiter.jar
.bach/external-modules/org.junit.jupiter.params.jar
.bach/external-modules/org.junit.platform.commons.jar
.bach/external-modules/org.junit.platform.engine.jar
.bach/external-modules/org.opentest4j.jar
```

### Install an external tool

The `install` tool also downloads all assets in one go:

```shell
bach install jreleaser@1.2.0
```

It first downloads the external tool directory information file.
Then it parses that tool directory information file in order to download all assets into a directory matching the name of the tool being installed.

Files:
```text
.bach/external-modules
.bach/external-tools/jreleaser@1.2.0.tool-directory.properties
.bach/external-tools/jreleaser@1.2.0/jreleaser-tool-provider-1.2.0.jar
.bach/external-tools/jreleaser@1.2.0/README.adoc
```
