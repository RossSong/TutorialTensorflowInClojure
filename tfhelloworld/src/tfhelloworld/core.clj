(ns tfhelloworld.core
	(:import [org.tensorflow Tensor TensorFlow Graph Session])
)

(defn hello []
   (println "Hello world!"))

(defn tfhello []
    (try (let [graph (let [g (Graph.)
			   value (str "Hello, TensorFlow!")
			   t (Tensor/create (.getBytes value))]
			(-> (.opBuilder g "Const" "MyConst")
			    (.setAttr "dtype" (.dataType t))
                            (.setAttr "value" t)
			    (.build))
			g)]
	(try (let [output (-> (Session. graph)
				(.runner)
				(.fetch "MyConst")
				(.run)
				(.get 0)
				)]
		(println (String. (.bytesValue output) "UTF-8")))))))


(def graph (new Graph))
(def tensorA
    (let [tensor (Tensor/create (int 10))]
         (-> graph
             (.opBuilder "Const" "A")
             (.setAttr "dtype" (.dataType tensor))
             (.setAttr "value" tensor)
             .build
             (.output 0))))

(def tensorB
    (let [tensor (Tensor/create (int 32))]
         (-> graph
             (.opBuilder "Const" "B")
             (.setAttr "dtype" (.dataType tensor))
             (.setAttr "value" tensor)
             .build
             (.output 0))))

(def add
    (-> (.opBuilder graph "Add" "a_plus_b")
        (.addInput tensorA)
        (.addInput tensorB)
        .build
        (.output 0)
        ))

(def session (new Session graph))

(def result
    (-> session
	.runner
        (.fetch "a_plus_b")
	.run
	(.get 0)
 	)
)

(defn tfhello2 []
	(try (let [output result]
		(println (.intValue output)))))

(defn -main []
  (tfhello)
  (tfhello2))
