# bach-register

Bach's default repository for external asset index files.

## External Library Index Files

A library maps a set of Java module names to their external modular JAR file locations.

A library named `NAME` ...
- ... uses the `NAME.library.properties` index file name pattern.
- ... is stored in the [.bach/external-modules](.bach/external-modules) folder.
- ... is imported by running `bach import NAME [--from REGISTER]`.

## External Tool Index Files

A tool index file maps a set of local file paths to their remote file locations.

A tool named `NAME` ...
- ... uses the `NAME.tool.properties` index file name pattern
- ... is stored in [.bach/external-tools](.bach/external-tools) folder.
- ... is installed by running `bach install NAME [--from REGISTER]`.
