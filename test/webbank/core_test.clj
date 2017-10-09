(ns webbank.core-test
	(:require [clojure.test :refer :all]
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
	(testing "/credit endpoint"
		(let [json-request (generate-string { "account" "BA47856", "description" "Salary", "amount" 2371.20, "date" "2015-07-13"})
			expected-response (generate-string {})
			response (request-application "/credit" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "/debit endpoint"
		(let [json-request (generate-string { "account" "BA47856", "description" "Purchase", "amount" 3651.98, "date" "2016-02-21"})
			expected-response (generate-string {})
			response (request-application "/debit" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "/balance endpoint"
		(let [json-request (generate-string {"account" "BA47856"})
			expected-response (generate-string {"balance" -1280.78})
			response (request-application "/balance" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "/statement endpoint"
		(let [json-request (generate-string {"account" "BA47856", "start" "2015-07-13", "end" "2016-02-21"})
			expected-response (generate-string {})
			response (request-application "/statement" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "/debts endpoint"
		(let [json-request (generate-string {"account" "BA47856", "start" "2015-07-13", "end" "2016-02-21"})
			expected-response (generate-string {})
			response (request-application "/debts" json-request)]

			(is (= 200 (:status response)))
			(is (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)

	(testing "nonexistent endpoint"
		(let [json-request (generate-string {})
			expected-response "Page not found"
			response (request-application "/nonexistent" json-request)]

			(is (= 404 (:status response)))
			(is (= "text/html; charset=utf-8" (get-in response [:headers "Content-Type"])))
			(is (= expected-response (:body response)))
		)
	)
)
