(ns scicloj.prompts.v1.api
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(defn catalogue []
  (-> "prompts-catalog.edn"
      io/resource
      slurp
      edn/read-string))

(defn generate-prompt
  "Generate a prompt based on the tasks described in the tags."
  [desired-tags]
  (->> (catalogue)
       :prompts
       (filter (fn [[_ {:keys [tags]}]]
                 (some #(tags %) desired-tags)))
       (map (fn [[prompt-name _]]
              (->> prompt-name
                   (str "prompts/")
                   io/resource
                   slurp)))
       (str/join "\n\n")
       (str (slurp "CLAUDE-base.md") "\n\n")))

(defn all-tags []
  (->> (catalogue)
       :prompts
       vals
       (mapcat :tags)
       distinct
       sort))

