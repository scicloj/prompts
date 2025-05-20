# Time Series Visualization with Tableplot

## Purpose
This prompt focuses specifically on visualizing time series data using Tableplot's Plotly API.

## Setup
```clojure
(require '[scicloj.tableplot.v1.plotly :as plotly])
(require '[scicloj.tablecloth.api :as tc])
(require '[java-time :as jt])
```

## Basic Time Series Plot

```clojure
;; Sample time series data
(def time-data (tc/dataset {:date (map #(jt/local-date 2025 1 %) (range 1 31))
                            :value (map #(+ 100 (* 10 (Math/sin (/ % 3.0)))) (range 30))
                            :category (repeatedly 30 #(rand-nth ["A" "B" "C"]))}))

;; Basic time series line plot
(-> time-data
    (plotly/base {:=layout {:xaxis {:type "date"}}})
    (plotly/layer-line {:=x :date :=y :value})
    (plotly/plot))
```

## Time Series with Date Range Selector

```clojure
(-> time-data
    (plotly/base {:=layout {:xaxis {:type "date"
                                    :rangeselector {:buttons [
                                                    {:count 7 :label "1w" :step "day" :stepmode "backward"}
                                                    {:count 14 :label "2w" :step "day" :stepmode "backward"}
                                                    {:step "all"}]}}
                           :title "Time Series with Range Selector"}})
    (plotly/layer-line {:=x :date :=y :value})
    (plotly/plot))
```

## Time Series with Range Slider

```clojure
(-> time-data
    (plotly/base {:=layout {:xaxis {:type "date"
                                    :rangeslider {:visible true}}
                           :title "Time Series with Range Slider"}})
    (plotly/layer-line {:=x :date :=y :value})
    (plotly/plot))
```

## Multiple Time Series by Category

```clojure
(-> time-data
    (plotly/base {:=layout {:xaxis {:type "date"}}})
    (plotly/layer-line {:=x :date :=y :value :=color :category})
    (plotly/plot))
```

## Filled Time Series (Area Chart)

```clojure
(-> time-data
    (plotly/base {:=layout {:xaxis {:type "date"}}})
    (plotly/layer-area {:=x :date :=y :value :=color :category})
    (plotly/plot))
```

## Candlestick Chart for Financial Data

```clojure
;; Sample financial data
(def financial-data 
  (tc/dataset {:date (map #(jt/local-date 2025 1 %) (range 1 31))
               :open (repeatedly 30 #(+ 100 (rand 10)))
               :high (repeatedly 30 #(+ 105 (rand 10)))
               :low (repeatedly 30 #(+ 95 (rand 10)))
               :close (repeatedly 30 #(+ 100 (rand 10)))}))

;; Candlestick chart
(-> financial-data
    (plotly/base {:=layout {:xaxis {:type "date"}
                           :title "Stock Price"}})
    (plotly/layer-candlestick {:=x :date
                              :=open :open
                              :=high :high
                              :=low :low
                              :=close :close})
    (plotly/plot))
```

## Time Series with Annotations

```clojure
;; Define important events
(def events [{:date (jt/local-date 2025 1 5) :event "Product Launch"}
            {:date (jt/local-date 2025 1 15) :event "Quarterly Report"}
            {:date (jt/local-date 2025 1 25) :event "Conference"}])

;; Create annotations for the events
(def annotations
  (mapv (fn [{:keys [date event]}]
          (let [y-val (+ 100 (* 10 (Math/sin (/ (.getDayOfMonth date) 3.0))))]
            {:x date
             :y y-val
             :xref "x"
             :yref "y"
             :text event
             :showarrow true
             :arrowhead 2
             :arrowsize 1
             :arrowwidth 1
             :ax 0
             :ay -40}))
        events))

;; Plot with annotations
(-> time-data
    (plotly/base {:=layout {:xaxis {:type "date"}
                           :annotations annotations
                           :title "Time Series with Event Annotations"}})
    (plotly/layer-line {:=x :date :=y :value})
    (plotly/plot))
```

## Time Series Heatmap Calendar

```clojure
;; Generate a year of daily data
(def calendar-data
  (let [dates (map #(jt/local-date 2025 (inc (quot % 31)) (inc (rem % 31))) (range 365))
        values (map #(+ 50 (* 30 (Math/sin (/ % 30.0)))) (range 365))]
    (tc/dataset {:date dates
                 :value values
                 :day (map #(.getDayOfMonth %) dates)
                 :month (map #(.getMonthValue %) dates)
                 :weekday (map #(.getValue (.getDayOfWeek %)) dates)})))

;; Create a calendar heatmap
(-> calendar-data
    (plotly/base {:=layout {:title "Calendar Heatmap"}})
    (plotly/layer-heatmap {:=x :day
                          :=y :month
                          :=z :value
                          :=color-scale "Viridis"})
    (plotly/plot))
```

## Time Series Decomposition

```clojure
;; Generate time series with trend, seasonality, and noise
(def decomp-data
  (let [dates (map #(jt/local-date 2025 (inc (quot % 30)) (inc (rem % 30))) (range 365))
        trend (map #(+ 100 (* 0.1 %)) (range 365))
        seasonality (map #(* 15 (Math/sin (/ % 30.0))) (range 365))
        noise (repeatedly 365 #(* 5 (- (rand) 0.5)))
        values (map + trend seasonality noise)]
    (tc/dataset {:date dates
                 :value values
                 :trend trend
                 :seasonality seasonality
                 :noise noise})))

;; Plot the decomposition
(-> decomp-data
    (plotly/base {:=layout {:title "Time Series Decomposition"
                           :grid {:rows 4 :columns 1 :pattern "independent"}
                           :height 800}})
    (plotly/layer-line {:=x :date :=y :value :=name "Original" :=subplot 1})
    (plotly/layer-line {:=x :date :=y :trend :=name "Trend" :=subplot 2})
    (plotly/layer-line {:=x :date :=y :seasonality :=name "Seasonality" :=subplot 3})
    (plotly/layer-line {:=x :date :=y :noise :=name "Residual" :=subplot 4})
    (plotly/plot))
```

## Multiple Time Series with Synchronized Axes

```clojure
;; Multiple metrics
(def multi-metric-data
  (tc/dataset {:date (map #(jt/local-date 2025 1 %) (range 1 31))
               :revenue (map #(+ 1000 (* 100 (Math/sin (/ % 5.0)))) (range 30))
               :customers (map #(+ 500 (* 50 (Math/cos (/ % 7.0)))) (range 30))
               :profit (map #(+ 300 (* 30 (Math/sin (/ % 3.0)))) (range 30))}))

;; Synchronized subplots
(-> multi-metric-data
    (plotly/base {:=layout {:title "Business Metrics"
                           :grid {:rows 3 :columns 1 :pattern "independent" :roworder "top to bottom"}
                           :height 800
                           :xaxis {:type "date"}
                           :xaxis2 {:type "date"}
                           :xaxis3 {:type "date"}}})
    (plotly/layer-line {:=x :date :=y :revenue :=name "Revenue" :=subplot 1})
    (plotly/layer-line {:=x :date :=y :customers :=name "Customers" :=subplot 2})
    (plotly/layer-line {:=x :date :=y :profit :=name "Profit" :=subplot 3})
    (plotly/plot))
```

## Time Series Forecasting Visualization

```clojure
;; Historical and forecast data
(def forecast-data
  (let [dates (concat 
               (map #(jt/local-date 2025 1 %) (range 1 31))
               (map #(jt/local-date 2025 2 %) (range 1 15)))
        historical (take 30 dates)
        forecast (drop 30 dates)
        hist-values (map #(+ 100 (* 10 (Math/sin (/ % 7.0)))) (range 30))
        forecast-values (map #(+ 100 (* 10 (Math/sin (/ (+ % 30) 7.0)))) (range 14))
        confidence-low (map #(- % 15) forecast-values)
        confidence-high (map #(+ % 15) forecast-values)]
    (tc/dataset {:date (concat historical forecast)
                 :value (concat hist-values (repeat 14 nil))
                 :forecast (concat (repeat 30 nil) forecast-values)
                 :conf-low (concat (repeat 30 nil) confidence-low)
                 :conf-high (concat (repeat 30 nil) confidence-high)
                 :is-forecast (concat (repeat 30 false) (repeat 14 true))})))

;; Visualize forecast with confidence interval
(-> forecast-data
    (plotly/base {:=layout {:title "Time Series Forecast"
                           :xaxis {:type "date"}
                           :shapes [{:type "rect"
                                    :xref "x"
                                    :yref "paper"
                                    :x0 (jt/local-date 2025 1 31)
                                    :y0 0
                                    :x1 (jt/local-date 2025 2 15)
                                    :y1 1
                                    :fillcolor "#f8f9fa"
                                    :opacity 0.3
                                    :line {:width 0}}]}})
    (plotly/layer-line {:=x :date :=y :value :=name "Historical" :=mark-width 2})
    (plotly/layer-line {:=x :date :=y :forecast :=name "Forecast" :=mark-width 2 :=mark-dash "dash"})
    (plotly/layer-ribbon {:=x :date :=y-min :conf-low :=y-max :conf-high 
                          :=name "Confidence Interval" :=mark-opacity 0.2})
    (plotly/plot))
```

## Tips for Time Series Visualization
- Always specify `{:xaxis {:type "date"}}` in the layout for proper date handling
- Use range selectors and sliders for interactive exploration of long time series
- Consider synchronized subplots for multiple related time series
- Add annotations to highlight important events
- Use different chart types based on the data pattern:
  - Line charts for general trends
  - Area charts for cumulative values
  - Candlestick charts for OHLC financial data
- For forecasting visualization, clearly distinguish historical vs. predicted data
- Use confidence intervals to show prediction uncertainty