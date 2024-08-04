(ns classify.core
  (:require
   ;; [markdown.core :refer [md->html]]
   [ajax.core :refer [GET POST]]
   [classify.ajax :as ajax]
   [clojure.string :as string]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown-to-hiccup.core :as m]
   [re-catch.core :as rc]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.core :as reitit])
  (:import goog.History))

;; bump-version.sh updates this.
(def ^:private version "v0.6-SNAPSHOT")

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
  (let [c (:count @session)]
    ;; (js/alert (:docs @session))
    [:section.section>div.container>div.content
     [:p c]
     (when (pos? c)
       [:div
        [buttons-component]
        [rc/catch (m/md->hiccup (:docs @session))]
        [buttons-component]])]))


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
