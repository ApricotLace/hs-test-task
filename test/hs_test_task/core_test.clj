(ns hs-test-task.core-test
  (:require [clojure.test :refer :all]
            [hs-test-task.views.patientcard :as pcard]))

(def fixtures {:patient {:fullname "Evgeny Andreevich Mukha"
                         :gender "Male"
                         :birthdate "30.04.2000"
                         :adress "Spb, krilenko"
                         :idcard "1337"}
               :rendered-pcard (slurp "test/hs_test_task/fixtures/patiendcard.html")})

(deftest pcard-render
  (testing "Should render correctly"
    (is (= (pcard/patientcard (:patient fixtures))
           (clojure.string/trim-newline (:rendered-pcard fixtures))))))
