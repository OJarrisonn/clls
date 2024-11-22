(ns clls.file
  (:import (java.nio.file Files LinkOption))
  (:require [clojure.string :as str]))

(defn file-extension
  "Given a File, return its extension."
  [file]
  (let [name (.getName file)]
    (if (re-find #"\." name)
      (last (str/split name #"\."))
      nil)))

(defn standardize-type
  "Given a file type string, return a standardized version of it."
  [type]
  (let [kw (keyword (str/lower-case type))]
    (condp contains? kw
      #{:jpg :jpeg :png :gif :bmp :tiff :svg :webp :ico :heic :heif}
      :image

      #{:mp4 :webm :mov :avi :wmv :flv :mkv :m4v :m4p :m4a :f4v :f4p :f4a :f4b :3gp :3g2 :ogv :ogg :ogm :ogx}
      :video

      #{:mp3 :wav :flac :aac :m4a :wma :ogg :oga :opus :aiff :alac :amr :pcm :aif :au :mka :m3u :m3u8 :pls :qcp :ra :rm :sln :tta :vox :wv :weba}
      :audio

      #{:yaml :yml}
      :yaml

      kw)))

(defn file-type-from-name
  "Given a file, return its type as a keyword. If it's name is special, otherwise return nil"
  [file]
  (condp re-matches (.getName file)
    #"^\.?LICENSE(\..+)?" :license
    #"^\.?README(\..+)?" :readme
    nil))

(defn file-type-from-extension
  "Given a File, return its type as a keyword."
  [file]
  (let [ext (file-extension file)]
    (if ext
      (standardize-type ext)
      :unknown)))

(defn file-type
  "Given a File, return its type as a keyword."
  [file]
  (if (.isDirectory file) :dir
      (or (file-type-from-name file) (file-type-from-extension file))))

(defn get-permissions
  "Given a File, return a string with its permissions."
  [file]
  (into #{} 
        (map #(keyword (str/lower-case (.name %))) 
             (Files/getPosixFilePermissions (.toPath file) 
                                            (into-array LinkOption [])))))

(defn get-user
  "Given a File, return a string with its owner."
  [file]
  (.getName (Files/getOwner (.toPath file) (into-array LinkOption []))))

(defn get-group
  "Given a File, return a string with its group."
  [file]
  (-> (.toPath file)
      (Files/readAttributes "posix:group"
                            (into-array LinkOption [LinkOption/NOFOLLOW_LINKS]))
      (.get "group")
      (.getName)))

(defn get-creation-date
  "Given a File, return its creation date."
  [file]
  (-> (.toPath file)
      (Files/readAttributes "creationTime" 
                            (into-array LinkOption [LinkOption/NOFOLLOW_LINKS]))
      (.get "creationTime")
      (.toMillis)))

(defn get-accessed-date
  "Given a File, return its last accessed date."
  [file]
  (-> (.toPath file)
      (Files/readAttributes "lastAccessTime" 
                            (into-array LinkOption [LinkOption/NOFOLLOW_LINKS]))
      (.get "lastAccessTime")
      (.toMillis)))

(defn file-object
  "Given a File, return a map with its name, type, creation date, and size."
  [file]
  {:file file
   :dir (.isDirectory file)
   :name (.getName file)
   :type (file-type file)
   :created (get-creation-date file) 
   :accessed (get-accessed-date file)
   :modified (.lastModified file)
   :perms (get-permissions file) 
   :user (get-user file) 
   :group (get-group file)
   :size (.length file)})