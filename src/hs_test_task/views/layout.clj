(ns hs-test-task.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn hide? [flash]
  (if (nil? flash) "display: none;" ""))

(defn render-layout [title flash content]
  (html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge, chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
     [:title title]
     (include-css "https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css")
     (include-js "https://code.jquery.com/jquery-3.5.1.slim.min.js")
     (include-js "https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js")
     (include-js "https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js")]
    [:body
     [:nav {:class "navbar navbar-expand-md navbar-light fixed-top bg-light"}
      [:div {:class "container-fluid"}
       [:a {:href "/" :class "navbar-brand"} "HS-TEST-TASK-K8S-TEST!"]
       [:div {:class "navbar-nav mr-auto" :style (if (= title "main-page") "" "display: none;")}
        [:a {:href "/add-patient" :class "nav-link"} "Add patient"]]]]
     [:main {:class "container p-5"}
      [:div {:class (:message-style flash)
             :style (str (hide? flash) "white-space: pre;")} (:message flash)
       [:button {:type "button" :class "close" :data-dismiss "alert"}
        [:span "&times;"]]]
      content]]))
