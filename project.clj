(defproject classify "v0.4.42"

  :description "classify students report files, etc."
  :url "http://localhost:3000"

  :dependencies [[ch.qos.logback/logback-classic "1.5.6"]
                 [cljs-ajax "0.8.4"]
                 [clojure.java-time "1.4.2"]
                 [com.cognitect/transit-clj "1.0.333"]
                 [com.cognitect/transit-cljs "0.8.280"]
                 [com.google.javascript/closure-compiler-unshaded "v20240317"]
                 [cprop "0.1.20"]
                 [expound "0.9.0"]
                 [funcool/struct "1.4.0"]
                 [json-html "0.4.7"]
                 [luminus-transit "0.1.6"]
                 [luminus-undertow "0.1.18"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.12.1"]
                 [metosin/muuntaja "0.6.10"]
                 [metosin/reitit "0.7.0"] [metosin/ring-http-response "0.9.3"]
                 [mount "0.1.18"]
                 [nrepl "1.1.2"]
                 [org.clojure/clojure "1.11.3"]
                 [org.clojure/clojurescript "1.11.132" :scope "provided"]
                 [org.clojure/core.async "1.6.681"]
                 [org.clojure/tools.cli "1.1.230"]
                 [org.clojure/tools.logging "1.3.0"]
                 [org.webjars.npm/bulma "1.0.1"]
                 [org.webjars.npm/material-icons "1.13.2"]
                 [org.webjars/webjars-locator "0.52"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [reagent "1.2.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.12.1"]
                 [ring/ring-defaults "0.5.0"]
                 [selmer "1.12.61"]
                 [thheller/shadow-cljs "2.28.8" :scope "provided"]]

  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot classify.core

  :plugins []
  :clean-targets ^{:protect false}
  [:target-path "target/cljsbuild"]


  :profiles
  {:uberjar {:omit-source true

             :prep-tasks ["compile" ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]
             :aot :all
             :uberjar-name "classify.jar"
             :source-paths ["env/prod/clj"  "env/prod/cljs"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "1.0.7"]
                                 [cider/piggieback "0.5.3"]
                                 [org.clojure/tools.namespace "1.5.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.12.1"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.25.0"]
                                 [jonase/eastwood "1.2.4"]
                                 [cider/cider-nrepl "0.28.5"]]


                  :source-paths ["env/dev/clj"  "env/dev/cljs" "test/cljs"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
