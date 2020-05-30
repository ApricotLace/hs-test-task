(ns hs-test-task.core
  (:require [compojure.core :refer [defroutes GET]]
            [org.httpkit.server :as hk]
            [hs-test-task.views.layout :as layout]
            [hs-test-task.views.patientcard :as pcard]
            ))

(defroutes routes
  (GET "/" [] (layout/common "main-page"
                             (pcard/patientcard
             {:fullname "Evgeny Andreevich Mukha"
              :gender "Male"
              :birthdate "30.04.2000"
              :adress "Spb, krilenko"
              :idcard "1337"}))))

(defn -main []
  (hk/run-server routes {:port 7000}))

(def server (-main))
(quote (server))
