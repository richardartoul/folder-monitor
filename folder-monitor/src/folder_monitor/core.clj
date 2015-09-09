(ns folder-monitor.core
  (:gen-class)
  (:require [clojure-watch.core :refer [start-watch]]))

(def sookasa-regex #".*\.ext\(.*conflicted copy \d{4}-\d{2}-\d{2}\)\.sookasa")

(defn rename-file
  "Renames incorrectly named files to their proper form"
  [event filename]
  ; print the event and filename to the console - change this to a log file
  (println event filename)
  ; see if the modified files name matches the incorrect pattern
  (println 
    (re-matches sookasa-regex filename)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  ; Starst watching the folder and listening for changes
  (start-watch [{:path "/home/richie/Desktop/sookasa"
                 :event-types [:create :modify :delete]
                 :bootstrap (fn [path] (println "Starting to watch " path))
                 :callback rename-file
                 :options {:recursive true}}]))
