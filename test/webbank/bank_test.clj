(ns webbank.bank-test
	(:require
		[clojure.test :refer :all]
		[clj-time.core :as clj-time]
		[webbank.bank :refer :all]))


(deftest test-get-account-by
	(let [account-number "ABC01678"]
		(testing "when there is no such account"
			(reset-bank)
			(is (nil? (get @bank account-number)))
			(is (= (sorted-map) (get-account-by account-number)))
		)

		(testing "when there is such account"
			(is ((complement nil?) (get @bank account-number)))
			(is (= (get @bank account-number) (get-account-by account-number)))
		)
	)
)
