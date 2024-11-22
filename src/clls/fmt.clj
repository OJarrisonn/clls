(ns clls.fmt
  (:import (java.util Date)))

(def default-icon " ")
(def six-months (* 30 24 60 60 1000 6))

(def file-icons
  {:dir "󰉋 "
   :image "󰋩 "
   :video " "
   :audio " "
   :license " "
   :readme " "
   :unknown default-icon})

(def file-columns
  [:dir :perms :user :group :size :created :accessed :modified])

(def formated-columns
  {:dir "Dir"
   :perms "Permissions"
   :user "User"
   :group "Group"
   :size "Size"
   :created "Date Created"
   :accessed "Date Accessed"
   :modified "Date Modified"})

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
  (str (or (get file-icons type) default-icon) name))

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
    (str (.getDate date) 
         " " (nth months (.getMonth date)) 
         " " (if (< delta six-months) 
               (str (.getHours date) ":" (.getMinutes date)) 
               (.getYear date)))))

(defn format-size
  "Given a size in bytes, return it formatted as a string."
  [size]
  (cond
    (< size 1024) (str size)
    (< size 1048576) (str (int (/ size 1024)) "Ki")
    (< size 1073741824) (str (int (/ size 1048576)) "Mi")
    :else (str (int (/ size 1073741824)) "Gi")))

(defn format-permissions
  "Given a set of PosixFilePermissions, return a string with its permissions in the ls format."
  [perms]
  (str (if (contains? perms :owner_read) "r" "-")
       (if (contains? perms :owner_write) "w" "-")
       (if (contains? perms :owner_execute) "x" "-")
       (if (contains? perms :group_read) "r" "-")
       (if (contains? perms :group_write) "w" "-")
       (if (contains? perms :group_execute) "x" "-")
       (if (contains? perms :others_read) "r" "-")
       (if (contains? perms :others_write) "w" "-")
       (if (contains? perms :others_execute) "x" "-")))

(defn format-dir-flag
  "Given a file, return a string with a flag if it's a directory."
  [file]
  (if (:dir file) "d" "."))

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
     :perms (format-permissions (:perms file)) 
     :dir (format-dir-flag file)
     :user (:user file) 
     :group (:group file)
     :size (format-size (:size file))})