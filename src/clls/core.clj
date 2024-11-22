(ns clls.core
  (:gen-class)
  (:require [clls.file :refer [file-object]] 
            [clls.fsys :refer [open-folder list-folder]] 
            [clls.display :refer [display]]
            [clls.fmt :refer [format-file]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [folder (open-folder (or (first args) "."))
        files (list-folder folder)
        entries (map #(format-file (file-object %)) files)]
    (display entries)))
