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

(defn reset
	"Forwards http reset request to bank service."
	[request]
	(bank/reset-bank)
	(response {})
)

(defn request-transaction
	"Forwards http transaction request to bank service."
	[request]
	(let [account-number (get request "account")
		description (get request "description")
		amount (.setScale (new BigDecimal (str (get request "amount"))) 2)
		date (converter/from-string (get request "date"))]
	(bank/add-account-transaction account-number description amount date)
	(response {}))
)

(defn request-balance
	"Forwards http balance request to bank service."
	[request]
	(let [account-number (get request "account")]
	(response
		{"amount" (bank/current-balance account-number)}))
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
