(ns tfhelloworld.core2
        (:import [org.tensorflow Tensor TensorFlow Graph Session DataType])
)
        
(def value (str "hello world"))
(def t (Tensor/create (.getBytes value)))
(def g (Graph.))
(def op (-> (.opBuilder g "Const" "hello")(.setAttr "dtype" (.dataType t))(.setAttr "value" t)(.build)))
(def sess (Session. g))
(def output (-> (.runner sess)(.fetch "hello")(.run)(.get 0)))

(defn -main [& args]
	(prn (String. (.bytesValue output) "UTF-8"))
	(.close sess)
)
