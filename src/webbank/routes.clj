(ns webbank.routes
  	(:require
		[compojure.core :refer :all]
  		[compojure.route :as route]
  		[ring.util.response :as util]
  		[webbank.bank :as bank]))


(defn response
	"Converts all map responses to standard json."
	([] (response {}))

	([response-map]
	(util/response {:status 200 :headers {"Content-Type" "application/json"} :body response-map}))
)


(defn credit-account
	"Forward http credit request to bank service."
	[request]
	(let [account-number (get request "account")
		description (get request "description")
		amount (get request "amount")
		date (get request "date")]
	(bank/add-account-operation account-number description amount date))
	(response)
)

(defn debit-account
	"Forward http debit request to bank service."
	[request]
	(let [account-number (get request "account")
		description (get request "description")
		amount (get request "amount")
		date (get request "date")]
	(bank/add-account-operation account-number  description (- amount) date))
	(response)
)

(defn request-balance
	"Forward http balance request to bank service."
	[request]
	(let [account-number (get request "account")]
	(response
		{:balance (bank/current-balance account-number)}))
)

(defn request-statement
	"Forward http statement request to bank service."
	[request]
	(let [account-number (get request "account")
		start-date (get request "start")
		end-date (get request "end")]
	(response
		(bank/account-statement account-number start-date end-date)))
)

(defn request-debts
	"Forward http debts request to bank service."
	[request]
	(let [account-number (get request "account")]
	(response
		(bank/periods-of-debt account-number)))
)


(defroutes server-routes
	(POST "/credit" {body :body} (credit-account body))
	(POST "/debit" {body :body} (debit-account body))
	(POST "/balance" {body :body} (request-balance body))
	(POST "/statement" {body :body} (request-statement body))
	(POST "/debts" {body :body} (request-debts body))
	(route/not-found (util/response {:status 404, :message "Page not found"}))
)
