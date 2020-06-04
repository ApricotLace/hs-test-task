(ns hs-test-task.core
  (:require [ring.adapter.jetty :as jetty]
            [hs-test-task.models.migration :refer [migrate]]
            [environ.core :refer [env]]
            [hs-test-task.controllers.patient :refer [routes]]
            [ring.middleware.defaults :refer [wrap-defaults secure-site-defaults]])
  (:gen-class))

(def application (wrap-defaults routes (assoc secure-site-defaults
                                              :session {:flash true}
                                              :proxy true
                                              :security {:anti-forgery false})))
(defn -main []
  (migrate)
  (try
    (jetty/run-jetty application {:port (Integer. (env :port 7000)) :join? false})
    (catch Exception e (println e))))
