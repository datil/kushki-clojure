(def project 'kushki-clojure)
(def version "0.1.0")

(set-env! :resource-paths #{"resources"}
          :source-paths   #{"src" "test"}
          :dependencies   '[[adzerk/boot-test "RELEASE" :scope "test"]
                            [environ "1.1.0"]
                            [boot-environ "1.1.0"]
                            [clj-http "3.7.0"]
                            [cheshire "5.8.0"]
                            [boot-codox "0.10.3" :scope "test"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]])

(require '[adzerk.bootlaces :refer :all])
(def +version+ "0.1.0")
(bootlaces! +version+)

(task-options!
 pom {:project     project
      :version     version
      :description "Cliente en Clojure para el API de Kushki"
      :url         "http://github.com/datil/kushki-clojure"
      :scm         {:url "https://github.com/datil/kushki-clojure"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]])

(require '[environ.boot :refer [environ]])

(def config (let [f (clojure.java.io/file "config.edn")]
              (if (.exists f) (-> f slurp read-string) {})))

(deftask dev
  []
  (environ :env config))

(require '[codox.boot :refer [codox]])

(deftask docs []
  (comp
   (codox
    :name "kushki-clojure"
    :description "Librería para Kushki en Clojure"
    :source-paths ["src"]
    :version "1.0.0")
   (target)))

