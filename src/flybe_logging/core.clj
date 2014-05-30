(ns flybe-logging.core
  (:require [taoensso.timbre :as timbre]
            [flybe-logging.mongo :as mongo-a]
            [somnium.congomongo :as congo]))



(timbre/refer-timbre)

(timbre/set-config! [:appenders :mongo]  {:fn mongo-a/appender-fn})
(timbre/set-config! [:appenders :mongo :enabled?]  true)

(timbre/set-config! [:appenders :standard-out] {:enabled? false})


(mongo-a/connect
  {:db "db-logs"
   :server {:host "128.200.19.35" :port 27017}
   :write-concern :safe})

(timbre/set-config! [:appenders :spit :enabled?] false)

(timbre/set-config! [:shared-appender-config :spit-filename] "/var/log/flybe/timbre")
(timbre/set-config! [:shared-appender-config :mongo] {:db         "db-logs"
                                                      :collection "example"
                                                      :server     {:host "128.200.19.35" :port 27017}
                                                      :enabled? true
                                                      :async? true
                                                      :min-level :info
                                                      :rate-limit [1 1000]
                                                      :fn mongo-a/appender-fn})




(try
  (throw (Exception. "Ooops"))
  (catch Throwable t
    (timbre/info {:error {:name "Hello" :id 0} :throws :Throwable :crap {:foo :fb}})))

;
;(def db
;  (congo/make-connection  "db-logs" :host "128.200.19.35" :port 27017))
;
;
;(congo/with-mongo db
;                   (congo/insert! :log {:test "test"}))