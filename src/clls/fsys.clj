(ns clls.fsys
  (:require
   [clls.config :refer [options]]) 
  (:import
   (java.io File)))

(defn open-folder
  "Get a File to a folder if it exists and is readable, otherwise return nil."
  [folder]
  (let [dir (File. folder)]
    (if (and (.canRead dir) (.isDirectory dir)) dir nil)))

(defn list-folder
  "Given a folder File, return a list of its contents as a File[]."
  [folder]
  (if folder
    (cond->> (.listFiles folder)
      (:files-only options)
      (filter #(not (.isDirectory %)))
      
      (:dirs-only options)
      (filter #(.isDirectory %))
      
      (not (:all options))
      (filter #(not (.isHidden %))))
    nil))