# Wsp Lang
Interpreter/runtime for a lisp-like language.

### Usage:
```
$ ./gradlew jar
$ java -jar build/libs/Wsp-1.0-SNAPSHOT.jar
Wsp 0.1.

wsp>
```

### Repl:
```
$ java -jar build/libs/Wsp-1.0-SNAPSHOT.jar
Wsp 0.1.

wsp> (+ 7 9)
=> 16

wsp> (load "std.wsp")
=> <Function (a b) (or (= a b) (> a b))>

wsp> (define (div a b) (/ a b))
=> <Function (a b) (/ a b)>

wsp> (div 100.0 6)
=> 16.666666666666668

wsp> (exit)

Process finished with exit code 0
```

### Script:
```
$ java -jar build/libs/Wsp-1.0.SNAPSHOT.jar example.wsp
"Fibonacci sequence - 0 1 2 3 4 5 6"
0
1
1
2
3
5
8
```
