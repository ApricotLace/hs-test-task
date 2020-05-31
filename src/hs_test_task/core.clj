(ns hs-test-task.core
  (:require [compojure.core :refer [defroutes GET]]
            [org.httpkit.server :as hk]
            [hs-test-task.views.layout :refer [render-layout]]
            [hs-test-task.models.migration :refer [migrate]]
            [environ.core :refer [env]]
            [hs-test-task.controllers.patient :refer [routes]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn -main []
  (migrate)
  (hk/run-server (wrap-defaults routes (assoc site-defaults
                                              :session {:flash true}
                                              :security {:anti-forgery false}))
                 {:port (env :port 7000)}))
