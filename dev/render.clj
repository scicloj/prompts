(ns render
  (:require [scicloj.clay.v2.api :as clay]))

(clay/make! {:base-target-path "docs"
             :format [:quarto :html]
             :source-path "notebooks/explore.clj"})
