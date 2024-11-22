(ns clls.core-test
  (:require [clojure.test :refer :all]
            [clls.fsys :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (not= (open-folder "./resources/mock-folder") nil))))
