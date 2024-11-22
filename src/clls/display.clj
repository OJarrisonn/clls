(ns clls.display
  (:require
   [clls.config :refer [options]]
   [clls.fmt :refer [align-columns max-columns-width]]))

(defn display
  "Given a list of file objects, display them in a table."
  [files]
  (let [max-widths (max-columns-width files)
        aligned-files (map #(align-columns % max-widths) files)
        aligned-headers (align-columns (:headers options) max-widths)
        lines (cons aligned-headers aligned-files)
        applied-lines (cond->> lines
                        (:columns options)
                        (map #(select-keys % (:columns options)))
                      
                        true
                        (map #(vals %)))]
    
    (doseq [line applied-lines]
      (doseq [part line] (print (str part " ")))
      (println))))