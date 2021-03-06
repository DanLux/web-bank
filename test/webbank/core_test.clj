(ns webbank.core-test
	(:require
		[clojure.test :refer :all]
		[ring.mock.request :as mock]
		[cheshire.core :refer :all]
		[webbank.core :as core]))

(defn request-application
	[endpoint json]
	(-> (mock/request :post endpoint json)
		(mock/content-type "application/json")
		(core/application)
	)
)

(deftest test-routes
	(testing "/transactions endpoint"
		(testing "for a credit transaction"
			(let [json-request (generate-string { "account" "BA47856", "description" "Salary", "amount" 2371.20M, "date" "2016-07-13"})
				expected-response (generate-string {})
				response (request-application "/transactions" json-request)]

				(is (= 200 (:status response)))
				(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
				(is (= expected-response (:body response)))
			)
		)

		(testing "for a debit transaction"
			(let [json-request (generate-string { "account" "BA47856", "description" "Purchase", "amount" -3651.98M, "date" "2015-12-21"})
				expected-response (generate-string {})
				response (request-application "/transactions" json-request)]

				(is (= 200 (:status response)))
				(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
				(is (= expected-response (:body response)))
			)
		)
	)

	(testing "/balances endpoint"
		(let [json-request (generate-string {"account" "BA47856"})
			expected-response (generate-string {"amount" -1280.78M})
			response (request-application "/balances" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "/statements endpoint"
		(let [json-request (generate-string {"account" "BA47856", "start" "2015-12-21", "end" "2016-07-13"})
			expected-response (generate-string {
				"2015-12-21" [[{"description" "Purchase", "amount" -3651.98M}], -3651.98M],
				"2016-07-13" [[{"description" "Salary", "amount" 2371.20M}], -1280.78M]
			})
			response (request-application "/statements" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "/debts endpoint"
		(let [json-request (generate-string {"account" "BA47856"})
			expected-response (generate-string {
				"debts" [{"principal" 3651.98M, "start" "2015-12-21", "end" "2016-07-12"},
						{"principal" 1280.78M, "start" "2016-07-13"}]
			})
			response (request-application "/debts" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "nonexistent endpoint"
		(let [json-request (generate-string {})
			expected-response "Resource not found"
			response (request-application "/nonexistent" json-request)]

			(is (= 404 (:status response)))
			(is (= "text/html; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)
)
