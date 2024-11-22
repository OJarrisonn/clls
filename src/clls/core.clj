(ns clls.core
  (:gen-class)
  (:require [clls.file :refer [file-object]] 
            [clls.fsys :refer [open-folder list-folder]] 
            [clls.fmt :refer [format-file]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [folder (open-folder (first args))
        files (list-folder folder)]
    (println (map #(format-file (file-object %)) files))))
