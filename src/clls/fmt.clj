(ns clls.fmt
  (:require
   [clls.config :refer [options]]) 
  (:import
   (java.util Date)))

(def default-icon " ")
(def six-months (* 30 24 60 60 1000 6))

(def file-columns
  [:dir :perms :user :group :size :created :accessed :modified :name])

(def months
  ["Jan" "Feb" "Mar" "Apr" "May" "Jun" "Jul" "Aug" "Sep" "Oct" "Nov" "Dec"])

(defn quote-file-name
  "Given a file name, return it quoted if it contain spaces."
  [name]
  (if (re-find #"\s" name)
    (str "\"" name "\"")
    name))

(defn iconify-file-name
  "Given a file name, return it with an icon acording to it's type."
  [name type]
  (str (or (get (:icons options) type) (:unknown (:icons options))) name))

(defn append-dir-slash
  "If a file is a directory, append a slash to its name."
  [name is-dir]
  (if is-dir
    (str name "/")
    name))

(defn format-date
  "Given a date, return it formatted as a string."
  [date]
  (let [date (Date. date)
        delta (- (.getTime (Date.)) (.getTime date))]
    (str (format "%02d" (.getDate date)) 
         " " (nth months (.getMonth date))  
         " " (if (< delta six-months) 
               (str (format "%02d" (.getHours date)) ":" (format "%02d" (.getMinutes date))) 
               (+ 1900 (.getYear date))))))

(defn format-size
  "Given a size in bytes, return it formatted as a string."
  [size]
  (let [binary (:binary options) 
        scale (if binary 1024 1000)]
    (cond 
      (< size scale) (str size) 
      (< size (* scale scale)) (str (int (/ size scale)) (if binary "Ki" "K")) 
      (< size (* scale scale scale scale)) (str (int (/ size (* scale scale))) (if binary "Mi" "M")) 
      :else (str (int (/ size (* scale scale scale scale))) (if binary "Gi" "G")))))

(defn format-dir-flag
  "Given a file, return a string with a flag if it's a directory."
  [file]
  (if (:dir file) "d" "."))

(defn format-permissions
  "Given a file object returns a string with its permissions in the ls format."
  [file]
  (let [perms (:perms file)]
    (str (if (:dir-as-perm options) (if (:dir file) "d" ".") "")
         (if (contains? perms :owner_read) "r" "-")
         (if (contains? perms :owner_write) "w" "-")
         (if (contains? perms :owner_execute) "x" "-")
         (if (contains? perms :group_read) "r" "-")
         (if (contains? perms :group_write) "w" "-")
         (if (contains? perms :group_execute) "x" "-")
         (if (contains? perms :others_read) "r" "-")
         (if (contains? perms :others_write) "w" "-")
         (if (contains? perms :others_execute) "x" "-"))))

(defn format-file
    "Given a file object, return a string with its name, icon, creation date, and size."
    [file]
    {:name (append-dir-slash ;; Format the file name including icons, quotes and slashes
            (iconify-file-name (quote-file-name (:name file)) 
                               (:type file)) 
            (.isDirectory (:file file)))
     :created (format-date (:created file)) 
     :accessed (format-date (:accessed file)) 
     :modified (format-date (:modified file))
     :perms (format-permissions file) 
     :dir (format-dir-flag file)
     :user (:user file) 
     :group (:group file)
     :size (format-size (:size file))})

(defn max-columns-width
  "Given a list of maps, return a map with the maximum width of each column."
  [files ]
  (let [rows (conj files (:headers options))]
    (into 
     {} 
     (map 
      (fn [col] 
        [col (apply max (map #(count (get % col)) rows))]) 
      file-columns))))

(defn align-columns
  "Given a list of maps and a map with the maximum width of each column, return a list of maps with the columns aligned."
  [file max-widths]
  (let [alignment (:align options)
        align (fn [col val] 
                (let [pad (apply str (repeat (- (get max-widths col) (count val)) " "))
                      align (get alignment col)] 
                  (if (= align :left) (str val pad) (str pad val))))]
    (reduce 
     (fn [acc col] 
       (assoc acc col (align col (get file col)))) 
     {} 
     file-columns)))
