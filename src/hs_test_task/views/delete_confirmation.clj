(ns hs-test-task.views.delete-confirmation)

(defn build-delete-confirmation [id]
  [:div {:class "bg-light m-5 p-5 rounded lead"}
   [:h1 {:class "mb-5"} "Delete this record?"]
   [:form {:action (str "/delete-patient/" id) :method "post"}
    [:button {:type "submit" :class "btn btn-danger mr-2"} "Delete"]
    [:a {:href "/" :class "btn btn-primary"} "Cancel"]]])
