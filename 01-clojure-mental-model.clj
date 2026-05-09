; calling functions

(+ 1 2)

(println "hello")

; basic data structures

{:name "Andrey",
 :role "Staff Engineer",
 :active true}

; map keywords can be used as functions
(:name {:name "Andrey",
        :role "Staff Engineer",
        :active true})

; vectors are used for ordered collections
[1 2 3 4]

; lists
'(1 2 3 4)
; this is also a list where + is the first element in it
(+ 1 2)

; set
#{:admin :editor :viewer}

; in real Clojure code, do not overuse global def
(def user-name "Andrey")

; most local values are created with let
; think of let as local bindings
(let [name "Andrey" role "Staff Engineer"]
  (str name " is a " role))

; defining functions
(defn greet [name]
  (str "Hello, " name))

(greet "Andrey")

; multiple arguments
(defn add [a b]
  (+ a b))

(add 10 26)

; functions are values
(def add-one inc)

(inc 10)
(add-one 10)

; anonymous function
(fn [x]
  (+ x 1))

((fn [x]
   (+ x 1))
 36)

; shortcut syntax
#(+ % 1)

(#(+ % 1) 36)

; conditions
(if true
  "yes"
  "no")

; only false and nil are falsy
; these are truthy
; 0
; ""
; []
; {}
(if []
  "truthy"
  "false")

; cond
(defn classify-age [age]
  (cond
    (< age 13) :child
    (< age 18) :teenager
    :else :adult))

(classify-age 12)

; In Clojure, you often model domain entities as plain maps.
{:id 1
 :name "Andrey"
 :role :engineer}

; functions operate on this data
(defn promote [user]
  (assoc user :role :staff-engineer))

(promote {:id 1 :name "Andrey" :role :engineer})

; Updating data
(def user
  {:id 1
   :name "Andrey"
   :stats {:requests 10
           :errors 1}})

; update top-level field
(assoc user :name "Andrey Krisanov")

; update nested field
(assoc-in user [:stats :requests] 11)

; update using a function
(update-in user [:stats :requests] inc)

; Exercise #1
(defn active-user? [user]
  (:active user))

(def user {:id 1
           :name "Andrey"
           :active true})

(active-user? user)

; Exercise #2
(defn add-login [user]
  (update user :login-count inc))

(add-login {:id 1
            :name "Andrey"
            :login-count 0})

; Exercise #3
(defn classify-request [request]
  (let [tokens (:tokens request)]
    (cond
      (> tokens 10000) :large
      (> tokens 1000) :medium
      :else :small)))

(classify-request {:path "/chat"
                   :method :post
                   :tokens 1200})

; Exercise #4
(defn expensive-request? [{:keys [input-tokens output-tokens]}]
  (> (+ input-tokens output-tokens)))


(expensive-request? {:model :deepseek-v3
                     :input-tokens 2000
                     :output-tokens 8000})
