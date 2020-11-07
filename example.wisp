(set 'print
  (lambda (a)
    (progn
      (print-raw (to-string a))
      (print-line))))

(print-line)
"plus function - implements '+'"
(set 'plus (lambda (x y) (+ x y)))
(plus 3 5)

(print-line)
"p function - print"
(set 'p (lambda (a) (progn (print a))))
(p "Test printing one thing from new function")
(p "Test printing another thing from new function")

(print-line)
"Head and tail of a list (a b c)"
(set 'l (list "a" "b" "c"))
(head l)
(tail l)

(print-line)
"p2 - print twice."
(set 'p2
  (lambda (a)
    (progn
      (print a)
      (print a))))
(p2 "Test double print")

(print-line)
"fib sequence - 0 1 2 3 4 5 6"
(set 'fib
  (lambda (n)
    (cond
      ((= n 0) 0)
      ((= n 1) 1)
      (t (+ (fib (- n 2)) (fib (- n 1)))))))
(fib 0)
(fib 1)
(fib 2)
(fib 3)
(fib 4)
(fib 5)
(fib 6)

(print-line)
"count - find the number of values in the input."
(set 'count
  (lambda (l)
    (cond
      ((null l) 0)
      (t (+ 1 (count (tail l)))))))
(count '("a" "b" "c"))
(cond (f "false") (t "true"))

(print-line)
"print - pretty-print the input."
'("a" "b" "c")
"hello"

// Not
(print-line)
"not - returns the opposite boolean value for the given input."
(set 'not
  (lambda (a)
    (eq a ())))
(not t)
(not ())

(print-line)
"cons - constructs an object from two other objects."
(cons "hello" "h")
(cons "a" '("b" "c"))

(print-line)
"lambda invocation - applies arguments to a lambda function."
((lambda (x) (+ x x)) 10)

(print-line)
"progn - builtin function for combining multiple statements."
(progn
  "Hello"
  "Hello2")

(print-line)
(print "singleton - returns a list containing the given item.")
(set 'singleton
  (lambda (x)
    (cons x ())))

(set 'reverse
  (lambda (l)
    (reverse-aux l ())))
(set 'reverse-aux
  (lambda (l a)
    (cond
      ((null l) a)
      (t (reverse-aux (tail l) (cons (head l) a))))))

(print-line)
"reverse - returns the reverse of the input."
reverse-aux
(reverse ())
(reverse (list "a"))
(reverse '("a" "b"))
(reverse '("a" "b" "c"))

(print-line)
"palendromep - checks if the input is a palendrome."
(set 'palendromep
  (lambda (p)
    (cond
      ((null l) t)
      ((eq p (reverse p)) t))))
(palendromep '("a" "b" "c"))
(palendromep '("a" "b" "c" "b" "a"))

(print-line)
"Locally-scoped variables - no trace of x or y outside of foo."
(set 'foo
  (lambda (x)
    (progn
      (set 'y x)
      (print x)
      (print y))))
(foo 11)
x
y