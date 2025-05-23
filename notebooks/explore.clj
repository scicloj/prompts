;; # Noj prompts example

;; ## Setup

(ns explore
  (:require
   [scicloj.kindly.v4.kind :as kind]
   [scicloj.prompts.v1.api :as prompts]
   [tablecloth.api :as tc]
   [scicloj.metamorph.ml.rdatasets :as rdatasets]))

;; ## Preparing the prompt

(->> [:creating-datasets :plot-trends :aggregate-data]
     (prompts/generate-prompt)
     (spit "CLAUDE.md"))

;; ## The prompt we prepared

(kind/hiccup
 [:div {:style {:height "400px"
                :overflow-y "scroll"
                :background-color "floralwhite"}}
  (kind/md
   (slurp "CLAUDE.md"))])

;; ## The data

(def economics
  (rdatasets/ggplot2-economics_long))

;; @claude please show a few rows of the economics dataset.

(tc/head economics 10)

;; @claude pleaes show the distinct values of `:variable` and their counts.

(-> economics
    (tc/group-by :variable)
    (tc/aggregate {:count tc/row-count}))

;; @claude please define a new dataset that only includes
;; the cases where `:variable` is `"unemploy"`.

(def unemploy-data
  (tc/select-rows economics #(= "unemploy" (:variable %))))

;; @claude please plot the time series of unemployment data.

(require '[scicloj.tableplot.v1.plotly :as plotly])

(-> unemploy-data
    (plotly/base {:=layout {:xaxis {:type "date"}
                            :title "US Unemployment Over Time"}})
    (plotly/layer-line {:=x :date :=y :value})
    (plotly/plot))

;; @claude please adapt the Plotly layout to support a few
;; time ranges: last year, last decade, and whole period.

(-> unemploy-data
    (plotly/base {:=layout {:xaxis {:type "date"
                                    :rangeselector {:buttons [
                                                              {:count 1 :label "1Y" :step "year" :stepmode "backward"}
                                                              {:count 10 :label "10Y" :step "year" :stepmode "backward"}
                                                              {:step "all" :label "All"}]}}
                            :title "US Unemployment Over Time with Range Selector"}})
    (plotly/layer-line {:=x :date :=y :value})
    (plotly/plot))

;; @claude for the whole-period series, please add a running index number
;; and a regression line of the unemployment rate by this number.
;; Do the whole thing in one `->` pipeline.

(-> unemploy-data
    (tc/add-column :index (range (tc/row-count unemploy-data)))
    (plotly/base {:=layout {:xaxis {:title "Time Index"}
                           :yaxis {:title "Unemployment Rate"}
                           :title "Unemployment Rate vs Time Index with Regression"}})
    (plotly/layer-point {:=x :index :=y :value :=name "Unemployment"})
    (plotly/layer-smooth {:=x :index :=y :value :=name "Linear Trend"})
    (plotly/plot))

