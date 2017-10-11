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

(s/def ::annotated-list (s/cat          ;; s/cat implies order
                         :elements (s/* any?)
                         :count nat-int?))

(s/conform ::dependent-spec [3 "a" "b" "c"])
;; => {:count 3, :elements ["a" "b" "c"]}





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

(do-something ["a" "b" "c" 3])
;; => Size:  3 , elements:  [a b c]


;; Bonus Question
;; -> what happens, when I decide to change the pattern to ["a" "b" "c" 3] ?!

(s/valid? ::dependent-spec ["a" "b" "c" "d" "e" 5])
;; => true

