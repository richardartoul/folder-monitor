(ns folder-monitor.core-test
  (:require [clojure.test :refer :all]
            [folder-monitor.core :refer :all]
            [clojure.java.io :as io]))

(deftest rename-file-function
  (testing "rename-file is a function"
  (is (= (function? rename-file)))))

(deftest rename-file-valid-name-test
  (testing "unit test for the rename-file function with a properly named file"
    (is (= (rename-file :create "test.txt") nil))))

(deftest rename-file-invalid-name-test
  (testing "unit test for the rename-file function with an improperly named file"
    (is (= (rename-file :create "test.someext(Richie's conflicted copy 2015-07-05).sookasa") 
           "test(Richie's conflicted copy 2015-07-05).someext.sookasa"))))

(deftest add-watch-new-folder-test
  (testing "unit test for the process-file-event function to make sure it registers new watchers for folders added after program starts"
    (.mkdir (io/file "test_folder"))
    (is (= (process-file-event nil {:kind :create :file "test_folder"}) "Watch added to test_folder"))))
