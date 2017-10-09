(ns webbank.core
  	(:require
  		[org.httpkit.server :refer [run-server]]
		[compojure.core :refer :all]
  		[ring.middleware.json :as middleware]
  		[webbank.routes]))

(defn attach-middleware
	"Add Ring middlewares to parse json request bodies and setup json response headers."
	[app-routes]
  	(-> app-routes
    	(middleware/wrap-json-body)
    	(middleware/wrap-json-response))
)

(defn -main
	"Starts web server on port 5000."
	[& args]
	(println "Web server running on port 5000.")
	(-> webbank.routes/server-routes
		(attach-middleware)
		(run-server {:port 5000})
	)
)
