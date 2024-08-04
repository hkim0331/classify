(ns classify.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   ;; [markdown.core :refer [md->html]]
   [markdown-to-hiccup.core :as m]
   [classify.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

;; bump-version.sh updates this.
(def ^:private version "v0.5.53")

(defonce session (r/atom {:page :home}))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page (:page @session)) "is-active")}
   title])
(comment
  (println "hello")
  (try
    (m/md->hiccup "今大学生活が始まって<span style=\"color: red;\">*約３か月*</span>が立つが今使うことのできる自分で管理する自由な時間についてわかってきた気がする。入学してきたときは自由な時間と言われたら遊ぶようなことしか考えてなかったけれど<span style=\"color: red;\">*自分を磨くという意味での「自由」な時間*</span>でもあると思えるようになりました。早めに校長先生の話を知ることができて良かったと思いました。")
    (catch js/Error e
      (println "zero div")
      ))
  :rcf)

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
   (.log js/console (str @session))
   (buttons-component)
   (when-let [docs (:docs @session)]
     ;; (md->html docs)
     ;; was [:pre docs]
     (m/md->hiccup docs))
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
