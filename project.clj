(defproject hs-test-task "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [environ "1.1.0"]
                 [compojure "1.4.0"]
                 [http-kit "2.3.0"]
                 [hiccup "1.0.5"]
                 [honeysql "1.0.444"]
                 [ring/ring-defaults "0.3.2"]
                 [formative "0.8.9"]
                 [clj-time "0.15.2"]]
  :main hs-test-task.core/-main
  :repl-options {:init-ns hs-test-task.core})
