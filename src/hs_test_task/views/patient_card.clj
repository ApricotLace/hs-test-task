(ns hs-test-task.views.patient-card)

(defn build-patientcard [data]
    [:div {:class "bg-light m-5 p-5 rounded lead"}
     [:h3 "Fullname"]
     [:p (:full_name data)]
     [:h3 "Gender:"]
     [:p (:gender data)]
     [:h3 "Date of birth:"]
     [:p (:date_of_birth data)]
     [:h3 "Address:"]
     [:p (:address data)]
     [:h3 "Health insurance card id:"]
     [:p (:health_insurance_card_id data)]
     [:a {:href (str "/edit-patient/" (:id data)) :class "btn btn-primary mr-2"} "Edit"]
     [:a {:href (str "/delete-patient/" (:id data)) :class "btn btn-danger"} "Delete"]])
