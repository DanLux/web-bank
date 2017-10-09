(ns webbank.date-converter-test
	(:require
		[clojure.test :refer :all]
		[clj-time.core :as clj-time]
		[webbank.date-converter :as date-converter]))


(def br-valentines-string "2015-06-12")
(def br-valentines-date (clj-time/local-date 2015 06 12))

(def easter-string "2017-04-16")
(def easter-date (clj-time/local-date 2017 04 16))

(def christmas-string "2017-12-25")
(def christmas-date (clj-time/local-date 2017 12 25))

(def reveillon-string "2018-01-01")
(def reveillon-date (clj-time/local-date 2018 01 01))

(deftest test-converter
	(testing "from string to date conversion"
		(is (= br-valentines-date (date-converter/from-string br-valentines-string)))
		(is (= easter-date (date-converter/from-string easter-string)))
		(is (= christmas-date (date-converter/from-string christmas-string)))
		(is (= reveillon-date (date-converter/from-string reveillon-string)))
	)

	(testing "from date to string conversion"
		(is (= br-valentines-string (date-converter/to-string br-valentines-date)))
		(is (= easter-string (date-converter/to-string easter-date)))
		(is (= christmas-string (date-converter/to-string christmas-date)))
		(is (= reveillon-string (date-converter/to-string reveillon-date)))
	)
)