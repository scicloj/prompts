# Data Visualization with Tableplot's Plotly API

## Purpose
This prompt demonstrates how to create interactive data visualizations using Tableplot's Plotly API, which provides a Clojure-friendly interface to Plotly.js.

## Setup
```clojure
(require '[scicloj.tableplot.v1.plotly :as plotly])
(require '[scicloj.tablecloth.api :as tc])
(require '[scicloj.ml.dataset :as ds])

;; For interactive notebook display:
(require '[scicloj.clay.v2.api :as clay])
```

## Basic Plotting Pattern

The general pattern follows a pipeline approach:

```clojure
;; Sample dataset
(def dataset (tc/dataset {:x [1 2 3 4 5]
                          :y [10 20 15 25 30]
                          :category ["A" "B" "A" "B" "A"]}))

;; Basic plot pattern
(-> dataset
    (plotly/base)             ;; Initialize plot template
    (plotly/layer-point {:=x :x :=y :y})  ;; Add a layer
    (plotly/plot))            ;; Render the plot
```

## Plot Types and Layers

### Point Plot (Scatter Plot)
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x 
                         :=y :y
                         :=color :category
                         :=mark-size 10
                         :=mark-opacity 0.7
                         :=mark-symbol :circle}))
```

### Line Plot
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-line {:=x :x 
                        :=y :y
                        :=color :category
                        :=mark-width 2}))
```

### Bar Chart
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-bar {:=x :category 
                       :=y :y
                       :=color :category}))
```

### Boxplot
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-boxplot {:=x :category 
                           :=y :y}))
```

### Violin Plot
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-violin {:=x :category 
                          :=y :y}))
```

### Histogram
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-histogram {:=x :y}))
```

### Combining Multiple Layers
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x :=y :y :=color :category})
    (plotly/layer-smooth {:=color :category})  ;; Add smoothing line
    (plotly/plot))
```

## Customizing Visualizations

### Color Mapping

#### By Categorical Variable
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x 
                         :=y :y 
                         :=color :category}))
```

#### By Numerical Variable
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x 
                         :=y :y 
                         :=color :y  ;; Numerical variable for color gradient
                         :=color-scale :viridis}))
```

### Size Mapping
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x 
                         :=y :y 
                         :=mark-size :y  ;; Size based on y values
                         :=mark-size-range [5 20]}))  ;; Min/max size range
```

### Symbol Mapping
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x 
                         :=y :y 
                         :=mark-symbol :category}))  ;; Different symbols by category
```

### Layout Customization
```clojure
(-> dataset
    (plotly/base {:=layout {:title "My Plot"
                           :width 800
                           :height 500
                           :plot_bgcolor "#f8f9fa"
                           :paper_bgcolor "#ffffff"
                           :font {:family "Arial" :size 14}
                           :showlegend true}})
    (plotly/layer-point {:=x :x :=y :y}))
```

### Axis Configuration
```clojure
(-> dataset
    (plotly/base {:=layout {:xaxis {:title "X-Axis Title"
                                    :tickformat ".1f"
                                    :rangemode "tozero"}
                           :yaxis {:title "Y-Axis Title"
                                   :type "log"}}})
    (plotly/layer-point {:=x :x :=y :y}))
```

## Advanced Visualization Techniques

### Statistical Smoothing
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-point {:=x :x :=y :y :=color :category})
    (plotly/layer-smooth {:=method :loess  ;; options: :lm, :loess, :gam
                          :=span 0.75      ;; smoothness parameter
                          :=mark-width 2}))
```

### Faceting (Multiple Plots)
```clojure
(-> dataset
    (plotly/base {:=facet {:column :category}})  ;; Split by category in columns
    (plotly/layer-point {:=x :x :=y :y}))

;; Both row and column faceting
(-> dataset
    (plotly/base {:=facet {:row :another-column
                           :column :category}})
    (plotly/layer-point {:=x :x :=y :y}))
```

### 3D Plots
```clojure
(-> dataset-with-z-column
    (plotly/base {:=layout {:scene {:xaxis {:title "X"}
                                    :yaxis {:title "Y"}
                                    :zaxis {:title "Z"}}}})
    (plotly/layer-point {:=x :x :=y :y :=z :z :=color :category}))
```

### Geo Maps (with latitude/longitude)
```clojure
(-> dataset-with-geo-data
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showland true}}})
    (plotly/layer-point {:=lat :latitude 
                         :=lon :longitude
                         :=color :value
                         :=mark-size :population}))
```

### Heatmap
```clojure
(-> correlation-matrix-dataset
    (plotly/base)
    (plotly/layer-heatmap {:=x :column-names
                           :=y :column-names
                           :=z :correlation-values
                           :=color-scale [:blue :white :red]}))
```

## Working with Time Series

```clojure
(-> time-dataset
    (plotly/base {:=layout {:xaxis {:type "date"
                                    :rangeselector {:buttons [{:count 1 :label "1m" :step "month" :stepmode "backward"}
                                                            {:count 6 :label "6m" :step "month" :stepmode "backward"}
                                                            {:count 1 :label "1y" :step "year" :stepmode "backward"}
                                                            {:step "all"}]}
                                    :rangeslider {:visible true}}}})
    (plotly/layer-line {:=x :date :=y :value}))
```

## Specialized Techniques

### Frequency Polygons (Overlaid Histograms as Lines)
```clojure
(-> dataset
    (plotly/base)
    (plotly/layer-histogram {:=x :value 
                            :=color :category
                            :=type :overlay  ;; Overlaid histograms
                            :=histogram-type :line}))  ;; As lines instead of bars
```

### Composite Views with Multiple y-Axes
```clojure
(-> dataset
    (plotly/base {:=layout {:yaxis {:title "First Y Metric"}
                           :yaxis2 {:title "Second Y Metric"
                                    :overlaying "y"
                                    :side "right"}}})
    (plotly/layer-line {:=x :x :=y :y1})
    (plotly/layer-line {:=x :x :=y :y2 :=yaxis "y2" :=color "orange"}))
```

### Adding Annotations
```clojure
(-> dataset
    (plotly/base {:=layout {:annotations [{:x 3 :y 20 :text "Peak" :showarrow true}
                                         {:x 1 :y 10 :text "Start" :showarrow false}]}})
    (plotly/layer-line {:=x :x :=y :y}))
```

## Examples with Real-World Data

### Iris Dataset Visualization
```clojure
(def iris (tc/dataset "https://raw.githubusercontent.com/scicloj/tablecloth/main/test/data/iris.csv"))

;; Scatter plot matrix by species
(-> iris
    (plotly/base {:=layout {:height 800 :width 800}})
    (plotly/layer-point {:=x :sepal-length 
                         :=y :sepal-width
                         :=color :species 
                         :=mark-opacity 0.7})
    (plotly/plot))

;; Box plots of measurements by species
(-> iris
    (tc/pivot->longer [:sepal-length :sepal-width :petal-length :petal-width]
                      {:value-column-name :measurement
                       :key-column-name :metric})
    (plotly/base)
    (plotly/layer-boxplot {:=x :metric
                           :=y :measurement
                           :=color :species}))
```

## Tips
- Use `plotly/base` to initialize the plot and set global options
- Add layers with `plotly/layer-*` functions for different chart types
- Access columns with the `:=x`, `:=y`, `:=color`, etc. parameters
- Customize appearance with `:=mark-*` parameters
- Render the final plot with `plotly/plot`
- Layer order matters - later layers appear on top
- For interactive exploration, display in a notebook with Clay