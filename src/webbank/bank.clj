(ns webbank.bank
	(:require
		[clj-time.core :as clj-time]))


(def ^:private empty-bank {})
(def bank (atom empty-bank))


(defn debug-bank []
	(println (str "Bank status:\n" @bank "\n"))
)

(defn reset-bank
	"Returns bank to its original state."
	[]
	(reset! bank empty-bank)
)

(defn ^:private create-account
	"Adds new empty account to bank identified by account-number."
	[account-number]
	(->
		bank
		(swap! #(assoc % account-number (sorted-map)))
		(get account-number)
	)
)

(defn get-account-by
	"Retrieves bank account identified by account-number. In case there is none, creates a new account and returns it."
	[account-number]
	(if-let [bank-account (get @bank account-number)]
		bank-account
		(create-account account-number)
	)
)

(defn add-account-transaction
	"Adds new transaction to bank account identified by account-number.
	This transaction is represented by a short description, an amount and the date it happened."
	[account-number description amount date]
	(let [new_transaction {:description description :amount amount}]
		(as-> (get-account-by account-number) input
			(get input date [])
			(conj input new_transaction)
			(swap! bank assoc-in [account-number date] input)
		)
	)
)

(defn daily-balance
	"Returns the balance of all account transactions which happened on date."
	[account date]
	(->>
		(get account date [])
		(map #(:amount %))
		(reduce + 0.00M)
	)
)

(defn account-statement
	"Returns a statement with transactions and balances grouped by date for the bank account identified by account-number."
	([account-number]
	(let [account (get-account-by account-number)]
		(loop [[current-date & next-dates] (keys account)
				statement-map account
				balance-accumulator 0.00M]
			(if current-date
				(do (let [new-balance (+ balance-accumulator (daily-balance account current-date))
						  current-transactions (get account current-date [])]
					(recur next-dates (assoc statement-map current-date [current-transactions new-balance]) new-balance))
				)
				statement-map
			)
		)
	))

	([account-number start-date end-date]
	(->> (account-statement account-number)
		(drop-while #(clj-time/before? (first %) start-date))
		(take-while #((complement clj-time/after?) (first %) end-date))
		(into (sorted-map))
	))
)

(defn ^:private balance-from-daily-statement
	"Extracts balance from daily-statement."
	[daily-statement]
	(if daily-statement
		((comp second val) daily-statement)
		0.00M
	)
)

(defn current-balance
	"Gets current balance related to the bank account identified by account-number."
	[account-number]
	(balance-from-daily-statement (last (account-statement account-number)))
)

(defn periods-of-debt
	"Returns the periods where balances of the account (identified by account-number) has been negative."
	[account-number]
	(println "To be implemented")
	{}
)
