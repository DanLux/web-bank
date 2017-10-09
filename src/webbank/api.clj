(ns webbank.api
  	(:require
  		[ring.util.response :as util]
  		[webbank.bank :as bank]
		[webbank.date-converter :as converter]))


;;;;;;; DEBUG METHOD
(defn response
	"Converts map responses to standard json."
	[response-map]
	(bank/debug-bank)
	(util/response response-map)
)
;;;;;;;;;;;;;;;;;;;;;


(defn credit-account
	"Forwards http credit request to bank service."
	[request]
	(let [account-number (get request "account")
		description (get request "description")
		amount (new BigDecimal (str (get request "amount")))
		date (converter/from-string (get request "date"))]
	(bank/add-account-operation account-number description amount date)
	(response {}))
)

(defn debit-account
	"Forwards http debit request to bank service."
	[request]
	(let [account-number (get request "account")
		description (get request "description")
		amount (new BigDecimal (str (get request "amount")))
		date (converter/from-string (get request "date"))]
	(bank/add-account-operation account-number  description (- amount) date)
	(response {}))
)

(defn request-balance
	"Forwards http balance request to bank service."
	[request]
	(let [account-number (get request "account")]
	(response
		{"balance" (bank/current-balance account-number)}))
)

(defn request-statement
	"Forwards http statement request to bank service."
	[request]
	(let [account-number (get request "account")
		start-date (converter/from-string (get request "start"))
		end-date (converter/from-string (get request "end"))]
	(response
		(bank/account-statement account-number start-date end-date)))
)

(defn request-debts
	"Forwards http debts request to bank service."
	[request]
	(let [account-number (get request "account")]
	(response
		(bank/periods-of-debt account-number)))
)
