# L4 Commons

This is a set of Java libraries for common functionality used within the
open-source projects published by [Level Four AB](https://github.com/LevelFourAB).

## Archived

This project is archived. It has been split up into several smaller libraries:

* [Exobytes](https://github.com/LevelFourAB/exobytes) for serialization
* [Exoconf](https://github.com/LevelFourAB/exoconf) for configuration
* [ylem-io](https://github.com/LevelFourAB/ylem-io) for extended IO types
* [ylem-ids](https://github.com/LevelFourAB/ylem-ids) for id manipulation
* [ylem-types](https://github.com/LevelFourAB/ylem-types) for type manipulation

## License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0),
see the file `LICENSE` and `NOTICE` for details.

## Usage via Maven

The indiviudal parts of this library are available from Maven central:

```xml
<dependency>
  <groupId>se.l4.commons</groupId>
  <artifactId>commons-projectHere</artifactId>
  <version>1.2.0</version>
</dependency>
```

## `commons-id` - Identifiers

The `commons-id` project contains code for working with identifiers, such
as generating them via `LongIdGenerator` and encoding/decoding them using
`LongIdCodec`.

## `commons-io` - I/O utilities

Provides the `Bytes` class which acts as an abstraction over binary data,
also provides `ExtendedDataInput` and `ExtendedDataOutput` that provide
more types and control over data than the regular `DataInput` and `DataOutput`.

This module also provides functional interfaces that throw `IOException`
such as `IoConsumer`, `IoFunction` and `IoSupplier`.

## `commons-types` - Find and create new types

Provides support for finding types, creating instances of types or generating
new types.

## `commons-serialization` - Serialization and deserialization of objects

Streaming serialization of Java objects with support for different formats,
including a binary custom format and a JSON format.

Formats can be defined for any class, using either a custom Serializer
implementation or annotations.

## `commons-config` - Configuration via files

Configuration loading from a lenient config format, including conversion to
objects via serialization.

## `commons-guice` - Extensions for integrating with Guice

Integration for Google Guice, such as providing an `InstanceFactory` that
uses Guice to create objects.
