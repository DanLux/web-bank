(ns webbank.date-converter
	(:require
		[clj-time.core :as t]))

(defn from-string
	"Converts string to local time date object."
	[date-string]
	(let [ [year month day] (mapv #(Integer/parseInt %) (re-seq  #"\d+" date-string)) ]
		(t/local-date year month day)
	)
)

(defn to-string
	"Converts local time date object to string."
	[date]
	(let [ year (format "%04d" (t/year date))
		month (format "%02d" (t/month date))
		day (format "%02d" (t/day date))]
		(str year "-" month "-" day))
)