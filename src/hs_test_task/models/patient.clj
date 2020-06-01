(ns hs-test-task.models.patient
  (:require [clojure.java.jdbc :as db]
            [honeysql.core :as sql]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [clj-time.coerce :as coerce]
            [environ.core :refer [env]]))

(def db-spec (env :database-url
                  "postgresql://localhost:5432/patients"))

(defn patient->vals [patient]
  [(:date_of_birth patient)
   (:gender patient)
   (:address patient)
   (:full_name patient)
   (:health_insurance_card_id patient)])

(defn date-map->sql-date [{:keys [year month day]}]
  (-> (str year "-" month "-" day " " "00:00:00")
      format/parse
      coerce/to-sql-date))

(defn str-date->date-map [str-date]
  (let [date-obj (format/parse str-date)]
    {:year (str (time/year date-obj))
     :month (str (time/month date-obj))
     :day (str (time/day date-obj))}))

(defn form-data->patient-model [form-data]
  (let [form-data (update (dissoc form-data :__anti-forgery-token :submit)
                          :date-of-birth date-map->sql-date)]
    {:date_of_birth (:date-of-birth form-data)
     :gender (:gender form-data)
     :address (:address form-data)
     :full_name (:full-name form-data)
     :health_insurance_card_id (:health-insurance-card-id form-data)}))

(defn patient-model->form-data [patient-model]
  {:date-of-birth (str-date->date-map (str (:date_of_birth patient-model)))
   :gender (:gender patient-model)
   :address (:address patient-model)
   :full-name (:full_name patient-model)
   :health-insurance-card-id (:health_insurance_card_id patient-model)})

(defn get-all-patients []
  (db/query db-spec (sql/format {:select [:*]
                                 :from [:patients]})))

(defn get-patients-ids []
  (db/query db-spec (sql/format {:select [:health_insurance_card_id]
                                 :from [:patients]})))

(defn add-patient [params]
  (try
    (db/insert! db-spec
                :patients
                [:date_of_birth :gender :address :full_name :health_insurance_card_id]
                (patient->vals params))
    (catch Exception e (println (.getNextException e)))))

(defn get-patient-by-id [id]
  (-> (db/query db-spec (sql/format {:select [:*]
                                     :from [:patients]
                                     :where [:= :id id]}))
      first))

(defn update-patient-by-id [id new-data]
  (try
    (db/update! db-spec :patients new-data ["id = ?" id])
    (catch Exception e (println (.getNextException e)))))

(defn delete-patient-by-id [id]
  (try
    (db/delete! db-spec :patients ["id = ?" id])
    (catch Exception e (println (.getNextException e)))))
