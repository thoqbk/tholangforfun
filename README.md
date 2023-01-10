# ThoLangForFun programming language

An effort to implement a simple programming language for fun

Sample code:

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

## Build

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

Or checkout examples in `examples` folder e.g. 

```
java -jar target/tl4f.jar examples/put.tl4f
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
null
```