(ns classify.routes.home
  (:require
   [classify.config :refer [env]]
   [classify.layout :as layout]
   [clojure.java.io :as io]
   [clojure.java.shell :refer [sh]]
   [clojure.tools.logging :as log]
   [classify.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(def ^:private version "0.2.0")
(def ^:private updated_at "2022-08-04 10:20:32")

;; being reset in docs
(def current (atom nil))

(defn entries [dir]
  (-> dir io/file .list seq))

(defn docs []
  (let [dir  (str (env :base) (env :src))
        file (-> dir
                 entries
                 first)
        src (str dir file)]
    (reset! current src)
    src))

(defn move-docs
  "move file `current` to folder `dest`"
  [{{:keys [dest]} :path-params}]
  (let [dest (str (env :base dest) dest)]
    (log/info @current dest)
    (try
      (sh "mv" @current dest)
      (catch Exception e (log/error (.getMessage e))))))

(defn home-page [request]
  (layout/render request "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> (docs) slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/move/:dest" {:get #(do (move-docs %)
                             (-> (response/ok "OK")
                                 (response/header "Content-Type" "text/plain")))}]])
