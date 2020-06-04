(ns hs-test-task.core-test
  (:require [clojure.test :refer :all]
            [hs-test-task.views.patient-card :refer [build-patientcard]]
            [hs-test-task.models.migration :refer :all]
            [hs-test-task.models.patient :refer [get-patient-by-id db-spec]]
            [clojure.string]
            [hs-test-task.controllers.patient :refer [routes]]
            [ring.mock.request :as rmock]
            [hs-test-task.models.patient :refer [date-map->sql-date
                                                 str-date->date-map
                                                 form-data->patient-model
                                                 patient-model->form-data]]
            [hs-test-task.controllers.patient :refer [keyword->field-label build-error-message]]
            [hiccup.core :refer [html]]))

(def fixtures {:patient {:full_name "Evgeny Andreevich Mukha"
                         :gender "Male"
                         :date_of_birth "2000-04-30"
                         :address "Spb, krilenko"
                         :health_insurance_card_id "1337"}
               :rendered-pcard (clojure.string/trim-newline (slurp "test/hs_test_task/fixtures/patiendcard.html"))
               :built-migration-query (clojure.string/trim-newline (slurp "test/hs_test_task/fixtures/migration-query.sql"))
               :field-label "Foo dir bar dor"
               :date-map {:year "1900" :month "1" :day "1"}
               :incorrect-date-map {:year "2030" :month "1" :day "1"}
               :form-data {:date-of-birth {:year "2000" :month "4" :day "30"}
                           :address "Test address"
                           :full-name "Evgeny Andreevich Mukha"
                           :gender "Male"
                           :health-insurance-card-id "123123"}
               :error-message "A field error-message\n"
               :error-message-pluralized "A, B fields error-message\n"
               :full-name-field-error "Full name field must not be blank\n"
               :date-of-birth-error "Date of birth field The entered date is greater than the current\n"
               :record-add-succ "Patient record added successfully"
               :uniqueness-error "Health insurance card id must be unique"
               :record-edit-succ "Patient record updated successfully"
               :record-delete-succ "Patient record deleted successfully"})

(deftest pcard-render
  (testing "Should render correctly"
    (is (= (html (build-patientcard (:patient fixtures)))
           (:rendered-pcard fixtures)))))

(deftest migration-query-build
  (testing "Should build correctly"
    (is (= (first migrated?-query) (:built-migration-query fixtures)))))

(deftest convert-keyword
  (testing "Shoud convert correctly"
    (is (= (keyword->field-label :foo-dir-bar-dor) (:field-label fixtures)))))

(deftest build-error-messages
  (testing "Single"
    (is (= (build-error-message (list {:keys [:A] :msg "error-message"}))
           (:error-message fixtures))))
  (testing "Pluralized"
    (is (= (build-error-message (list {:keys [:A :B] :msg "error-message"}))
           (:error-message-pluralized fixtures)))))

(deftest coerce-loops
  (testing "date-map -> sql-date -> str-date -> date-map"
    (is (= (:date-map fixtures)
           (-> (date-map->sql-date (:date-map fixtures))
               str
               (str-date->date-map)))))
  (testing "form-data -> patient-model -> form-data"
    (is (= (:form-data fixtures)
           (-> (form-data->patient-model (:form-data fixtures))
               (patient-model->form-data))))))

(deftest handler-tests
  (testing "Migrations"
    (println "DB_URL" db-spec)
    (when (not (migrated?)) (migrate))
    (is (= true (migrated?))))

  (testing "Main page"
    (let [response (routes (rmock/request :get "/"))
          status (:status response)]
      (is (= status 200))))

  (testing "Page not found"
    (let [response (routes (rmock/request :get "/abracadabra"))
          status (:status response)]
      (is (= status 404))))

  (testing "Add patient without full-name field"
    (let [response (routes (-> (rmock/request :post "/add-patient")
                               (assoc :params (assoc (:form-data fixtures) :full-name ""))))
          message (:message (:flash response))]
      (is (= message (:full-name-field-error fixtures)))))

  (testing "Add patient with incorrect date-of-birth field"
    (let [response (routes (-> (rmock/request :post "/add-patient")
                               (assoc :params (assoc (:form-data fixtures)
                                                     :date-of-birth
                                                     (:incorrect-date-map fixtures)))))
          message (:message (:flash response))]
      (is (= message (:date-of-birth-error fixtures)))))

  (testing "Add patient"
    (let [response (routes (-> (rmock/request :post "/add-patient")
                               (assoc :params (:form-data fixtures))))
          message (:message (:flash response))]
      (is (= message (:record-add-succ fixtures)))))

  (testing "Get new user"
    (let [patient (patient-model->form-data (get-patient-by-id 1))]
      (is (= patient (:form-data fixtures)))))

  (testing "Add patient with not unqie health-insurance-card-id"
    (let [response (routes (-> (rmock/request :post "/add-patient")
                               (assoc :params (:form-data fixtures))))
          message (:message (:flash response))]
      (is (= message (:uniqueness-error fixtures)))))

  (testing "Edit nonexistent user"
    (let [response (routes (rmock/request :get "/edit-patient/999"))
          status (:status response)]
      (is (= status 404))))

  (testing "Edit user"
    (let [response (routes (-> (rmock/request :post "/edit-patient/1")
                       (assoc :params (assoc (:form-data fixtures)
                                             :full-name "Oleg Olegovich Petrov"))))
          message (:message (:flash response))]
      (is (= message (:record-edit-succ fixtures)))))

  (testing "Get updated user"
    (let [patient (patient-model->form-data (get-patient-by-id 1))]
      (is (= patient (assoc (:form-data fixtures)
                            :full-name "Oleg Olegovich Petrov")))))

  (testing "Delete nonexistent user"
    (let [response (routes (rmock/request :get "/delete-patient/999"))
          status (:status response)]
      (is (= status 404))))

  (testing "Delete user"
    (let [response (routes (rmock/request :post "/delete-patient/1"))
          message (:message (:flash response))]
      (is (= message (:record-delete-succ fixtures))))))
