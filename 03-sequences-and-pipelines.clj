; In Clojure, many collections can be treated uniformly.
[1 2 3]     ;; vector
'(1 2 3)    ;; list
#{1 2 3}    ;; set
{:a 1 :b 2} ;; map

; They are different concrete data structures,
; but many functions operate on them through the sequence abstraction.

; SEQUENCE BASICS
; A sequence is a logical view over a collection.
(seq [1 2 3])
(seq []) ;; nil

; In Clojure, empty collections are truthy.
(if []
  :yes
  :no)

; But (seq []) returns nil, so:
(if (seq [])
  :has-items
  :empty)

; This is a common idiom:
(when (seq [])
  ...)

; first, rest, next
(def xs [1 2 3])

(first xs) ;; returns the first item => 1
(rest xs)  ;; use rest in recursive algorithms => (2, 3)
(next xs)  ;; use seq/next when emptiness matters => (2, 3)

(rest [1]) ;; ()
(next [1]) ;; nil

; map
(map inc [1 2 3])

; map is lazy
; It doesn't immediately build a full vector. It returns a lazy sequence.
(type (map inc [1 2 3]))

; if you want a vector
(mapv inc [1 2 3])

; filter
(filter odd? [1 2 3 4 5])
(filterv even? [1 2 3 4 5])

; remove
; remove is the opposite of filter
(remove odd? [1 2 3 4 5]) ;; => (2 4)

(defn failed? [request]
  (= (:status request) :failed))

(remove failed? [{:status :failed}
                 {:status :success}])

; keep
; keep is useful when you want both transform and remove nil results.
(keep identity [1 nil 2 nil 3])

(def requests
  [{:id 1 :model :kimi-k2-thinking :error nil}
   {:id 2 :model :deepseek-v3.2 :error "timeout"}
   {:id 3 :model :deepseek-v3.2 :error nil}
   {:id 4 :model :qwen3-coder :error "rate limit"}])

; extract only errors
(keep :error requests)

; without keep you might write
(->> requests
     (map :error)
     (remove nil?))

; some
; some returns the first truthy result.
(some even? [1 3 5 8 9]) ;; => true
(some :error requests)   ;; => timeout

(if (some :error requests)
  :has-errors
  :ok) ;; => :has-errors

; if you need strict boolean
(boolean (some :error requests))

; every
(every? number? [1 2 3])    ;; => true
(every? :success? requests) ;; => false

; not-any?
(not-any? failed? requests) ; no requests failed

; not-every?
(not-every? failed? requests) ; not all requests succeeded

; prefer not-any? and not-every? over wrapping with not:
(not (some failed? requests))

; sort-by
(sort-by :tokens requests)   ; asc
(sort-by :tokens > requests) ; desc

(->> requests
     (sort-by :tokens >)
     (take 3))

; take, drop, take-while, drop-while

(take 3 [1 2 3 4 5])
(drop 3 [1 2 3 4 5])
(take-while #(< % 4) [1 2 3 4 5 1])
(drop-while #(< % 4) [1 2 3 4 5 1])

; partition, partition-all
(partition 2 [1 2 3 4 5]) ; notice: 5 is dropped
(partition-all 2 [1 2 3 4 5])

; this is useful in batching
; split requests into batches of 100
(partition-all 100 requests)

; group-by
(group-by :model requests)

; frequencies
(frequencies [:a :b :a :c :b :a])

(->> requests
     (map :model)
     frequencies)

; juxt
; juxt creates a function that applies multiple functions to the same input
((juxt :id :model :tokens)
 {:id 1 :model :deepseek-v3.2 :tokens 1200}) ;; => [1 :deepseek-v3 1200]

; useful with sort-by
; this sorts by model first, then by tokens
(sort-by (juxt :model :tokens) requests)
