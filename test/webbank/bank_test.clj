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
			amount 1405.00M
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
			amount -760.75M
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
			credit-amount 245.74M
			debit-amount -170.21M
			date (converter/from-string "2017-04-03")
			expected-credit-operation {:description credit-description, :amount credit-amount}
			expected-debit-operation {:description debit-description, :amount debit-amount}]

			(reset-bank)
			(add-account-operation account-number credit-description credit-amount date)
			(add-account-operation account-number debit-description debit-amount date)
			(is (= 1 (count (vals @bank))))
			(is (some (partial = expected-credit-operation) (get-in @bank [account-number date])))
			(is (some (partial = expected-debit-operation) (get-in @bank [account-number date])))
			(is (= 2 (count (get-in @bank [account-number date]))))
		)
	)

	(testing "add multiple operations on different days and keep them sorted"
		(let [account-number "00007"
			feb-date (converter/from-string "2017-02-21")
			jul-date (converter/from-string "2017-07-14")
			dec-date (converter/from-string "2017-12-31")
			expected-feb-operation {:description "February salary", :amount 6500.00M}
			expected-jul-operation {:description "July vacation", :amount -2604.01M}
			expected-dec-operation {:description "New year purchases", :amount -590.49M}]

			(reset-bank)
			(add-account-operation account-number "New year purchases" -590.49M dec-date)
			(add-account-operation account-number "February salary" 6500.00M feb-date)
			(add-account-operation account-number "July vacation" -2604.01M jul-date)
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

(deftest test-balances-and-statements
	(let [account-number "BCA22156-0"
		just-made-account-number "99999XYZ-0"
		nonexistent-account-number "XXX-NOT-FOUND-XXX"
		before-yesterday (converter/from-string "2017-10-15")
		yesterday (converter/from-string "2017-10-16")
		today (converter/from-string "2017-10-17")
		tomorrow (converter/from-string "2017-10-18")]

		(reset-bank)
		(get-account-by just-made-account-number)
		(add-account-operation account-number "Purchase on Amazon" -3.34M yesterday)
		(add-account-operation account-number "Withdraw" -180.00M today)
		(add-account-operation account-number "Purchase on Uber" -45.23M yesterday)
		(add-account-operation account-number "Deposit" 1000.00M before-yesterday)


		(testing "daily-balance"
			(testing "when no transactions were made on given date"
				(is (zero? (daily-balance (get @bank account-number) tomorrow))))

			(testing "when transactions were made on given date"
				(is (= 1000.00M (daily-balance (get @bank account-number) before-yesterday)))
				(is (= -48.57M (daily-balance (get @bank account-number) yesterday)))
				(is (= -180.00M (daily-balance (get @bank account-number) today))))
		)


		(testing "current-balance"
			(testing "when there is no such account"
				(is (zero? (current-balance nonexistent-account-number))))

			(testing "when account has just been created"
				(is (zero? (current-balance just-made-account-number))))

			(testing "for a regular used account"
				(is (= 771.43M (current-balance account-number))))
		)


		(testing "account-statement"
			(testing "when there is no such account"
				(is (= (sorted-map) (account-statement nonexistent-account-number))))

			(testing "when account has just been created"
				(is (= (sorted-map) (account-statement just-made-account-number))))

			(testing "for a regular used account"
				(testing "without specific period"
					(let [expected-statement {
						before-yesterday [
							[{:description "Deposit", :amount 1000.00M}],
							1000.00M
						],

						yesterday [
							[{:description "Purchase on Amazon", :amount -3.34M}, {:description "Purchase on Uber", :amount -45.23M}],
							951.43M
						],

						today [
							[{:description "Withdraw", :amount -180.00M}],
							771.43M
						]
					}]

					(is (= expected-statement (account-statement account-number))))
				)

				(testing "given an early start date and past end date"
					(let [expected-statement {
						before-yesterday [
							[{:description "Deposit", :amount 1000.00M}],
							1000.00M
						],

						yesterday [
							[{:description "Purchase on Amazon", :amount -3.34M}, {:description "Purchase on Uber", :amount -45.23M}],
							951.43M
						]
					}]

					(is (= expected-statement (account-statement account-number (converter/from-string "2010-08-26") yesterday)))
					(is (= expected-statement (account-statement account-number (converter/from-string "2017-10-15") yesterday)))
					(is (= expected-statement (account-statement account-number before-yesterday yesterday))))
				)

				(testing "given an late start date and future end date"
					(let [expected-statement {
						yesterday [
							[{:description "Purchase on Amazon", :amount -3.34M}, {:description "Purchase on Uber", :amount -45.23M}],
							951.43M
						],

						today [
							[{:description "Withdraw", :amount -180.00M}],
							771.43M
						]
					}]

					(is (= expected-statement (account-statement account-number yesterday tomorrow)))
					(is (= expected-statement (account-statement account-number yesterday today))))
				)

				(testing "multi-arity equivalence"
					(is (= (account-statement account-number) (account-statement account-number before-yesterday today)))
				)
			)
		)
	)
)

(deftest test-periods-of-debt
	(testing ""
	)
)
