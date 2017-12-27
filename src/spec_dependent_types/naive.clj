(ns spec-dependent-types.naive
  (:require [clojure.spec.alpha :as s]))

(defn size-matches? [coll]
  (= (count coll)
     (inc (first coll))))

(s/def ::dependent-spec (s/and sequential?
                               #(nat-int? (first %))   ;; can be sure that it's a sequence
                               size-matches?))         ;; can be sure the first element is a number

(s/valid? ::dependent-spec [5 "a" "b" "c" "d" "e"])
;; => true

(s/valid? ::dependent-spec [3 "a" "b" "c"])
;; => true

(s/valid? ::dependent-spec [4 "singleElement"])
;; => false

(s/explain ::dependent-spec "WrongKindOfData")
;; => val: "WrongKindOfData" fails spec: :spec-depedent-types.naive/dependent-spec predicate: sequential?

(s/explain ::dependent-spec [4 "singleElement"])
;; => val: [4 "singleElement"] fails spec: :spec-depedent-types.naive/dependent-spec predicate: size-matches?

(s/valid? ::dependent-spec [0])
;; => true

(s/conform ::dependent-spec [3 "a" "b" "c"])  
;; => [3 "a" "b" "c"]
;; Conform returns the data unchanged.
;; This is because in this case order is encoded in a predicate ("#(nat-int? (first %))"), not in the spec itself.



(defn do-something [coll]
  (when (not (s/valid? ::dependent-spec coll))
    (throw (RuntimeException.)))
  (let [[count & elements] coll]
    (str "Size: " count ", elements: " elements)))

(do-something [3 "a" "b" "c"])
;; => Size:  3 , elements:  (a b c)







;; What happens if the format changes to ["a" "b" "c" 3]?
#_(defn size-matches? [coll]
  (= (count coll)
     (inc (last coll))))   ;; <--- changed to "last"

#_(s/def ::dependent-spec (s/and sequential?
                               #(nat-int? (last %))   ;; <--- changed to "last"
                               size-matches?)) 

#_(defn do-something [coll]
  (when (not (s/valid? ::dependent-spec coll))
    (throw (RuntimeException.)))
  (let [count (last coll)             ;; <--- can no longer use destructuring, must use "last"
        elements (butlast coll)]      ;; <--- must use "butlast"
    (str "Size: " count ", elements: " elements)))

;; Order dependece is a kind of complexity. It "infects" every usage point.
;; Sadly in this case both parts of the spec as well as every usage in the code was coupled to that specific order.

