(ns hs-test-task.controllers.patient
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [hs-test-task.models.patient :as model]
            [hs-test-task.views.layout :refer [render-layout]]
            [formative.parse :as fp]
            [clojure.string]
            [ring.util.response :as response]
            [hs-test-task.views.blank-patients-list :refer [render-blank-patients-list]]
            [hs-test-task.views.404 :refer [build-404-page]]
            [hs-test-task.views.delete-confirmation :refer [build-delete-confirmation]]
            [hs-test-task.views.patient-card :refer [build-patientcard]]
            [hs-test-task.views.patient-form :refer [render-patient-form add-patient-form-spec]]))

(def messages-styles {:danger "alert alert-danger alert-dismissible mt-5 fade show"
                      :success "alert alert-success alert-dismissible mt-5 fade show"})

(defn build-patient-list [patients]
  (into [:div] (if (pos? (count patients))
                 (map #(build-patientcard %) patients)
                 (render-blank-patients-list))))

(defn keyword->field-label [keyword]
  (-> keyword
      name
      (clojure.string/replace #"[_-]" " ")
      clojure.string/capitalize))

(defn build-error-message [problems]
  (reduce
    (fn [acc {:keys [keys msg]}]
      (let [labels (map #(keyword->field-label %) keys)
            pluralize? (> (count labels) 1)]
        (str acc
             (str (clojure.string/join ", " labels)
                  (if pluralize? " fields " " field ")
                  msg)
             "\n")))
    ""
    problems))

(defn validate-form [form-data redirect-url skip-uniqueness-check?]
  (let [discarded-form (fp/with-fallback
                         #(render-patient-form form-data nil %)
                         (fp/parse-params (add-patient-form-spec nil) form-data)
                         nil)]
    (if (not (nil? discarded-form))
      (-> (response/redirect redirect-url)
          (assoc :flash {:message (build-error-message (:problems discarded-form))
                         :message-style (:danger messages-styles)
                         :params form-data}))

      (let [patient (model/form-data->patient-model form-data)
            new-patient-id (:health_insurance_card_id patient)
            query-result (model/get-patients-ids)
            patients-ids (flatten (map #(vals %) query-result))]
        (if (and (not skip-uniqueness-check?) (some #(= new-patient-id %) patients-ids))
          (-> (response/redirect redirect-url)
              (assoc :flash {:message "Health insurance card id must be unique"
                             :message-style (:danger messages-styles)
                             :params form-data}))
          nil)))))

(defn submit-form
  ([form-data redirect-url]
   (let [problems (validate-form form-data redirect-url false)]
     (if (not (nil? problems))
       problems
       (let [patient (model/form-data->patient-model form-data)]
         (model/add-patient patient)
         (-> (response/redirect "/")
             (assoc :flash {:message "Patient record added successfully"
                            :message-style (:success messages-styles)}))))))

  ([form-data redirect-url user-id]
   (let [health-insurance-card-id (:health_insurance_card_id
                                    (model/get-patient-by-id (Integer/parseInt user-id)))
         skip-uniqueness-check? (= health-insurance-card-id
                                   (:health-insurance-card-id form-data))
         problems (validate-form form-data redirect-url skip-uniqueness-check?)]
     (if (not (nil? problems))
       problems
       (let [patient (model/form-data->patient-model form-data)]
         (model/update-patient-by-id (Integer/parseInt user-id) patient)
         (-> (response/redirect "/")
             (assoc :flash {:message "Patient record updated successfully"
                            :message-style (:success messages-styles)})))))))

(defn index [flash]
  (render-layout "main-page" flash (build-patient-list (reverse (model/get-all-patients)))))

(defn add-patient [flash flash-params]
  (render-layout "add-patient" flash (render-patient-form flash-params "Add new patient")))

(defn not-found []
  (render-layout "not-found" nil (build-404-page)))

(defn edit-patient [params flash flash-params]
  (if (not (nil? flash))
    (render-layout "edit-patient" flash (render-patient-form flash-params "Edit patient"))
    (let [user-id (:user-id params)
          patient (if (number? (try (Integer/parseInt user-id) (catch Exception _ nil)))
                   (model/get-patient-by-id (Integer/parseInt (:user-id params)))
                   nil)]
      (if (nil? patient)
        (route/not-found (not-found))
        (render-layout "edit-patient" nil
                       (render-patient-form
                         (model/patient-model->form-data patient)
                         "Edit patient"
                         ))))))

(defn delete-patient [id]
  (let [patient (if (number? (try (Integer/parseInt id) (catch Exception _ nil)))
                 (model/get-patient-by-id (Integer/parseInt id))
                 nil)]
    (if (nil? patient)
      (route/not-found (not-found))
      (render-layout "delete-patient" nil (build-delete-confirmation id)))))

(defn submit-deletion [id]
  (model/delete-patient-by-id (Integer/parseInt id))
  (-> (response/redirect "/")
      (assoc :flash {:message "Patient record deleted successfully"
                     :message-style (:success messages-styles) })))

(defroutes routes
  (GET "/" {flash :flash} (index flash))
  (GET "/add-patient" {flash :flash} (add-patient flash (:params flash)))
  (POST "/add-patient" {params :params} (submit-form params "/add-patient"))
  (POST "/edit-patient/:user-id" {params :params uri :uri} (submit-form params uri (:user-id params)))
  (GET "/edit-patient/:user-id" {params :params flash :flash} (edit-patient params flash (:params flash)))
  (GET "/delete-patient/:user-id" {params :params} (delete-patient (:user-id params)))
  (POST "/delete-patient/:user-id" {params :params} (submit-deletion (:user-id params)))
  (route/not-found (not-found)))
