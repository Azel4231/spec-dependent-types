(ns spec-depedent-types.intro)

;; "Can you model dependent types in clojure.spec?"

;; What are dependent types?
;; -> Parts of the data depend on each other
;; -> The structure of the data depends on certain values in the data itself

   [3 "A" "B" "C"]
;; [count & elements]

;; Valid:
[3 "A" "B" "C"]
[1 "X"]
[0]

;; Invalid:
[5 "a"]
[1 "x" "y" "z"]

;; is this useful?
;; -> Yes, I think so
;; -> Checksum based data (ISBN, IBAN)
;; -> is this valid?    "ISBN 0-321-12521-5"   
;;                                        ^
;;                                     checksum (cross-sum mod 11)

;; Most type systems can't model this
;; -> Java certainly can't
;; -> Idris can (I think)

;; Spec is not a type system
;; -> It can't check this at compile-time
;; -> It works at runtime -> should be easy (as in most languages)
[3 "A" "B" "C"]

;; I tried to model this in two ways
;; -> Learned more spec syntax
;; -> Learned about behavior of the "and" macro


