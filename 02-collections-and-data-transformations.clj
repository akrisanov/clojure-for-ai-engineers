(def user {:name "Andrey" :role :engineer})

; In Clojure, keywords can be used to get values from maps.
; This means these two are equivalent:
(get user :name)
(:name user)

(def user
  {:id 1
   :name "Andrey"
   :role :staff-engineer
   :active true})

(:role user)

; You can also provide a default value:
(:country user :unknown)

; The assoc function is used to add or replace a value
(assoc user :country "US") ; returns a new map

; dissoc removes a key
(dissoc user :active)

; update changes a value using a function
(def request
  {:model :deepseek-v3
   :input-tokens 2000
   :output-tokens 8000
   :retries 0})

(update request :retries inc)
(update request :retries + 3)

; NESTED DATA

(def run
  {:id "run-1"
   :status :running
   :usage {:input-tokens 2000
           :output-tokens 8000}
   :model {:name :deepseek-v3
           :provider :local}})

; get nested value
(get-in run [:usage :input-tokens])

; set nested value
(assoc-in run [:model :provider] :openrouter)

; update nested value
(update-in run [:usage :output-tokens] + 500)

; VECTORS
(def models [:deepseek-v3 :kimi-k2-thinking :qwen3-coder])

; get by index
(nth models 0)

; vectors themselves can also be used as functions
(models 1)

; add to the end
(conj models :gemma3)

; SETS
; Sets are useful for membership checks.
(def local-models #{:deepseek-v3 :kimi-k2-thinking :qwen3-coder})

; check membership
(contains? local-models :kimi-k2-thinking)

; idiomatic Clojure often uses the set itself as a function
(local-models :kimi-k2-thinking)
(local-models :gpt-oss-120b) ; nil

; This works well in conditions.
; !!! If set set contains nil or false, this trick becomes less clear.
(if (local-models :kimi-k2-thinking)
  :local
  :external)

; MAPS

(def requests
  [{:id 1 :model :deepseek-v3 :tokens 1200}
   {:id 2 :model :kimi-k2 :tokens 9000}
   {:id 3 :model :qwen-coder :tokens 600}])

; get all token counts
(map :tokens requests)
; ^ this works because :tokens is a function
(:tokens (nth requests 0))

; add derived field
(map (fn [request]
       (assoc request :expensive? (> (:tokens request) 5000)))
     requests)

; ^ result is a **lazy** sequence, not a vector
; if you need a vector
(mapv (fn [request]
        (assoc request :expensive? (> (:tokens request) 5000)))
      requests)

; FILTER

(filter (fn [request]
          (> (:tokens request) 1000))
        requests)

; REDUCE
; reduce is used to accumulate one result from a collection.

; sum all tokens
(reduce (fn [total request]
          (+ total (:tokens request)))
        0
        requests)

; build a map by ID
; this is common in real backend code
(reduce (fn [result request]
          (assoc result (:id request) request))
        {}
        requests)

; THREADING MACROS
; Threading macros make data transformations readable.
; Use -> when the value goes as the first argument.

; without threading
(assoc
 (update request :retries inc)
 :status :retrying)

; with threading
; this reads as: take request, update retries, then assoc status
(-> request
    (update :retries inc)
    (assoc :status :retrying))

; use ->> when the value goes as the last argument
; This is extremely common for collection pipelines.

(defn expensive? [request]
  (> (:tokens request) 5000))

; without threading
(reduce + 0 (map :tokens (filter expensive? requests)))

; with threading
(->> requests
     (filter expensive?)
     (map :tokens)
     (reduce + 0))

; SMALL REAL EXAMPLE

; let’s say we have LLM requests
(def requests
  [{:id 1 :model :deepseek-v3 :input-tokens 500 :output-tokens 600}
   {:id 2 :model :kimi-k2-thinking :input-tokens 3000 :output-tokens 4000}
   {:id 3 :model :qwen3-coder :input-tokens 200 :output-tokens 100}])

; define total tokens
(defn total-tokens [{:keys [input-tokens output-tokens]}]
  (+ input-tokens output-tokens))

; define expensive predicate
(defn expensive-request? [request]
  (> (total-tokens request) 5000))

; find expensive requests
(filterv expensive-request? requests)

; calculate total traffic
(->> requests
     (map total-tokens)
     (reduce + 0))

; add derived field
(defn enrich-request [request]
  (assoc request
         :total-tokens (total-tokens request)
         :expensive? (expensive-request? request)))

(mapv enrich-request requests)

; EXERCISE 1
; without destructuring
(defn total-tokens [request]
  (+ (:input-tokens request)
     (:output-tokens request)))

(total-tokens {:input-tokens 100
               :output-tokens 300})

; EXERCISE 2
; Write a function that adds :expensive? true/false to a request.
(defn mark-expensive [request]
  (assoc request
         :expensive? (expensive-request? request)))

(mark-expensive {:id 1
                 :input-tokens 3000
                 :output-tokens 4000})

; EXERCISE 3
(defn only-expensive [requests]
  (filter expensive-request? requests))

(only-expensive requests)

; EXERCISE 4
; Write a function that calculates total tokens for all requests.
(defn total-traffic [requests]
  (reduce (fn [total {:keys [input-tokens output-tokens]}]
            (+ total input-tokens output-tokens))
          0
          requests))

(total-traffic [{:input-tokens 100 :output-tokens 200}
                {:input-tokens 300 :output-tokens 400}])

; EXERCISE 5
(defn requests-by-id [requests]
  (reduce (fn [result request]
            (assoc result (:id request) request))
          {}
          requests))

(requests-by-id [{:id 1 :model :deepseek-v3}
                 {:id 2 :model :kimi-k2}])

; EXERCISE 6
(defn local-request? [request]
  (contains? local-models (:model request)))

(local-request? {:model :kimi-k2-thinking})
(local-request? {:model :gpt-5.5})
