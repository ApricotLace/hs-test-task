(ns hs-test-task.views.layout
  (:use [hiccup.page :only (html5 include-css)]))

(defn common [title & content]
  (html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge, chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
     [:title title]
     (include-css "https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css")]
    [:body
     [:nav {:class "navbar navbar-expand-md navbar-light fixed-top bg-light"}
      [:div {:class "container-fluid"}
       [:a {:href "/" :class "navbar-brand"} "HS-TEST-TASK"]]]
     [:main {:class "container"} content]
     ]))

(common "title" "content")
