(ns folder-monitor.core
  (:gen-class)
  (:require [clojure-watch.core :refer [start-watch]]))

; regex for identifying misnamed sookasa files
; also includes capturing groups so that filenames can be rearranged programatically
(def sookasa-regex #"([^.]*)(\..*)(\(.*conflicted copy \d{4}-\d{2}-\d{2}\))\.sookasa")

(defn rename-file
  "Renames incorrectly named files to their proper form"
  [event filename]
  ; print the event and filename to the console - change this to a log file
  (println event filename)
  
  ; destructuring to break apart regex groups
  ; full-filename is the full filename
  ; short-filename is the name of the file without the conflict parens and extensions
  ; extension is the files extension (other than .sookasa)
  ; conflict-string is the (users conflicted copy...) string
  (let [[full-filename short-filename extension conflict-string]
         (re-matches sookasa-regex filename)]
    (println short-filename)
    (println extension)
    (println conflict-string)))

  ; see if the modified files name matches the incorrect pattern
  ; (println 
  ;   (re-matches sookasa-regex filename)))

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
