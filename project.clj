(defproject com.flybe/socket-rocket "0.1.0-SNAPSHOT"
  :description "A socket appender for Logstash & Timbre"
  :url "https://github.com/flybe-dev/socket-rocket"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.taoensso/timbre "3.1.6"]
                 [cheshire "5.3.1"]]
  :plugins [[codox "0.7.3"]]
  :codox {:src-dir-uri "https://github.com/flybe-dev/socket-rocket/blob/master"
          :src-linenum-anchor-prefix ""
          :sources ["src/flybe_logging"]})
