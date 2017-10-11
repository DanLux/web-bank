(ns webbank.core
  	(:require
  		[org.httpkit.server :refer [run-server]]
		[compojure.core :refer :all]
		[compojure.route :as route]
  		[ring.middleware.json :as middleware]
  		[webbank.api :as api]))


(defroutes server-routes
	(POST "/transactions" {body :body} (api/request-transaction body))
	(POST "/balances" {body :body} (api/request-balance body))
	(POST "/statements" {body :body} (api/request-statement body))
	(POST "/debts" {body :body} (api/request-debts body))
	(route/not-found "Resource not found")
)

(defn attach-middleware
	"Adds Ring middlewares to parse json request bodies and setup json response headers."
	[app-routes]
  	(-> app-routes
    	(middleware/wrap-json-body)
    	(middleware/wrap-json-response))
)

(def application (attach-middleware server-routes))

(defn -main
	"Starts web server on port 5000."
	[& args]
	(println "Web server running on port 5000.")
	(run-server application {:port 5000})
)