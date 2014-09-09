(ns socket-rocket.logstash
  (:import (java.io  PrintWriter )
           (java.net InetAddress Socket)
           (clojure.lang PersistentArrayMap PersistentVector LazySeq IPersistentMap)
           (java.util Formattable))
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :refer [generate-string]]
            [clojure.string :as strs]))

(def conn (atom nil))

(defn connect [{:keys [port logstash]}]
  (try
    (let [addr (InetAddress/getByName logstash)
          sock (Socket. addr port)]
      {:printer (PrintWriter. (.getOutputStream sock))
       :socket  sock})
    (catch Throwable _)))

(defn connected?
  [sock]
  (when sock
    (.isConnected sock)))

(defn closed?
  [sock]
  (when sock
    (.isClosed sock)))

(defn in-trouble?
  [pw]
  (when pw
    (.checkError pw)))

(def not-in-trouble?
  (complement in-trouble?))

(def not-connected?
  (complement connected?))

(def not-closed?
  (complement closed?))

(defn awesome?
  [{:keys [printer socket]}]
  (and (not-closed? socket)
       (connected? socket)
       (not-in-trouble? printer) ))

(defn ensure-conn
  [socket-config]
  (swap! conn #(or (and (-> % awesome?) %)
                   (connect socket-config))))


(defn json-formatter
  [{:keys [level throwable timestamp args hostname ns] :as params}]
  (generate-string {
                     :level     level
                     :throwable (timbre/stacktrace throwable)
                     :msg       (apply print-str args)
                     :timestamp (-> timestamp strs/upper-case)
                     :hostname  (-> hostname strs/upper-case)
                     :ns        ns}))


(defn appender-fn [formatter-fn {:keys [ap-config] :as params}]
  (when-let [socket-config (:logstash ap-config)]
    (when-let [{:keys [printer]} (ensure-conn socket-config)]
      (.println printer (formatter-fn params))
      (.flush printer))))

(defn make-logstash-appender
  "Logs to a listening socket.
  Needs :logstash config map in :shared-appender-config, e.g.:
  {:logstash \"128.200.20.117\"
   :port 4660
   :ns *ns*}"
  [formatter-fn]
  {:min-level :trace
   :enabled? true
   :fn (partial appender-fn formatter-fn)})

(def logstash-appender
  (make-logstash-appender json-formatter))

(comment (timbre/set-config! [:appenders :logstash] logstash-appender)
         (timbre/set-config! [:shared-appender-config :logstash] {:port     4660
                                                                  :logstash "dougal.develofdspment.local"}))
