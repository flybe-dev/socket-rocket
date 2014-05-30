(defproject com.flybe/socket-rocket "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories ^:replace [["artifactory" "http://repo.flybe.local/artifactory/repo"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.taoensso/timbre "3.1.6"]
                 [server-socket "1.0.0"]
                 [congomongo "0.4.3"]
                 [com.flybe/flybe-json "0.1.0-SNAPSHOT"]])
