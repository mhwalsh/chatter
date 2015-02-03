(ns chatter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.page :as page]
            [hiccup.form :as form]
            [ring.util.anti-forgery :as anti-forgery]))

(def messages (atom '()))

(defn generate-message-view
  "this generates the html for displaying messags"
  []
  (page/html5
   [:head
    [:title "chatter"]]
   [:body
    [:h1 "Our Chat App"]
    [:p
     (form/form-to
      [:post "/"]
      (anti-forgery/anti-forgery-field )
      "Name: " (form/text-field "name")
      "Message: " (form/text-field "message")
      (form/submit-button "Submit"))]
    [:p
     [:table
      (map (fn [m] [:tr [:td (:name m)] [:td (:message m)]]) @messages)]]]))

(defn update-messages!
  "this will update the message list"
  [name message]
  (swap! messages conj  {:name name :message message}))

(defroutes app-routes
  (GET "/" [] (generate-message-view))
  (POST "/" {params :params} (do
                               (update-messages! (get params "name") (get params "message"))
                               (generate-message-view)))
  (route/not-found "Not Found"))

(def app (wrap-params app-routes))
