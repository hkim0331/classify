(ns ^:dev/once classify.app
  (:require
   [classify.core :as core]
   [cljs.spec.alpha :as s]
   [expound.alpha :as expound]
   [devtools.core :as devtools]))

(extend-protocol IPrintWithWriter
  symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(set! s/*explain-out* expound/printer)

(enable-console-print!)

(devtools/install!)

(core/init!)
