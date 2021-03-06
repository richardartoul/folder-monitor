; Note: This program does not account for it files that are already improperly named
; in the folder before the application starts. I decided to leave that feature out
; and focus on this application simply as a "monitoring" program. That being said
; implementing a scan of the entire directory on startup would be trivial.
; Simply use the (file-seq dir) function to get a list of all files in the directory
; and then run them all through the rename-file function. Those that match the regular
; expression pattern will be automatically renamed and those that don't will be left untouched.

; hawk is a simple wrapper around the Java 7 WatchEvent API and Barbary WatchService API
; clojure.java.io is the clojure wrapper around the Java filesystem API
(ns folder-monitor.core
  (:gen-class)
  (:require [clojure.java.io :as io] [hawk.core :as hawk]))

; Compiler boilerplate so functions can be declared in any order
(declare logger)
(declare rename-file)
(declare process-file-event)
(declare watch-folder)

; regex for identifying misnamed sookasa files
; also includes capturing groups so that filenames can be rearranged programatically
(def sookasa-regex #"([^.]*)(\..+)(\(.*conflicted copy \d{4}-\d{2}-\d{2}\))\.sookasa")

; main function that gets invoked when application starts
(defn -main
  ; expects one argument: Which path to watch for file changes
  [path & args]
  ; Starts watching the folder and listening for changes
  (watch-folder path)
  ; Print to console that watch has started
  (def log-entry (str "Starting to watch " path))
  (logger log-entry))

(defn watch-folder
  "Adds a new listener and associated callback for filesystem events at a specified folder"
  [path]
  (hawk/watch! [{:paths [path]
                 :handler process-file-event}]))

; callback function is called everytime a file is created or modified
(defn process-file-event
  "Determine if new file is a file or folder. 
  If its a folder, adds it to the list of folders to watch. 
  Else handle renaming."
  [context {event :kind file :file}]
  
  (do
    (def filename (.toString file))
    
    (if (.isDirectory (io/file filename))
        (do
          ; if its a directory, watch it for events 
          (if (= event :create)
          (watch-folder filename))
          ; Return for testing purposes
          (str "Watch added to " filename))
       ; Else pass the file to the rename function
      (if (or (= event :modify) (= event :create))
       (rename-file event filename)))))
  

(defn rename-file
  "Renames incorrectly named files to their proper form"
  [event filename]
  
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
      ; return the new filename if it was changed
      file-to-create)
      
    ; returns nil if the file wasn't renamed
    nil))

; Originally implemented a logging solution, but disabled because filesystem triggers duplicate events
(defn logger
  "Appends input to log file and adds newline"
  [log-entry]
  ; (spit "folder-monitor-log.txt" (str log-entry "\n") :append true)
  (println log-entry))