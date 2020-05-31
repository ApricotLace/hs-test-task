(ns hs-test-task.models.migration
  (:require [clojure.java.jdbc :as db]
            [hs-test-task.models.patient :refer [db-spec]]
            [honeysql.core :as sql]))

(def migrated?-query (sql/format {:select [:%count.*]
                                  :from [:information_schema.tables]
                                  :where [:= :table_name "patients"]}))

(defn migrated? []
  (-> (db/query db-spec migrated?-query)
      first
      :count
      pos?))

(defn migrate []
  (when (not (migrated?))
    (db/db-do-commands db-spec
                       (db/create-table-ddl
                         :patients
                         [[:id :serial "PRIMARY KEY"]
                          [:full_name :varchar "NOT NULL"]
                          [:gender :varchar "NOT NULL"]
                          [:date_of_birth :date "NOT NULL"]
                          [:address :varchar "NOT NULL"]
                          [:health_insurance_card_id :varchar "NOT NULL UNIQUE"]
                          [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]]))))
