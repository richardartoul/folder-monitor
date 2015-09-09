(ns folder-monitor.core-test
  (:require [clojure.test :refer :all]
            [folder-monitor.core :refer :all]))

(deftest rename-file-valid-name-test
  (testing "unit test for the rename-file function"
    (is (= (rename-file :create "test.txt") nil))))

(deftest rename-file-invalid-name-test
  (testing "unit test for the rename-file function"
    (is (= (rename-file :create "test.ext(Richie's conflicted copy 2015-07-05).sookasa") 
           "test(Richie's conflicted copy 2015-07-05).ext.sookasa"))))
