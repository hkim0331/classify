(ns classify.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [classify.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

;; bump-version.sh updates this.
(def ^:private version "0.3.3")

(defonce session (r/atom {:page :home}))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "classify"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]
   [:p "version " version]])

(declare fetch-docs! move-docs!)

(defn move-docs! [dest]
  (let [url (str "/move/" dest)]
    ;;(js/alert (str "url:" url))
    (GET url {:handler #(fetch-docs!)
              :error-handler #(js/alert "error: " %)})))

(defn buttons-component []
  [:div.columns
   [:div.column [:input {:type "submit"
                         :class "button is-primary"
                         :value "next"
                         :on-click #(fetch-docs!)}]]
   (for [k ["A" "B" "C" "D" "Other"]]
     ^{:key k}
     [:div.column [:input {:type "submit"
                           :class "button is-warning"
                           :value k
                           :on-click #(move-docs! k)}]])])

(defn home-page []
  [:section.section>div.container>div.content
   [:p (:count @session)]
   (when-let [docs (:docs @session)]
     [:pre docs])
   (buttons-component)])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :home]
    ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [^js/Event.token event]
       (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs"  {:handler #(swap! session assoc :docs %)})
  (GET "/count" {:handler #(swap! session assoc :count %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
