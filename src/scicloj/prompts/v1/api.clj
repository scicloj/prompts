(ns scicloj.prompts.v1.api
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(defn generate-prompt
  "Generate a prompt based on the tasks described in the tags."
  [desired-tags]
  (->> "prompts-catalog.edn"
       io/resource
       slurp
       edn/read-string
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

