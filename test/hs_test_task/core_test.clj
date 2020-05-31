(ns hs-test-task.core-test
  (:require [clojure.test :refer :all]
            [hs-test-task.views.patient-card :refer [build-patientcard]]
            [hs-test-task.models.migration :refer [migrated?-query]]
            [clojure.string]
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
               :form-data {:date-of-birth {:year "2000" :month "4" :day "30"}
                           :address "Test address"
                           :full-name "Evgeny Andreevich Mukha"
                           :gender "Male"
                           :health-insurance-card-id "123123"}
               :error-message "A field error-message"
               :error-message-pluralized "A, B fields error-message"})

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

(deftest build-error-message-single
  (testing "Should build correctly"
    (is (= (build-error-message (list {:keys [:A] :msg "error-message"}))
           (:error-message fixtures)))))

(deftest build-error-message-pluralized
  (testing "Should build correctly"
    (is (= (build-error-message (list {:keys [:A :B] :msg "error-message"}))
           (:error-message-pluralized fixtures)))))

(deftest coerce-loop
  (testing "date-map -> sql-date -> str-date -> date-map"
    (is (= (:date-map fixtures)
           (-> (date-map->sql-date (:date-map fixtures))
               str
               (str-date->date-map))))))

(deftest coerce-loop2
  (testing "form-data -> patient-model -> form-data"
    (is (= (:form-data fixtures)
           (-> (form-data->patient-model (:form-data fixtures))
               (patient-model->form-data))))))
