(ns clls.display
  (:require [clls.fmt :refer [max-columns-width align-columns columns-headers]]))

(defn display
  "Given a list of file objects, display them in a table."
  [files opts]
  (let [max-widths (max-columns-width files)
        aligned-files (map #(align-columns % max-widths) files)
        aligned-headers (align-columns columns-headers max-widths)
        lines (cons aligned-headers aligned-files)
        applied-lines (cond->> lines
                        (:columns opts)
                        (map #(select-keys % (:columns opts)))
                      
                        true
                        (map #(vals %)))]
    
    (doseq [line applied-lines]
      (doseq [part line] (print (str part " ")))
      (println))))