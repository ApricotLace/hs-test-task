(defproject hs-test-task "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [environ "1.1.0"]
                 [compojure "1.6.0"]
                 [ring/ring-jetty-adapter "1.8.1"]
                 [hiccup "1.0.5"]
                 [honeysql "1.0.444"]
                 [ring/ring-defaults "0.3.2" :exclusions [ring/ring-core]]
                 [formative "0.8.9"]
                 [clj-time "0.15.2"]]
  :main ^:skip-aot hs-test-task.core
  :min-lein-version "2.0.0"
  :uberjar-name "hs-test-task-standalone.jar"
  :profiles {:uberjar {:aot :all}})
