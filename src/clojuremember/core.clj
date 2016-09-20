(ns clojuremember.core
  (:require [cheshire.core :as js]
            ;[ring.adapter.jetty :as jetty]
            [clj-http.client :as hc]
            [environ.core :refer [env]]))

(def auth-url "https://www.rememberthemilk.com/services/auth/")

(def shared-secret "e2dcd53a8516e04e")
(def api-key "e0536bd6aeaa72217bf6950d113300f6")
(def frob "a8e2cd1a5e40864899a5af8f29a1ead74b99e264")
(defn sign-par
  ([m] (sign-par m shared-secret))
  ([m shared-secret]
   (let [g (str shared-secret (clojure.string/join (map #(str % (m %)) (sort (keys m)))))
         md (java.security.MessageDigest/getInstance "MD5")
         _ (.update md (.getBytes g "UTF-8") 0 (count g))]
     (.toString  (BigInteger. 1 (.digest md)) 16))))

(def m  {"yxz" "foo"  "feg" "bar" "abc" "baz"})

(sign-par m "BANANAS")

(defn query-with
  []
  (let [qmap {"api_key" api-key "perms" "read"}
        #_res #_(hc/get auth-url
                        {:query-params (assoc qmap "api_sig" (sign-par qmap))})]
    (str auth-url "?" (hc/generate-query-string (assoc qmap "api_sig" (sign-par qmap))))))

(def authtokenurl "https://api.rememberthemilk.com/services/rest/")

(defn get-auth-token
  []
  (let [qmap {"api_key" api-key "method" "rtm.auth.getToken"
              "frob" frob}
        res (hc/get authtokenurl
                    {:query-params (assoc qmap "api_sig" (sign-par qmap))})]
    res))

(def at (get-auth-token))
;;docs
;;https://www.rememberthemilk.com/services/api/request.rest.rtm
;;parse XML now to get token
