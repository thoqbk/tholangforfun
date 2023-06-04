# ThoLangForFun programming language

An effort to implement another programming language for fun

Learn about how to implement an intepreter:
- [Part 1: High-Level Overview](./write-an-interpreter-high-level-overview.md)
- [Part 2: Implementation](./write-an-interpreter-implementation.md)

Sample code:

Function & recursion:

```
let fib = function (n) {
    if (n == 0) {
        return 0;
    }
    if (n == 1) {
        return 1;
    }
    return fib(n - 1) + fib(n - 2);
};

fib(8);
```

While loop:

```
let n = 10;
let message = "I am counting ";
while (n > 0) {
    message = message + n + " ";
    n = n - 1;
}
put message;
```

More [examples](./examples/)

## Build

Prerequisites:
- [Java 16+](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/install.html)

Move to the root directory of the project then run the folllowing command

```
mvn clean compile assembly:single && cp -rf ./target/tl4f.jar .
```

The output jar file is `tl4f.jar` in the root folder

## Run

Create .tl4f source file then run:

```
java -jar tl4f.jar <source file>.tl4f
```

Or checkout examples in [examples](./examples/) folder e.g. 

```
java -jar tl4f.jar examples/put.tl4f
```

Result:
```
Hello ThoLangForFun
0
1
2
3
4
5
6
7
8
9
10
Exit with result null
```
