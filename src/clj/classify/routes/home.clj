(ns classify.routes.home
  (:require
   [classify.config :refer [env]]
   [classify.layout :as layout]
   [clojure.java.io :as io]
   [clojure.java.shell :refer [sh]]
   [clojure.tools.logging :as log]
   ;;[clojure.string :as str]
   [classify.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(def ^:private version "0.6-SNAPSHOT")
(def ^:private updated_at "2024-08-04 20:30:27")

;; being reset in docs. keep current filename.
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
    (log/info "docs" @current)
    (try
      (slurp @current)
      (catch Exception _
        ""))))

(defn count-docs []
 (let [dir (str (env :base) (env :src))]
  (-> dir entries count)))

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
                    (-> (response/ok (docs))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/move/:dest" {:get #(do (move-docs %)
                             (-> (response/ok "OK")
                                 (response/header "Content-Type" "text/plain")))}]
   ["/count" {:get (fn [_] (-> (response/ok (str (count-docs)))
                               (response/header "Content-Type" "text/plain")))}]])
