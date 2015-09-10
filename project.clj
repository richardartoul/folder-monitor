(defproject folder-monitor "0.1.0-SNAPSHOT"
  :description "Folder-Monitor: Monitors a folder for new files and renames them appropriately"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"] [clojure-watch "LATEST"]]
  :main ^:skip-aot folder-monitor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
