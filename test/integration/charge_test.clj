(ns integration.charge-test
  (:require [clojure.test :refer :all]
            [kushki-clojure.core :refer :all]
            [environ.core :refer [env]]))

(def ec-merchant-id "10000001641080185390111217")
(def ec-secret-merchant-id "10000001641088709280111217")
(def ec-test-client (client :test ec-secret-merchant-id ec-merchant-id))

(def co-merchant-id "10000001958318993042555001")
(def co-secret-merchant-id "10000001958363505343555001")
(def co-test-client (client :test co-secret-merchant-id co-merchant-id))

(defn test-token
  []
  {:card {:name "Patricio Moreano"
          :number "5142054428834241"
          :expiryMonth "05"
          :expiryYear "21"
          :cvv "045"}
   :totalAmount 11.20
   :currency "USD"
   :isDeferred false})

(defn test-charge
  [token]
  {:token token
   :amount {:subtotalIva 10.00
            :subtotalIva0 0.00
            :ice 0.00
            :iva 1.20
            :currency "USD"}
   :months 0})

(defn test-charge-with-metadata
  [token]
  {:token token
   :amount {:subtotalIva 10.00
            :subtotalIva0 0.00
            :ice 0.00
            :iva 1.20
            :currency "USD"}
   :months 0
   :metadata {:invoice-number "001-002-000000001"}})

(defn test-deferred-charge
  [token]
  {:token token
   :amount {:subtotalIva 10.00
            :subtotalIva0 0.00
            :ice 0.00
            :iva 1.20
            :currency "USD"}
   :months 12})

(defn test-deferred-charge-with-metadata
  [token]
  {:token token
   :amount {:subtotalIva 10.00
            :subtotalIva0 0.00
            :ice 0.00
            :iva 1.20
            :currency "USD"}
   :months 12
   :metadata {:invoice-number "001-002-000000001"}})

(deftest create-token-test
  (testing "create a token"
    (is (= true
           (let [resp (create-token ec-test-client (test-token))]
             (contains? resp :token))))))

(deftest create-charge-test
  (testing "create a successful charge"
    (let [token (:token
                 (create-token ec-test-client (test-token)))
          resp (create-charge
                ec-test-client
                (test-charge token))]
      (is (contains? resp :ticketNumber)))))

(deftest create-charge-with-metadata-test
  (testing "create a successful charge"
    (let [token (:token (create-token ec-test-client (test-token)))
          resp (create-charge ec-test-client (test-charge-with-metadata token))]
      (is (contains? resp :ticketNumber)))))

(deftest create-deferred-charge-test
  (testing "create a successful deferred charge"
    (let [token (:token (create-token ec-test-client (test-token)))
          resp (create-charge ec-test-client (test-deferred-charge token))]
      (is (contains? resp :ticketNumber)))))

(deftest create-deferred-charge-with-metadata-test
  (testing "create a successful deferred charge with metadata"
    (let [token (:token (create-token ec-test-client (test-token)))
          resp (create-charge ec-test-client (test-deferred-charge-with-metadata token))]
      (is (contains? resp :ticketNumber)))))

(deftest create-invalid-commerce-charge-test
  (testing "create a charge with an invalid commerce ID"
    (let [token (:token (create-token co-test-client (test-token)))
          resp (create-charge ec-test-client (test-charge token))]
      (is (not (contains? resp :ticketNumber))))))

(deftest create-invalid-total-charge-test
  (testing "create a charge with an invalid total amount"
    (let [token (:token (create-token ec-test-client {:card {:name "Patricio Moreano"
                                                             :number "5142054428834241"
                                                             :expiryMonth "05"
                                                             :expiryYear "21"
                                                             :cvv "045"}
                                                      :totalAmount 11.20
                                                      :currency "USD"
                                                      :isDeferred false}))
          invalid-charge {:token token
                          :amount {:subtotalIva 10.00
                                   :subtotalIva0 0.00
                                   :ice 0.00
                                   :iva 1.00
                                   :currency "USD"}
                          :months 0}
          resp (create-charge ec-test-client invalid-charge)]
      (is (not (contains? resp :ticketNumber))))))

(deftest create-invalid-token-charge-test
  (testing "create a charge with an invalid token"
    (let [token "invalid"
          charge {:token token
                  :amount {:subtotalIva 10.00
                           :subtotalIva0 0.00
                           :ice 0.00
                           :iva 1.00
                           :currency "USD"}
                  :months 0}
          resp (create-charge ec-test-client charge)]
      (is (not (contains? resp :ticketNumber))))))

(deftest void-charge-test
  (testing "void a charge"
    (let [token (:token (create-token ec-test-client {:card {:name "Patricio Moreano"
                                                             :number "5142054428834241"
                                                             :expiryMonth "05"
                                                             :expiryYear "21"
                                                             :cvv "045"}
                                                      :totalAmount 11.20
                                                      :currency "USD"
                                                      :isDeferred false}))
          charge {:token token
                  :amount {:subtotalIva 10.00
                           :subtotalIva0 0.00
                           :ice 0.00
                           :iva 1.20
                           :currency "USD"}
                  :months 0}
          charge-resp (create-charge ec-test-client charge)
          void-resp (void-charge
                     ec-test-client
                     (:ticketNumber charge-resp)
                     (dissoc charge :token))]
      (is (contains? charge-resp :ticketNumber))
      (is (contains? void-resp :ticketNumber)))))

;;TODO this test times out against the UAT environment
;; (deftest refund-charge-test
;;   (testing "refund a charge"
;;     (let [token (:token (create-token ec-test-client {:card {:name "Patricio Moreano"
;;                                                              :number "5142054428834241"
;;                                                              :expiryMonth "05"
;;                                                              :expiryYear "21"
;;                                                              :cvv "045"}
;;                                                       :totalAmount 11.20
;;                                                       :currency "USD"
;;                                                       :isDeferred false}))
;;           charge {:token token
;;                   :amount {:subtotalIva 10.00
;;                            :subtotalIva0 0.00
;;                            :ice 0.00
;;                            :iva 1.20
;;                            :currency "USD"}
;;                   :months 0}
;;           charge-resp (create-charge ec-test-client charge)
;;           refund-resp (refund-charge
;;                        ec-test-client
;;                        (:ticketNumber charge-resp))
;;           _ (println refund-resp)]
;;       (is (contains? charge-resp :ticketNumber))
;;       (is (= (:code refund-resp) "K000")))))
