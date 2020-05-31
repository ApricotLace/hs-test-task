(ns hs-test-task.views.blank-patients-list
  (:require [hiccup.core :refer [html]]))

(defn render-blank-patients-list []
  (html [:div {:class "bg-light m-5 p-5 rounded lead"} "There are no records. Add some!"]))

(render-blank-patients-list)
