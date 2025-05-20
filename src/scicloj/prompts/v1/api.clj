(ns scicloj.prompts.v1.api
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn- load-catalog
  "Load the prompts catalog from resources"
  []
  (-> "prompts-catalog.edn"
      io/resource
      slurp
      edn/read-string))

(defn list-prompts
  "List all available prompts"
  []
  (:prompts (load-catalog)))

(defn get-prompt-metadata
  "Get metadata for a specific prompt"
  [prompt-id]
  (get-in (load-catalog) [:prompts prompt-id]))

(defn get-prompt
  "Get the content of a specific prompt"
  [prompt-id]
  (when-let [metadata (get-prompt-metadata prompt-id)]
    (-> (str "prompts/" prompt-id)
        io/resource
        slurp)))

(defn find-prompts-by-tag
  "Find prompts that have a specific tag"
  [tag]
  (->> (list-prompts)
       (filter (fn [[_ metadata]] (contains? (:tags metadata) tag)))
       (into {})))

(defn get-all-tags
  "Get all unique tags from all prompts"
  []
  (->> (list-prompts)
       (mapcat (fn [[_ metadata]] (:tags metadata)))
       (into #{})))