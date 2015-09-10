; clojure-watch is a simple wrapper around the Java 7 WatchEvent API
; clojure.java.io is the clojure wrapper around the Java filesystem API
(ns folder-monitor.core
  (:gen-class)
  (:require [clojure-watch.core :refer [start-watch]])
  (:require [clojure.java.io :as io]))

; regex for identifying misnamed sookasa files
; also includes capturing groups so that filenames can be rearranged programatically
(def sookasa-regex #"([^.]*)(\..+)(\(.*conflicted copy \d{4}-\d{2}-\d{2}\))\.sookasa")

(defn rename-file
  "Renames incorrectly named files to their proper form"
  [event filename]
  
  (println event filename)
  
  ; only do something if the regex pattern is found
  (if (re-matches sookasa-regex filename)
    ; destructuring to break apart regex groups
    ; full-filename is the full filename
    ; short-filename is the name of the file without the conflict parens and extensions
    ; extension is the files extension (other than .sookasa)
    ; conflict-string is the (users conflicted copy...) string
    (let [[full-filename short-filename extension conflict-string]
           (re-matches sookasa-regex filename)]
      (def file-to-create (str short-filename conflict-string extension ".sookasa"))
      ; rename the file
      (.renameTo (io/file full-filename) 
                 (io/file file-to-create))
      ; construct the log entry
      (def log-line (str filename " was renamed to " file-to-create " at " (new java.util.Date) "\n"))
      ; append the entry to the log file
      (spit "folder-monitor-log.txt" log-line :append true)
      ; return the new filename if it was changed
      file-to-create)
      
    ; returns nil if the file wasn't renamed
    nil))

; callback function is called everytime a file is created or modified
(defn process-file-event
  "Determine if new file is a file or folder. 
  If its a folder, adds it to the list of folders to watch. 
  Else handle renaming."
  [event filename]
  
  (if (.isDirectory (io/file filename))
     (start-watch [{:path filename
                    :event-types [:create :modify]
                    :bootstrap (fn [path] (println "Starting to watch " filename))
                    :callback process-file-event
                    :options {:recursive true}}])
     (rename-file event filename)))


; main function that gets invoked when application starts
(defn -main
  ; expects one argument: Which path to watch for file changes
  [watch-folder & args]
  ; Starts watching the folder and listening for changes
  (start-watch [{:path watch-folder
                 :event-types [:create :modify]
                 :bootstrap (fn [path] (println "Starting to watch " path))
                 :callback process-file-event
                 ; recursively monitor subdirectories
                 :options {:recursive true}}]))
