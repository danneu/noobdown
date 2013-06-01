(ns noobdown.parse
  (:require [clojure.string :as str]
            [hiccup.core :as hiccup]))

(defn group-by-node
  "Nodes are delimited by a blank line (two \newline).
   Elements within a node are delimited by a line break (one \newline)."
  [markdown]
  (letfn [(split-by-nn [s] (str/split s #"\n\n"))
          (split-by-n [s] (str/split s #"\n"))]
    (map split-by-n (split-by-nn markdown))))

(defn tag-node
  "Adds the right identifier to a node vector to prepare it for the render dispatch."
  [[first-element :as node]]
  (condp #(.startsWith %2 %1) first-element
    "    " (into [:code] node)
    "-" (into [:unordered-list] node)
   "1." (into [:ordered-list] node)
   "http:" (into [:anchor] node)
   "######" (into [:heading6] node)
   "#####" (into [:heading5] node)
   "####" (into [:heading4] node)
   "###" (into [:heading3] node)
   "##" (into [:heading2] node)
   "#" (into [:heading1] node)
   (into [:paragraph] node)))


;; Transformation ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Helpers
(defn insignificant-chars
  [s]
  (inc (.indexOf s " ")))

(defn strip-insignificant-chars
  [s]
  (subs s (insignificant-chars s)))

(defn render-li
  [element]
  [:li (strip-insignificant-chars element)])


;; Transform tagged node and child elements into Hiccup datastructure
(defmulti hiccup-node first)

(defmethod hiccup-node :paragraph
  [[_ & elements]]
  (into [:p] elements))

(defmethod hiccup-node :unordered-list
  [[_ & elements]]
  (into [:ul] (map render-li elements)))

(defmethod hiccup-node :ordered-list
  [[_ & elements]]
  (into [:ol] (map render-li elements)))

(defmethod hiccup-node :anchor
  [[_ elements]]
  (into [:a] [{:href elements} elements]))

(defmethod hiccup-node :code
  [[_ & elements]]
  (into [:code] elements))

(defmethod hiccup-node :heading1
  [[_ & elements]]
  (into [:h1] (map #(subs % 2) elements)))

(defmethod hiccup-node :heading2
  [[_ & elements]]
  (into [:h2] (map #(subs % 3) elements)))

(defmethod hiccup-node :heading3
  [[_ & elements]]
  (into [:h3] (map #(subs % 4) elements)))

(defmethod hiccup-node :heading4
  [[_ & elements]]
  (into [:h4] (map #(subs % 5) elements)))

(defmethod hiccup-node :heading5
  [[_ & elements]]
  (into [:h5] (map #(subs % 6) elements)))

(defmethod hiccup-node :heading6
  [[_ & elements]]
  (into [:h6] (map #(subs % 7) elements)))


(defn to-hiccup
  "Converts Noobdown string into list of Hiccup datastructures."
  [noobdown]
  (map hiccup-node (map tag-node (group-by-node noobdown))))

(defn to-html
  "Converts Noobdown string into HTML."
  [noobdown]
  (hiccup/html (to-hiccup noobdown)))


;; Tests ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(assert (= (tag-node ["Hello"])
           [:paragraph "Hello"]))

(assert (= (tag-node ["- abc"])
           [:unordered-list "- abc"]))

(assert (= (tag-node ["http://example.com"])
           [:anchor "http://example.com"]))

(assert (= (tag-node ["    (+ 1 2)"])
           [:code "    (+ 1 2)"]))

(assert (= (tag-node ["# Header 1"])
           [:heading1 "# Header 1"]))

(assert (= (tag-node ["1. Item 1" "2. Item 2" "100. Item 100"])
           [:ordered-list "1. Item 1" "2. Item 2" "100. Item 100"]))


(assert (= (hiccup-node [:unordered-list "- a" "- b" "- c"])
           [:ul [:li "a"] [:li "b"] [:li "c"]]))

(assert (= (hiccup-node [:ordered-list "1. Item 1" "2. Item 2" "100. Item 100"])
           [:ol [:li "Item 1"] [:li "Item 2"] [:li "Item 100"]]))

(assert (= (hiccup-node [:paragraph "a"])
           [:p "a"]))

(assert (= (hiccup-node [:anchor "http://example.com"])
           [:a {:href "http://example.com"} "http://example.com"]))

(assert (= (hiccup-node [:code "    (+ 1 2)"])
           [:code "    (+ 1 2)"]))

(assert (= (hiccup-node [:heading1 "# Header 1"])
           [:h1 "Header 1"]))
