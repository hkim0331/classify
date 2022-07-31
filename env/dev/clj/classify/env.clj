(ns classify.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [classify.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[classify started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[classify has shut down successfully]=-"))
   :middleware wrap-dev})
