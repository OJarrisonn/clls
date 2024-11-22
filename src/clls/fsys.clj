(ns clls.fsys
  (:import (java.io File)))

(defn open-folder
  "Get a File to a folder if it exists and is readable, otherwise return nil."
  [folder]
  (let [dir (File. folder)]
    (if (and (.canRead dir) (.isDirectory dir)) dir nil)))

(defn list-folder
  "Given a folder File, return a list of its contents as a File[]."
  [folder]
  (if folder
    (.listFiles folder)
    nil))