(ns kushki-clojure.core
  "API para interactuar con Kushki."
  (:require [kushki-clojure.util :as u]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(defn client
  "Retorna un nuevo cliente Kushki."
  [env secret-key public-key]
  {:url (case env
          :live "https://api.kushkipagos.com"
          :test "https://api-uat.kushkipagos.com")
   :secret-key secret-key
   :public-key public-key
   :version "v1"})

(defn create-token
  "Crea un nuevo token. Retorna un mapa conteniendo un token."
  [client card]
  (:body (http/post (u/api-url client "tokens")
                    (u/api-req client :pub card))))

(defn create-charge
  "Genera un cargo a un token. Retorna un número de ticket."
  [client charge]
  (:body (http/post (u/api-url client "charges")
                    (u/api-req client :priv charge))))
(defn void-charge
  "Anula un cargo usando el número de ticket como referencia."
  [client ticket-number charge]
  (:body (http/delete (u/api-url client "charges" ticket-number)
                      (u/api-req client :priv charge))))

(defn refund-charge
  "Reembolsa un cargo usando el número de ticket como referencia."
  [client ticket-number]
  (:body (http/delete (u/api-url client "refund" ticket-number)
                      (u/api-req client :priv))))
