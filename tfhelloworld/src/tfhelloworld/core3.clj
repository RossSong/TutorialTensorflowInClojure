(ns tfhelloworld.core3
        (:import [org.tensorflow Tensor TensorFlow Graph Session Shape DataType]))


(def g (Graph.))
;;sess = tf.Session()
(def sess (Session. g))

;;X = tf.Placeholder(tf.flot32, [None, 3])
(def X (-> (.opBuilder g "Placeholder" "X")
           (.setAttr "dtype" (DataType/FLOAT))
           (.setAttr "shape" (Shape/make -1 (long-array 1 3)))
           (.build)
           (.output 0)))

;;x_data = [[1, 2, 3], [4, 5, 6]]
;;(def x_data (to-array-2d [[1 2 3][4 5 6]]))
;;(def x_data (Tensor/create (float-array [1 2 3 4 5 6])))
;;(def x_data (Tensor/create ((to-array-2d [[1 2 3][4 5 6]]))))
(def x_data (Tensor/create (into-array [(float-array [1 2 3]) (float-array [4 5 6])])))

;;W = tf.Variable(tf.random_normal([3,2]))
(def W (-> (.opBuilder g "Variable" "W")
           (.setAttr "shape" (Shape/make 3 (long-array 1 2)))
           (.setAttr "dtype" (DataType/FLOAT))
           (.build)
           (.output 0)))

(def tensor32 (Tensor/create (int-array [3 2])))

(def op32 (-> (.opBuilder g "Const" "32")
              (.setAttr "dtype" (.dataType tensor32))
              (.setAttr "value" tensor32)
              (.build)
              (.output 0)))

(def RN32 (-> (.opBuilder g "RandomStandardNormal" "RN32")
              (.addInput op32)
              (.setAttr "dtype" (DataType/FLOAT))
              (.build)
              (.output 0)))

(def opA32 (-> (.opBuilder g "Assign" "AssignW")
               (.addInput W)
               (.addInput RN32)
               (.build)
               (.output 0)))

(-> (.runner sess)(.fetch "AssignW")(.run)(.get 0))

;;b = tf.Variable(tf.random_normal([2, 1]))
(def b (-> (.opBuilder g "Variable" "b")
           (.setAttr "shape" (Shape/make 2 (long-array 1 1)))
           (.setAttr "dtype" (DataType/FLOAT))
           (.build)
           (.output 0)))

(def tensor21 (Tensor/create (int-array [2 1])))

(def op21 (-> (.opBuilder g "Const" "21")
              (.setAttr "dtype" (.dataType tensor21))
              (.setAttr "value" tensor21)
              (.build)
              (.output 0)))

(def RN21 (-> (.opBuilder g "RandomStandardNormal" "RN21")
              (.addInput op21)
              (.setAttr "dtype" (DataType/FLOAT))
              (.build)(.output 0)))

(def op21 (-> (.opBuilder g "Assign" "Assignb")
              (.addInput b)
              (.addInput RN21)
              (.build)
              (.output 0)))

(-> (.runner sess)(.fetch "Assignb")(.run)(.get 0))

;;expr = tf.matmul(X, W) + b
(def opMul (-> (.opBuilder g "MatMul" "MatMul")
               (.addInput X)
               (.addInput W)
               (.build)
               (.output 0)))

(def opExpr (-> (.opBuilder g "Add" "Add")
                (.addInput opMul)
                (.addInput b)
                (.build)
                (.output 0)))

;;sess.run(tf.global_variables_initializer())
(def ^:dynamic global-variables (atom []))

(defn global-variables-initializer []
  @global-variables)

;;sess.run(expr, feed_dict={X: x_data})
(def out (-> (.runner sess)(.feed "X" x_data)
                           (.fetch "Add")
                           (.run)
                           (.get 0)))

(defn -main [& args]
	(def ret (.copyTo out (make-array Float/TYPE 2 2)))
	(prn (aget (aget ret 0) 0)
		(aget (aget ret 0) 1)
		(aget (aget ret 1) 0)
		(aget (aget ret 1) 1))
	(.close sess)
)

