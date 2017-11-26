(ns kushki-clojure.util-test
  (:require [clojure.test :refer :all]
            [kushki-clojure.util :refer :all]))

(def stub-client {:url "https://stub.com"
                  :secret-key "secret"
                  :public-key "public"
                  :version "v1"})

(deftest api-url-without-param-test
  (testing "returns an URL for a resource"
    (is (= (api-url stub-client "charges")
           "https://stub.com/v1/charges"))))

(deftest api-url-with-param-test
  (testing "returns an URL for a resources with a param"
    (is (= (api-url stub-client "charges" "stub-id")
           "https://stub.com/v1/charges/stub-id"))))

(deftest priv-api-req-with-body-test
  (testing "returns a private request containing a body (i.e. charge)"
    (is (= (api-req stub-client :priv {:stub :body})
           {:body "{\"stub\":\"body\"}"
            :headers {"Private-Merchant-Id" "secret"}
            :content-type :json
            :throw-exceptions false
            :as :json
            :coerce :always}))))

(deftest priv-api-req-without-body-test
  (testing "returns a private request without a body (i.e. refund)"
    (is (= (api-req stub-client :priv)
           {:headers {"Private-Merchant-Id" "secret"}
            :throw-exceptions false
            :as :json
            :coerce :always}))))

(deftest pub-api-req-with-body-test
  (testing "returns a public request containing a body (i.e. token)"
    (is (= (api-req stub-client :pub {:stub :body})
           {:body "{\"stub\":\"body\"}"
            :headers {"Public-Merchant-Id" "public"}
            :content-type :json
            :throw-exceptions false
            :as :json
            :coerce :always}))))

(deftest pub-api-req-without-body-test
  (testing "returns a public request without a body"
    (is (= (api-req stub-client :pub)
           {:headers {"Public-Merchant-Id" "public"}
            :throw-exceptions false
            :as :json
            :coerce :always}))))

