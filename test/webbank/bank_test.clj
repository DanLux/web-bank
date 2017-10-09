(ns webbank.bank-test
	(:require
		[clojure.test :refer :all]
		[webbank.date-converter :as converter]
		[webbank.bank :refer :all]))


(deftest test-get-account-by
	(let [account-number "ABC01678"]
		(testing "when there is no such account"
			(reset-bank)
			(is (nil? (get @bank account-number)))
			(is (zero? (count (vals @bank))))
			(is (= (sorted-map) (get-account-by account-number)))
			(is (= 1 (count (vals @bank))))
		)

		(testing "when there is such account"
			(is ((complement nil?) (get @bank account-number)))
			(is (= 1 (count (vals @bank))))
			(is (= (get @bank account-number) (get-account-by account-number)))
		)
	)
)

(deftest test-add-account-operation
	(testing "add single credit operation"
		(let [account-number "ABC01678"
			description "Deposit"
			amount 1405.00
			date (converter/from-string "2017-08-22")
			expected-operation {:description description, :amount amount}]

			(reset-bank)
			(add-account-operation account-number description amount date)
			(is (= 1 (count (vals @bank))))
			(is (every? (partial = expected-operation) (get-in @bank [account-number date])))
		)
	)

	(testing "add single debit operation"
		(let [account-number "GTA961678-X"
			description "Withdraw"
			amount -760.75
			date (converter/from-string "2017-09-01")
			expected-operation {:description description, :amount amount}]

			(reset-bank)
			(add-account-operation account-number description amount date)
			(is (= 1 (count (vals @bank))))
			(is (every? (partial = expected-operation) (get-in @bank [account-number date])))
		)
	)

	(testing "add multiple operations on same day"
		(let [account-number "GTA961678-X"
			credit-description "Withdraw"
			debit-description "Deposit"
			credit-amount 245.74
			debit-amount -170.21
			date (converter/from-string "2017-04-03")
			expected-credit-operation {:description credit-description, :amount credit-amount}
			expected-debit-operation {:description debit-description, :amount debit-amount}]

			(reset-bank)
			(add-account-operation account-number credit-description credit-amount date)
			(add-account-operation account-number debit-description debit-amount date)
			(is (= 1 (count (vals @bank))))
			(is (some (partial = expected-credit-operation) (get-in @bank [account-number date])))
			(is (some (partial = expected-debit-operation) (get-in @bank [account-number date])))
			(= 2 (count (get-in @bank [account-number date])))
		)
	)

	(testing "add multiple operations on different days and keep them sorted"
		(let [account-number "00007"
			feb-date (converter/from-string "2017-02-21")
			jul-date (converter/from-string "2017-07-14")
			dec-date (converter/from-string "2017-12-31")
			expected-feb-operation {:description "February salary", :amount 6500.00}
			expected-jul-operation {:description "July vacation", :amount -2604.01}
			expected-dec-operation {:description "New year purchases", :amount -590.49}]

			(reset-bank)
			(add-account-operation account-number "New year purchases" -590.49 dec-date)
			(add-account-operation account-number "February salary" 6500.00 feb-date)
			(add-account-operation account-number "July vacation" -2604.01 jul-date)
			(is (= 1 (count (vals @bank))))
			(is (every? (partial = expected-feb-operation) (get-in @bank [account-number feb-date])))
			(is (every? (partial = expected-jul-operation) (get-in @bank [account-number jul-date])))
			(is (every? (partial = expected-dec-operation) (get-in @bank [account-number dec-date])))
			(is (= feb-date (first (keys (get @bank account-number)))))
			(is (= jul-date (second (keys (get @bank account-number)))))
			(is (= dec-date (last (keys (get @bank account-number)))))
		)
	)
)

(deftest test-current-balance
	(testing ""
	)
)

(deftest test-daily-balance
	(testing ""
	)
)

(deftest test-account-statement
	(testing ""
	)
)

(deftest test-periods-of-debt
	(testing ""
	)
)
