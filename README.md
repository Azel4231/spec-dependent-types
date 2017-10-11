# spec-depedent-types

A small example of using clojure.spec to define dependet types. I implemented the example in two ways.

1. Uses spec simply to validate
2. Uses spec's regex logic ("cat") to parse the data and then validate it

## Changing the order [3 "a" "b" "c"] -> ["a" "b" "c" 3]

Changing the order of the type is interesting because it shows how many parts of the program are tied to the specific order of things and need to change accordingly.
 
1. Requires the spec to change (obviously), the accompanying predicate to change (size-matches?) and all usage points to change.
2. Requires only the spec to change (::annotated-list). Thus this approach is way better as it decouples the rest of the code from the order in which things are defined.

## License

Distributed under the MIT License.
