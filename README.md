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
