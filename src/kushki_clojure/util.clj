(ns kushki-clojure.util
  (:require [cheshire.core :as json]))

(defn api-url
  ([client resource]
   (str (:url client) "/" (:version client) "/" resource))
  ([client resource id]
   (str (:url client) "/" (:version client) "/" resource "/" id)))

(defn- headers
  [type client]
  (case type
    :priv {"Private-Merchant-Id" (:secret-key client)}
    :pub {"Public-Merchant-Id" (:public-key client)}))

(defn api-req
  ([client type payload]
   {:body (json/generate-string payload)
    :headers (headers type client)
    :content-type :json
    :throw-exceptions false
    :as :json
    :coerce :always})
  ([client type]
   {:headers (headers type client)
    :throw-exceptions false
    :as :json
    :coerce :always}))
