(ns clls.config)

(def options
  {:columns [:dir :perms :size :user :modified :name]
   :headers {:dir "Dir"
             :perms "Permissions"
             :user "User"
             :group "Group"
             :size "Size"
             :created "Date Created"
             :accessed "Date Accessed"
             :modified "Date Modified"
             :name "Name"}
   :align {:dir :left
           :perms :left
           :user :left
           :group :left
           :size :right
           :created :left
           :accessed :left
           :modified :left
           :name :left}
   :icons {:dir "󰉋  "
           :image "󰋩 "
           :video " "
           :audio " "
           :license " "
           :readme " "
           :unknown " "}})