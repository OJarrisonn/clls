(ns clls.display
  (:require
   [clls.config :refer [options]]
   [clls.fmt :refer [align-columns max-columns-width]]))

(defn group-dirs-first
  "Given a list of files, group directories first."
  [files]
  (let [dirs (filter :dir files)
        non-dirs (filter (complement :dir) files)]
    (concat dirs non-dirs)))

(defn group-dirs-last
  "Given a list of files, group directories last."
  [files]
  (let [dirs (filter :dir files)
        non-dirs (filter (complement :dir) files)]
    (concat non-dirs dirs)))

(defn display
  "Given a list of file objects, display them in a table."
  [files]
  (let [show-header? (:show-header options)
        headers (:headers options)
        max-widths (max-columns-width (if show-header? (cons headers files) files))
        aligned-files (map #(align-columns % max-widths) files)
        aligned-headers (align-columns (:headers options) max-widths)
        lines (if show-header? (cons aligned-headers aligned-files) aligned-files)
        applied-lines (cond->> lines
                        (:columns options)
                        (map #(select-keys % (:columns options)))

                        (:sort-by options)
                        (sort-by (:sort-by options))

                        (:dirs-first options)
                        (group-dirs-first)

                        (:dirs-last options)
                        (group-dirs-last)

                        true
                        (map #(vals %)))]
    
    (doseq [line applied-lines]
      (doseq [part line] (print (str part " ")))
      (println))))