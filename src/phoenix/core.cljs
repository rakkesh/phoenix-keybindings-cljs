(ns phoenix.core
  (:require [clojure.string :as string]))

(defn bind [key modifiers callback]
  (js/Key. key (clj->js modifiers) callback))

(defn log [& xs]
  (.log js/Phoenix (string/join " " xs)))

(defn notify [^String message]
  (.notify js/Phoenix message))

(defn log-rectangle [prefix rectangle]
  (log prefix
       "x:" (.-x rectangle)
       "y:" (.-y rectangle)
       "width:" (.-width rectangle)
       "half-width:" (/ (.-width rectangle) 2.0)
       "height:" (.-height rectangle)))

(defn debug []
  (log "debug")
  (log-rectangle "Screen's visible frame" (.flippedVisibleFrame (.screen (.focused js/Window))))
  (log-rectangle "App's frame" (.frame (.focused js/Window))))

(defn dbg [x]
  (log x)
  x)

(defn alert [& xs]
  (let [modal (js/Modal.)
        main-screen-rect (.flippedVisibleFrame (.main js/Screen))]
    (set! (.-origin modal) #js {:x (/ (.-width main-screen-rect) 2)
                                :y (/ (.-height main-screen-rect) 2)})
    (set! (.-message modal) (string/join " " xs))
    (set! (.-duration modal) 2)
    (.show modal)))

(defn app-width-adjustment
  [app screen-width]
  (get-in {"iTerm" {1440 8}
           "Emacs" {1440 -4}}
          [(.name app) screen-width]
          0))

(defn half-screen-width
  [window screen-frame]
  (+ (* 0.5 (.-width screen-frame))
     (app-width-adjustment (.app window) (.-width screen-frame))))

(defn half-screen-height
  [window screen-frame]
  (+ (* 0.5 (.-height screen-frame))
     (app-width-adjustment (.app window) (.-height screen-frame))))

(defn to-left-half []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (.-x screen-frame)
                             :y (.-y screen-frame)
                             :width (half-screen-width window screen-frame)
                             :height (.-height screen-frame)}))))

(def margin 0.05)

(defn to-left-up []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (.-x screen-frame)
                             :y (.-y screen-frame)
                             :width (half-screen-width window screen-frame)
                             :height (half-screen-height window screen-frame)}))))

(defn to-left-down []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (.-x screen-frame)
                             :y (+ (.-y screen-frame) (* 0.5 (.-height screen-frame)))
                             :width (half-screen-width window screen-frame)
                             :height (half-screen-height window screen-frame)}))))

(defn to-right-half []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (+ (.-x screen-frame) (* 0.5 (.-width screen-frame)))
                             :y (.-y screen-frame)
                             :width (half-screen-width window screen-frame)
                             :height (.-height screen-frame)}))))

(defn to-right-up []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (+ (.-x screen-frame) (* 0.5 (.-width screen-frame)))
                             :y (.-y screen-frame)
                             :width (half-screen-width window screen-frame)
                             :height (half-screen-height window screen-frame)}))))

(defn to-right-down []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (+ (.-x screen-frame) (* 0.5 (.-width screen-frame)))
                             :y (+ (.-y screen-frame) (* 0.5 (.-height screen-frame)))
                             :width (half-screen-width window screen-frame)
                             :height (half-screen-height window screen-frame)}))))

(defn to-middle []
  (when-let [window (.focused js/Window)]
    (let [screen-frame (.flippedVisibleFrame (.screen window))]
      (.setFrame window #js {:x (+ (.-x screen-frame) (* 0.25 (.-width screen-frame)))
                             :y (.-y screen-frame)
                             :width (half-screen-width window screen-frame)
                             :height (.-height screen-frame)}))))

(defn to-fullscreen []
  (when-let [window (.focused js/Window)]
    (.setFrame window (.flippedVisibleFrame (.screen window)))))

(defn trim-upper []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (.-x window-frame)
                             :y (+ (.-y window-frame) (* margin (.-height window-frame)))
                             :width (.-width window-frame)
                             :height (* (- 1 margin) (.-height window-frame))}))))

(defn trim-lower []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (.-x window-frame)
                             :y (.-y window-frame)
                             :width (.-width window-frame)
                             :height (* (- 1 margin) (.-height window-frame))}))))

(defn trim-right []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (.-x window-frame)
                             :y (.-y window-frame)
                             :width (* (- 1 margin) (.-width window-frame))
                             :height (.-height window-frame)}))))

(defn trim-left []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (+ (.-x window-frame) (* margin (.-width window-frame)))
                             :y (.-y window-frame)
                             :width (* (- 1 margin) (.-width window-frame))
                             :height (.-height window-frame)}))))

(defn extend-upper []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (.-x window-frame)
                             :y (- (.-y window-frame) (* margin (.-height window-frame)))
                             :width (.-width window-frame)
                             :height (* (+ 1 margin) (.-height window-frame))}))))

(defn extend-lower []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (.-x window-frame)
                             :y (.-y window-frame)
                             :width (.-width window-frame)
                             :height (* (+ 1 margin) (.-height window-frame))}))))

(defn extend-right []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (.-x window-frame)
                             :y (.-y window-frame)
                             :width (* (+ 1 margin) (.-width window-frame))
                             :height (.-height window-frame)}))))

(defn extend-left []
  (when-let [window (.focused js/Window)]
    (let [window-frame (.frame window)]
      (.setFrame window #js {:x (- (.-x window-frame) (* margin (.-width window-frame)))
                             :y (.-y window-frame)
                             :width (* (+ 1 margin) (.-width window-frame))
                             :height (.-height window-frame)}))))

(def round js/Math.round)

(defn move-to-screen [window screen]
  (when (and window screen)
    (let [window-frame (.frame window)
          old-screen-rect (.flippedVisibleFrame (.screen window))
          new-screen-rect (.flippedVisibleFrame screen)
          x-ratio (/ (.-width new-screen-rect) (.-width old-screen-rect))
          y-ratio (/ (.-height new-screen-rect) (.-height old-screen-rect))]
      (.setFrame window #js {:width (round (* x-ratio (.-width window-frame)))
                             :height (round (* y-ratio (.-height window-frame)))
                             :x (+ (round (* (- (.-x window-frame) (.-x old-screen-rect))
                                             x-ratio))
                                   (.-x new-screen-rect))
                             :y (+ (round (* (- (.-y window-frame) (.-y old-screen-rect))
                                             y-ratio))
                                   (.-y new-screen-rect))}))))

(defn left-one-monitor []
  (when-let [window (.focused js/Window)]
    (when-not (= (.screen window) (.next (.screen window)))
      (move-to-screen window (.next (.screen window))))))

(defn right-one-monitor []
  (when-let [window (.focused js/Window)]
    (when-not (= (.screen window) (.previous (.screen window)))
      (move-to-screen window (.previous (.screen window))))))

(def last-recently-launched-app (atom nil))

;; Idea:
;;   search visible windows first, then do minimized windows
;; Below no longer cycles through all the windows. Previous
;;   implementation focused on every window and stayed on last
;;   focused. Now it just focuses on first one returned.
(defn focus-or-start [title]
  (if-let [app (.get js/App title)]
    (do
      ;; TODO: could probably switch this to visible windows?
      (let [windows (->> (.windows app)
                         (remove #(= 1 (.isMinimized %))))]
        (if (empty? windows)
          (notify (str "All windows minimized for " title))
          (.focus (first windows)))))
    (when-let [app (.launch js/App title)]
      (reset! last-recently-launched-app title)
      (.focus app))))

(def ^:export app-did-launch
  (js/Event. "appDidLaunch" (fn [app]
                              (when (= @last-recently-launched-app (.name app))
                                (.focus app)
                                (reset! last-recently-launched-app nil)))))
(defn switch-app [key title]
  (bind key ["cmd" "ctrl"] (partial focus-or-start title)))

;; Per Phoenix docs, need to capture results of
;; Phoenix.bind to GC doesn't clean them up.
(def ^:export bound-keys
  [(bind "h" ["alt" "cmd" "ctrl"] debug)

   (bind "left" ["ctrl" "alt" "cmd"] left-one-monitor)
   (bind "right" ["ctrl" "alt" "cmd"] right-one-monitor)

   (bind "left" ["alt" "cmd"] to-left-half)
   (bind "right" ["alt" "cmd"] to-right-half)

   (bind "right" ["shift" "ctrl" "alt"] trim-left)
   (bind "left" ["shift" "ctrl" "alt"] trim-right)
   (bind "down" ["shift" "ctrl" "alt"] trim-upper)
   (bind "up" ["shift" "ctrl" "alt"] trim-lower)

   (bind "left" ["ctrl" "alt"] extend-left)
   (bind "right" ["ctrl" "alt"] extend-right)
   (bind "up" ["ctrl" "alt"] extend-upper)
   (bind "down" ["ctrl" "alt"] extend-lower)

   (bind "f14" ["alt" "cmd"] to-middle)
   (bind "f15" ["alt" "cmd"] to-fullscreen)

   (bind "f16" ["alt" "cmd"] to-left-up)
   (bind "f17" ["alt" "cmd"] to-left-down)
   (bind "f18" ["alt" "cmd"] to-right-up)
   (bind "f19" ["alt" "cmd"] to-right-down)

   (switch-app "c" "iTerm")
   (switch-app "e" "Emacs")
   (switch-app "b" "Google Chrome")
   (switch-app "s" "Safari")
   (switch-app "n" "Notes")])
