# bach-register

Bach's default register for external asset index files.

## External Module Locator Index Files

A locator maps a set of Java module names to their external modular JAR file locations.

A named locator ...
- ... uses the `<NAME>.module-locator.properties` index file name pattern.
- ... is stored in the [.bach/external-modules](.bach/external-modules) folder.
- ... is imported into the current project by running `bach import <NAME> [--from <REGISTER>]`.

An external module `M` can be loaded by running `bach load-module M` if and only if an imported locator contains a mapping for module `M`.

## External Tool Directory Index Files

A tool directory index file maps all required files of that tool to their remote file locations.

A named tool directory index file ...
- ... uses the `<NAME>.tool-directory.properties` index file name pattern
- ... is stored in [.bach/external-tools](.bach/external-tools) folder.
- ... is installed by running `bach install <NAME> [--from REGISTER]`.
