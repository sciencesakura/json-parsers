# JJsonp

A toy JSON parser written in Java.

## Requirements

- Java 22+

## How to build

To build the project, run the following command:

```shell
./mvnw -P shade clean package
```

To build a native image, run the following command:

```shell
./mvnw -P native clean package
```

After building the project, you can find the `jjsonp.jar` or `jjsonp` in the `cli/target` directory.
