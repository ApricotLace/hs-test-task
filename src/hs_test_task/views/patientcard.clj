(ns hs-test-task.views.patientcard
  (:use [hiccup.core :only (html)]))

(defn patientcard [data]
  (html
    [:div {:class "bg-light p-5 rounded"}
     [:h1 (:fullname data)]
     [:h2 "Gender:"]
     [:p (:gender data)]
     [:h2 "Date of birth:"]
     [:p (:birthdate data)]
     [:h2 "Adress:"]
     [:p (:adress data)]
     [:h2 "ID card number:"]
     [:p (:idcard data)]]))
