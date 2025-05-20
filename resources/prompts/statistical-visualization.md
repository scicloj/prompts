# Statistical Visualization with Tableplot

## Purpose
This prompt focuses on creating statistical visualizations for data analysis and exploration using Tableplot's Plotly API.

## Setup
```clojure
(require '[scicloj.tableplot.v1.plotly :as plotly])
(require '[scicloj.tablecloth.api :as tc])
(require '[tech.v3.datatype.functional :as dfn])
```

## Distribution Visualization

### Histograms

```clojure
;; Sample data
(def sample-data (tc/dataset {:values (concat 
                                       (repeatedly 100 #(+ 5 (* 2 (rand))))
                                       (repeatedly 50 #(+ 15 (* 3 (rand)))))
                              :group (concat (repeat 100 "A") (repeat 50 "B"))}))

;; Basic histogram
(-> sample-data
    (plotly/base)
    (plotly/layer-histogram {:=x :values})
    (plotly/plot))

;; Histogram with custom bins
(-> sample-data
    (plotly/base)
    (plotly/layer-histogram {:=x :values 
                             :=nbins 15
                             :=histnorm "probability"})
    (plotly/plot))

;; Multiple histograms by group
(-> sample-data
    (plotly/base)
    (plotly/layer-histogram {:=x :values 
                             :=color :group
                             :=barmode "overlay"
                             :=mark-opacity 0.7})
    (plotly/plot))
```

### Box Plots

```clojure
;; Basic box plot
(-> sample-data
    (plotly/base)
    (plotly/layer-boxplot {:=y :values})
    (plotly/plot))

;; Box plot by group
(-> sample-data
    (plotly/base)
    (plotly/layer-boxplot {:=x :group :=y :values})
    (plotly/plot))

;; Horizontal box plot with points
(-> sample-data
    (plotly/base)
    (plotly/layer-boxplot {:=x :values 
                           :=y :group
                           :=boxpoints "all"
                           :=jitter 0.3
                           :=pointpos 0})
    (plotly/plot))
```

### Violin Plots

```clojure
;; Basic violin plot
(-> sample-data
    (plotly/base)
    (plotly/layer-violin {:=x :group :=y :values})
    (plotly/plot))

;; Violin with box plot inside
(-> sample-data
    (plotly/base)
    (plotly/layer-violin {:=x :group 
                          :=y :values
                          :=box {:visible true}
                          :=meanline {:visible true}
                          :=points "all"})
    (plotly/plot))

;; Split violin plot by adding another violin in opposite direction
(-> sample-data
    (tc/group-by :group)
    (plotly/base)
    (plotly/layer-violin {:=x "A"
                          :=y :values
                          :=side "negative"
                          :=color "#4C72B0"})
    (plotly/layer-violin {:=x "B"
                          :=y :values
                          :=side "positive"
                          :=color "#55A868"})
    (plotly/plot))
```

## Correlation and Relationship Visualization

### Scatter Plot with Regression Line

```clojure
;; Create correlated data
(def corr-data 
  (let [x (repeatedly 100 #(* 10 (rand)))
        noise (repeatedly 100 #(* 2 (- (rand) 0.5)))
        y (map #(+ (* 0.5 %) 2 %2) x noise)]
    (tc/dataset {:x x :y y})))

;; Scatter with regression line
(-> corr-data
    (plotly/base)
    (plotly/layer-point {:=x :x :=y :y})
    (plotly/layer-smooth {:=method :lm})
    (plotly/plot))

;; With confidence interval
(-> corr-data
    (plotly/base)
    (plotly/layer-point {:=x :x :=y :y})
    (plotly/layer-smooth {:=method :lm
                          :=confidence-interval 0.95})
    (plotly/plot))

;; Multiple regression lines by group
(-> (tc/dataset {:x (repeatedly 200 #(* 10 (rand)))
                 :y (repeatedly 200 #(* 10 (rand)))
                 :group (concat (repeat 100 "A") (repeat 100 "B"))})
    (plotly/base)
    (plotly/layer-point {:=x :x :=y :y :=color :group})
    (plotly/layer-smooth {:=method :lm :=color :group})
    (plotly/plot))
```

### Correlation Matrix

```clojure
;; Create multivariate data
(def multi-data
  (let [n 100
        x1 (repeatedly n #(* 10 (rand)))
        x2 (map #(+ (* 0.7 %) (* 3 (rand))) x1)
        x3 (map #(- (* -0.3 %) (* 2 (rand))) x1)
        x4 (repeatedly n #(* 10 (rand)))]
    (tc/dataset {:x1 x1 :x2 x2 :x3 x3 :x4 x4})))

;; Calculate correlation matrix
(defn correlation-matrix [dataset]
  (let [columns (tc/column-names dataset)
        n-cols (count columns)
        corr-fn (fn [col1 col2]
                  (dfn/correlation (dataset col1) (dataset col2)))]
    (tc/dataset
     {:x columns
      :y columns
      :z (vec (for [col1 columns
                    col2 columns]
                (corr-fn col1 col2)))})))

;; Visualize correlation matrix as heatmap
(-> (correlation-matrix multi-data)
    (plotly/base {:=layout {:title "Correlation Matrix"
                           :width 500
                           :height 500}})
    (plotly/layer-heatmap {:=x :x
                          :=y :y
                          :=z :z
                          :=color-scale "RdBu_r"
                          :=zmin -1
                          :=zmax 1
                          :=text-template ".2f"})
    (plotly/plot))
```

### Scatter Plot Matrix (SPLOM)

```clojure
;; Create a scatter plot matrix
(-> multi-data
    (plotly/base {:=layout {:title "Scatter Plot Matrix"}})
    (plotly/layer-splom {:=dimensions [{:label "X1" :values :x1}
                                      {:label "X2" :values :x2}
                                      {:label "X3" :values :x3}
                                      {:label "X4" :values :x4}]
                         :=mark-size 5
                         :=mark-opacity 0.6})
    (plotly/plot))
```

## Statistical Summary Visualizations

### Residual Plots

```clojure
;; Create model data
(def model-data
  (let [x (repeatedly 100 #(* 10 (rand)))
        y-pred (map #(+ 2 (* 0.5 %)) x)
        residuals (repeatedly 100 #(* 2 (- (rand) 0.5)))
        y-actual (map + y-pred residuals)]
    (tc/dataset {:x x
                 :y-actual y-actual
                 :y-pred y-pred
                 :residuals residuals})))

;; Residual plot
(-> model-data
    (plotly/base {:=layout {:title "Residual Plot"}})
    (plotly/layer-point {:=x :y-pred :=y :residuals})
    (plotly/layer-smooth {:=method :loess :=mark-dash "dash"})
    (plotly/layer-hline {:=y 0 :=mark-width 2 :=mark-dash "dash" :=mark-color "#888888"})
    (plotly/plot))

;; QQ plot for residuals
(defn qq-plot-data [values]
  (let [sorted (sort values)
        n (count sorted)
        quantiles (map #(/ (+ % 0.5) n) (range n))
        theoretical (map #(dfn/quantile-gaussian % 0 1) quantiles)]
    (tc/dataset {:observed sorted
                 :theoretical theoretical})))

(-> (qq-plot-data (:residuals model-data))
    (plotly/base {:=layout {:title "Normal Q-Q Plot"
                           :xaxis {:title "Theoretical Quantiles"}
                           :yaxis {:title "Sample Quantiles"}}})
    (plotly/layer-point {:=x :theoretical :=y :observed})
    (plotly/layer-abline {:=slope 1 :=intercept 0 :=mark-dash "dash"})
    (plotly/plot))
```

### Confidence Intervals and Error Bars

```clojure
;; Create grouped data with means and confidence intervals
(def grouped-stats
  (tc/dataset {:group ["A" "B" "C" "D"]
               :mean [25 32 15 40]
               :lower [22 28 12 35]
               :upper [28 36 18 45]}))

;; Bar chart with error bars
(-> grouped-stats
    (plotly/base {:=layout {:title "Mean Values with 95% Confidence Intervals"}})
    (plotly/layer-bar {:=x :group :=y :mean :=color :group})
    (plotly/layer-error-bars {:=x :group 
                             :=y :mean
                             :=error-y-minus #(map - (:mean %) (:lower %))
                             :=error-y-plus #(map - (:upper %) (:mean %))})
    (plotly/plot))
```

### Density Plots

```clojure
;; Kernel density estimation
(-> sample-data
    (plotly/base {:=layout {:title "Kernel Density Estimation"}})
    (plotly/layer-density {:=x :values :=color :group})
    (plotly/plot))

;; Combined histogram and density
(-> sample-data
    (plotly/base {:=layout {:title "Histogram with Density Overlay"}})
    (plotly/layer-histogram {:=x :values
                            :=histnorm "probability density"})
    (plotly/layer-density {:=x :values :=mark-color "red" :=mark-width 3})
    (plotly/plot))
```

## ANOVA and Statistical Testing Visualization

```clojure
;; Create data for multiple groups
(def anova-data
  (tc/dataset {:value (concat 
                        (repeatedly 30 #(+ 10 (* 2 (rand))))
                        (repeatedly 30 #(+ 12 (* 2 (rand))))
                        (repeatedly 30 #(+ 15 (* 2 (rand)))))
               :group (concat (repeat 30 "A") (repeat 30 "B") (repeat 30 "C"))}))

;; Box plot with individual points
(-> anova-data
    (plotly/base {:=layout {:title "One-way ANOVA Visualization"}})
    (plotly/layer-boxplot {:=x :group 
                          :=y :value
                          :=boxpoints "all"
                          :=jitter 0.3})
    (plotly/plot))

;; Mean plot with error bars
(defn group-stats [dataset group-col value-col]
  (-> dataset
      (tc/group-by group-col)
      (tc/aggregate {(str value-col "-mean") #(dfn/mean (% value-col))
                    (str value-col "-sd") #(dfn/standard-deviation (% value-col))
                    (str value-col "-n") #(count (% value-col))})
      (tc/add-column (str value-col "-se") #(dfn/sqrt (dfn// (% (str value-col "-sd"))
                                                          (% (str value-col "-n")))))
      (tc/add-column (str value-col "-ci95") #(dfn/* 1.96 (% (str value-col "-se"))))
      (tc/add-column (str value-col "-lower") #(dfn/- (% (str value-col "-mean"))
                                                    (% (str value-col "-ci95"))))
      (tc/add-column (str value-col "-upper") #(dfn/+ (% (str value-col "-mean"))
                                                    (% (str value-col "-ci95"))))))

(-> (group-stats anova-data :group :value)
    (plotly/base {:=layout {:title "Group Means with 95% Confidence Intervals"}})
    (plotly/layer-point {:=x :group :=y :value-mean :=mark-size 12})
    (plotly/layer-error-bars {:=x :group
                             :=y :value-mean
                             :=error-y-minus #(map - (:value-mean %) (:value-lower %))
                             :=error-y-plus #(map - (:value-upper %) (:value-mean %))})
    (plotly/plot))
```

## Tips for Statistical Visualization
- Use the right visualization for your statistical question:
  - Histograms and density plots for distributions
  - Box plots and violin plots for comparing groups
  - Scatter plots with smoothing for relationships
  - Bar charts with error bars for means comparison
- Add reference lines where appropriate (e.g., zero line in residual plots)
- Use confidence intervals to communicate uncertainty
- Consider multiple ways to visualize the same data for deeper insights
- Use proper statistical transformations when necessary (e.g., log scales)
- Label axes and include units for clarity