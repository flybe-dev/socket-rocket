(ns socket-rocket.logstash
  (:import (java.io  PrintWriter )
           (java.net InetAddress Socket)
           (clojure.lang PersistentArrayMap PersistentVector LazySeq IPersistentMap)
           (java.util Formattable))
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :refer  [generate-string]]
            [clojure.string :as strs]))

(def conn (atom nil))

(defn connect [{:keys [port logstash]}]
  (let [addr (InetAddress/getByName logstash)
        sock (Socket. addr port)]
    {:printer (PrintWriter. (.getOutputStream sock))
     :socket sock}))

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

(defprotocol IFormattable
  (formatty [this]))
(extend-protocol IFormattable
  IPersistentMap
  (formatty [this] this)
  Object
  (formatty [this]
    {:value this})
  nil                                                       ;; Pass the nils back up, I don't want an exception here!
  (formatty [this]
    {:nil this}))

(defn json-formatter
  [{:keys [level throwable timestamp hostname args] :as params}]
  (generate-string {
                      :level      level
                      :throwable  (timbre/stacktrace throwable)
                      :msg        (apply formatty args)
                      :timestamp  (-> timestamp strs/upper-case)
                      :hostname   (-> hostname strs/upper-case)
                      :ns         (str *ns*)}))
(defn args-isa?
  [params]
  (-> :args params first type))

(defn partition-map
  [n coll]
  (let [c (partition n coll)]
    (-> (map (fn [[k v]] {k v}) c) vec)))

(defn appender-fn [{:keys [ap-config] :as params}]
  (when-let [socket-config (:logstash ap-config)]
    (let [{:keys [printer]} (ensure-conn socket-config)]
      (.println printer
                (json-formatter params))
      (.flush printer))))

(def logstash-appender
  "Logs to a listening socket.\n
  Needs :logstash config map in :shared-appender-config, e.g.:
  {:logstash \"128.200.20.117\"
   :port 4660}"
  {:min-level :trace
   :enabled? true
   :fn appender-fn})

(comment (timbre/set-config! [:appenders :logstash] logstash-appender)
         (timbre/set-config! [:shared-appender-config :logstash] {:port     4660
                                                                  :logstash "dougal.development.local"}))