(ns hs-test-task.views.patient-form
  (:require [formative.core :as f]
            [hiccup.core :refer [html]]
            [clj-time.core :as time]))

(defn add-patient-form-spec [header] {:fields [{:name :h1 :type :heading :text header}
                                     {:name :full-name}
                                     {:name :br :type :heading}
                                     {:name :gender}
                                     {:name :br :type :heading}
                                     {:name :date-of-birth :type :date-select :year-start 1900 :year-end (time/year (time/now))}
                                     {:name :br :type :heading}
                                     {:name :address}
                                     {:name :br :type :heading}
                                     {:name :health-insurance-card-id}
                                     {:name :br :type :heading}
                                     {:name :br :type :heading}]
                            :validations [[:required [:full-name :gender :date-of-birth :address :health-insurance-card-id]]
                                          [:before (time/now) [:date-of-birth] "The entered date is greater than the current"]]})

(defn render-patient-form
  ([params header]
   (html
     [:div {:class "bg-light m-5 p-5 rounded lead"}
      (f/render-form (assoc (add-patient-form-spec header)
                            :values params))]))
  ([_ __ & [problems]]
   {:problems problems}))
