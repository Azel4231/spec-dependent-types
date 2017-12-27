(ns spec-dependent-types.nice
  (:require [clojure.spec.alpha :as s]))

(defn size-matches? [m]
  (= (:count m)
     (count (:elements m))))

;; The naive version doesn't work, why?
#_(defn size-matches? [coll]
  (= (count coll)
     (inc (first coll))))

(s/def ::dependent-spec (s/and ::annotated-list
                               size-matches?))

(s/def ::annotated-list (s/cat :count nat-int?         ;; s/cat implies order
                               :elements (s/* any?)))

(s/conform ::dependent-spec [3 "a" "b" "c"])
;; => {:count 3, :elements ["a" "b" "c"]}
;; This is a huge difference to the naive version. In this case the order is encoded in the spec itself using "cat".
;; It enables conform to transform the data into a shape that is order independent (a map).
;; Which in turn makes the code much less order dependent (see "Bonus question" below).



(s/valid? ::dependent-spec [5 "a" "b" "c" "d" "e"])
;; => true

(s/valid? ::dependent-spec [3 "a" "b" "c"])
;; => true

(s/valid? ::dependent-spec [4 "singleElement"])
;; => false

(s/explain ::dependent-spec [4 "singleElement"])
;; => val: {:count 4, :elements ["singleElement"]} fails spec: ::dependent-spec predicate: size-matches?

(s/valid? ::dependent-spec [0])
;; => true



(defn do-something [coll]
  (when (not (s/valid? ::dependent-spec coll))
    (throw (RuntimeException.)))
  (let [conformed (s/conform ::dependent-spec coll)]
    (str "Size: " (:count conformed) ", elements: " (:elements conformed))))

(do-something [3 "a" "b" "c"])
;; => Size:  3 , elements:  [a b c]




;; Bonus Question
;; -> what happens, when I decide to change the pattern to ["a" "b" "c" 3] ?!

;; The only thing that is order dependent is the ::annotated-list spec.
;; Switching places is really easy compared to figuring out last + butlast in the naive version.
#_(s/def ::annotated-list (s/cat :elements (s/* any?)   ;; <--- switched places
                                 :count nat-int? ))     ;; <--- switched places

;; When I wrote this spec I expected to achieve order independence in the usage points (e.g. "do-something").
;; What suprised me was that even accompanying predicates ("size-matches?") are order independent.
;; This is due to the fact that the s/and macro passes on conformed data to the next spec:
                                                         
#_(s/def ::dependent-spec (s/and ::annotated-list      ;; ["a" "b" "c" 3] is passed in and gets conformed to a map
                                 size-matches?))       ;; {:count 3, :elements ["a" "b" "c"]} is passed in,
                                                       ;; which is the same format as before




