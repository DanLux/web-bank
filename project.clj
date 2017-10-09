(defproject webbank "0.1.0-SNAPSHOT"
  :description "A simple web application for managing bank accounts"
  :url "http://localhost:5000"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
  				[compojure "1.6.0"]
  				[ring/ring-json "0.4.0"]
  				[ring/ring-mock "0.3.1"]
  				[cheshire "5.8.0"]
  				[http-kit "2.2.0"]
  				[clj-time "0.14.0"]]
  :main ^:skip-aot webbank.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
