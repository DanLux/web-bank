(ns webbank.bank
	(:require
		[clj-time.core :as clj-time]
		[webbank.date-converter :as converter]))


(def ^:private empty-bank {})
(def bank (atom empty-bank))


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
	"Adds new bank transaction to account identified by account-number."
	[account-number description amount date]
	(when ((complement zero?) amount)
		(let [new_transaction {:description description :amount amount}]
			(as-> (get-account-by account-number) input
				(get input date [])
				(conj input new_transaction)
				(swap! bank assoc-in [account-number date] input)
			)
		))
)

(defn daily-transactions-balance
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
				(do (let [new-balance (+ balance-accumulator (daily-transactions-balance account current-date))
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

(defn ^:private date-from-daily-statement
	"Extracts transaction date from daily-statement."
	[daily-statement]
	(key daily-statement)
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


(defn ^:private next-transaction-dates
	"Maps a transaction date to their very next for each transaction belonging to the account identified by account-number."
	[account-number]
	(let [transaction-dates (keys (get-account-by account-number))]
		(loop [[current successor :as dates] transaction-dates
				result {}]
			(if (nil? current)
				result
				(recur (rest dates) (assoc result current successor))
			)
		))
)

(defn periods-of-debt
	"Returns the periods where balances of the account (identified by account-number) has been negative."
	[account-number]

	(def eve #(clj-time/minus % (clj-time/days 1)))

	(let [full-statement (account-statement account-number)
		next-transactions (next-transaction-dates account-number)
		periods (for [daily-statement full-statement
					:let [balance (balance-from-daily-statement daily-statement)
						date (date-from-daily-statement daily-statement)
						next-date (get next-transactions date)]
					:when (neg? balance)]
					{
						:principal (.abs balance)
						:start (converter/to-string date)
					  	:end (when next-date (converter/to-string (eve next-date)))
					})
		last-debt (last periods)]
		(if (and last-debt (nil? (:end last-debt)))
			(conj (vec (drop-last periods)) (dissoc last-debt :end))
			(vec periods))
	)
)
