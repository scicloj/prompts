# Geo-Spatial Visualization with Tableplot

## Purpose
This prompt focuses on creating geo-spatial visualizations using Tableplot's Plotly API to represent data on maps.

## Setup
```clojure
(require '[scicloj.tableplot.v1.plotly :as plotly])
(require '[scicloj.tablecloth.api :as tc])
```

## Basic Map Visualizations

### Scatter Points on Maps

```clojure
;; Sample geo data with latitude and longitude
(def city-data
  (tc/dataset {:city ["New York" "Los Angeles" "Chicago" "Houston" "Phoenix"]
               :lat [40.7128 34.0522 41.8781 29.7604 33.4484]
               :lon [-74.0060 -118.2437 -87.6298 -95.3698 -112.0740]
               :population [8804190 3898747 2746388 2304580 1608139]
               :region ["Northeast" "West" "Midwest" "South" "West"]}))

;; Basic geo scatter plot
(-> city-data
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showland true
                                 :landcolor "rgb(240, 240, 240)"
                                 :countrycolor "rgb(200, 200, 200)"}}})
    (plotly/layer-geo-scatter {:=lat :lat 
                              :=lon :lon 
                              :=text :city
                              :=mark-size 12})
    (plotly/plot))

;; Sized and colored scatter points
(-> city-data
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showland true}
                           :title "US Cities by Population"}})
    (plotly/layer-geo-scatter {:=lat :lat 
                              :=lon :lon 
                              :=text :city
                              :=mark-size :population
                              :=mark-size-range [5 25]
                              :=color :region
                              :=hover-template "%{text}<br>Pop: %{marker.size:,}<br>Region: %{marker.color}"})
    (plotly/plot))
```

### Bubble Maps with Custom Hover Info

```clojure
;; Create more detailed hover info
(-> city-data
    (tc/add-column :hover-text #(str (:city %) "<br>"
                                   "Population: " (:population %) "<br>"
                                   "Region: " (:region %)))
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showcoastlines true
                                 :coastlinecolor "rgb(120, 120, 120)"
                                 :showland true
                                 :landcolor "rgb(250, 250, 250)"
                                 :showocean true
                                 :oceancolor "rgb(230, 240, 255)"}
                           :title "US Cities"}})
    (plotly/layer-geo-scatter {:=lat :lat 
                              :=lon :lon
                              :=text :hover-text
                              :=mark-size :population
                              :=mark-size-range [10 50]
                              :=mark-opacity 0.7
                              :=color :region
                              :=hover-info "text"})
    (plotly/plot))
```

### World Map with Different Projections

```clojure
;; World cities
(def world-cities
  (tc/dataset {:city ["New York" "London" "Tokyo" "Sydney" "Rio de Janeiro" "Cape Town"]
               :lat [40.7128 51.5074 35.6762 -33.8688 -22.9068 -33.9249]
               :lon [-74.0060 -0.1278 139.6503 151.2093 -43.1729 18.4241]
               :population [8804190 8982000 13960000 5367800 6747815 4618000]
               :continent ["North America" "Europe" "Asia" "Australia" "South America" "Africa"]}))

;; Mercator projection (standard)
(-> world-cities
    (plotly/base {:=layout {:geo {:projection {:type "mercator"}
                                 :showland true
                                 :showcountries true
                                 :countrycolor "rgb(200, 200, 200)"}
                           :title "World Cities (Mercator Projection)"}})
    (plotly/layer-geo-scatter {:=lat :lat 
                              :=lon :lon 
                              :=text :city
                              :=mark-size 10
                              :=color :continent})
    (plotly/plot))

;; Natural Earth projection
(-> world-cities
    (plotly/base {:=layout {:geo {:projection {:type "natural earth"}
                                 :showland true
                                 :showcountries true}
                           :title "World Cities (Natural Earth Projection)"}})
    (plotly/layer-geo-scatter {:=lat :lat 
                              :=lon :lon 
                              :=text :city
                              :=mark-size 10
                              :=color :continent})
    (plotly/plot))

;; Orthographic projection (globe)
(-> world-cities
    (plotly/base {:=layout {:geo {:projection {:type "orthographic"}
                                 :showland true
                                 :showcountries true
                                 :showocean true
                                 :oceancolor "rgb(230, 240, 255)"}
                           :title "World Cities (Orthographic Projection)"}})
    (plotly/layer-geo-scatter {:=lat :lat 
                              :=lon :lon 
                              :=text :city
                              :=mark-size 10
                              :=color :continent})
    (plotly/plot))
```

## Choropleth Maps

### US States Choropleth

```clojure
;; US states data
(def state-data
  (tc/dataset {:state ["AL" "AK" "AZ" "AR" "CA" "CO" "CT" "DE" "FL" "GA" 
                      "HI" "ID" "IL" "IN" "IA" "KS" "KY" "LA" "ME" "MD"]
               :value [42 38 25 49 26 39 52 13 40 30 
                      20 47 44 32 45 31 38 29 28 48]}))

;; US states choropleth map
(-> state-data
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showlakes true
                                 :lakecolor "rgb(255, 255, 255)"}
                           :title "US States Choropleth"}})
    (plotly/layer-choropleth {:=locations :state
                             :=z :value
                             :=locationmode "USA-states"
                             :=color-scale "Viridis"
                             :=marker-line-width 0.5
                             :=marker-line-color "rgb(255, 255, 255)"})
    (plotly/plot))
```

### World Countries Choropleth

```clojure
;; Country data with ISO codes
(def country-data
  (tc/dataset {:country ["USA" "GBR" "CAN" "DEU" "AUS" "IND" "CHN" "BRA" "RUS" "ZAF"]
               :code ["USA" "GBR" "CAN" "DEU" "AUS" "IND" "CHN" "BRA" "RUS" "ZAF"]
               :value [50 43 29 62 24 10 80 31 69 17]}))

;; World choropleth
(-> country-data
    (plotly/base {:=layout {:geo {:showframe false
                                 :showcoastlines true
                                 :projection {:type "natural earth"}}
                           :title "World Choropleth Map"}})
    (plotly/layer-choropleth {:=locations :code 
                             :=z :value
                             :=text :country
                             :=locationmode "ISO-3"
                             :=color-scale "YlOrRd"
                             :=marker-line-color "rgb(150, 150, 150)"
                             :=marker-line-width 0.5})
    (plotly/plot))
```

## GeoJSON Maps

### Custom GeoJSON Regions

```clojure
;; This example requires a GeoJSON file
;; For example, with a geojson for neighborhoods:

(def neighborhoods-geojson "{...}") ;; Actual GeoJSON would go here

(def neighborhood-data
  (tc/dataset {:id ["A" "B" "C" "D" "E" "F"]
               :value [12 45 33 22 38 15]}))

;; Choropleth with custom GeoJSON
(-> neighborhood-data
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showland true}
                           :title "Neighborhood Data"}})
    (plotly/layer-choropleth {:=geojson neighborhoods-geojson
                             :=locations :id
                             :=featureidkey "properties.district"
                             :=z :value
                             :=color-scale "Viridis"})
    (plotly/plot))
```

## Map with Overlay Layers

### Combined Choropleth and Scatter

```clojure
;; Combination visualization
(-> (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showland true
                                 :landcolor "rgb(240, 240, 240)"}
                           :title "Combined State Data and Cities"}})
    ;; Add the choropleth layer first (background)
    (plotly/layer-choropleth {:=locations (:state state-data)
                             :=z (:value state-data)
                             :=locationmode "USA-states"
                             :=color-scale "Blues"
                             :=marker-line-width 0.5
                             :=marker-line-color "rgb(255, 255, 255)"})
    ;; Add the scatter points on top
    (plotly/layer-geo-scatter {:=lat (:lat city-data)
                              :=lon (:lon city-data)
                              :=text (:city city-data)
                              :=mark-size (:population city-data)
                              :=mark-size-range [5 25]
                              :=mark-opacity 0.7
                              :=color (:region city-data)})
    (plotly/plot))
```

## Animated Maps

### Time Series on Maps

```clojure
;; Time series geo data
(def geo-time-data
  (let [cities ["New York" "Los Angeles" "Chicago" "Houston"]
        years (range 2010 2021)
        expand-city (fn [city] 
                      (repeat (count years) city))
        expand-years (fn [_] 
                       years)
        gen-value (fn [city year]
                    (case city
                      "New York" (+ 100 (* 5 (- year 2010)) (* 10 (rand)))
                      "Los Angeles" (+ 80 (* 3 (- year 2010)) (* 8 (rand)))
                      "Chicago" (+ 60 (* 2 (- year 2010)) (* 6 (rand)))
                      "Houston" (+ 70 (* 4 (- year 2010)) (* 7 (rand)))))]
    (tc/dataset {:city (mapcat expand-city cities)
                 :year (mapcat expand-years cities)
                 :lat (mapcat (fn [city] 
                                (repeat (count years) 
                                       (case city
                                         "New York" 40.7128
                                         "Los Angeles" 34.0522
                                         "Chicago" 41.8781
                                         "Houston" 29.7604)))
                             cities)
                 :lon (mapcat (fn [city] 
                                (repeat (count years) 
                                       (case city
                                         "New York" -74.0060
                                         "Los Angeles" -118.2437
                                         "Chicago" -87.6298
                                         "Houston" -95.3698)))
                             cities)
                 :value (for [city (mapcat expand-city cities)
                             year (mapcat expand-years cities)]
                          (gen-value city year))})))

;; Animated geo scatter
(-> geo-time-data
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :showland true}
                           :title "Value Changes by City (2010-2020)"
                           :updatemenus [{:type "buttons"
                                         :showactive true
                                         :buttons [{:label "Play"
                                                   :method "animate"
                                                   :args [nil {:frame {:duration 500 :redraw true}
                                                              :fromcurrent true}]}]}]}})
    (plotly/layer-geo-scatter {:=lat :lat
                              :=lon :lon
                              :=text :city
                              :=mark-size :value
                              :=mark-size-range [5 30]
                              :=color :city
                              :=animation-frame :year})
    (plotly/plot))
```

## Advanced Map Techniques

### Map with Multiple Traces and Custom Style

```clojure
;; Styled map with multiple data layers
(-> (plotly/base {:=layout {:geo {:scope "north america"
                                 :projection {:type "albers usa"}
                                 :showland true
                                 :landcolor "rgb(240, 240, 240)"
                                 :countrycolor "rgb(150, 150, 150)"
                                 :coastlinecolor "rgb(120, 120, 120)"
                                 :showlakes true
                                 :lakecolor "rgb(220, 240, 255)"
                                 :showrivers true
                                 :rivercolor "rgb(200, 230, 255)"}
                           :title "US Data Visualization"
                           :paper_bgcolor "rgb(250, 250, 250)"
                           :plot_bgcolor "rgb(250, 250, 250)"
                           :font {:family "Arial" :size 12 :color "rgb(60, 60, 60)"}}})
    ;; Base choropleth layer
    (plotly/layer-choropleth {:=locations (:state state-data)
                             :=z (:value state-data)
                             :=locationmode "USA-states"
                             :=color-scale "Blues"
                             :=marker-line-width 0.5
                             :=marker-line-color "rgb(255, 255, 255)"
                             :=name "State Values"
                             :=hover-template "<b>%{location}</b><br>Value: %{z}<extra></extra>"})
    ;; Major cities as larger points
    (plotly/layer-geo-scatter {:=lat (:lat city-data)
                              :=lon (:lon city-data)
                              :=text (:city city-data)
                              :=mark-size (:population city-data)
                              :=mark-size-range [10 40]
                              :=mark-opacity 0.7
                              :=mark-color "rgba(220, 50, 50, 0.8)"
                              :=name "Major Cities"
                              :=hover-template "<b>%{text}</b><br>Population: %{marker.size:,}<extra></extra>"})
    (plotly/plot))
```

### Heatmap on a Map (Density Map)

```clojure
;; Generate random points for a density map
(def random-points
  (let [n 1000
        base-lat 40.7
        base-lon -74.0
        spread 0.1]
    (tc/dataset {:lat (map #(+ base-lat (* spread (- (rand) 0.5))) (range n))
                 :lon (map #(+ base-lon (* spread (- (rand) 0.5))) (range n))})))

;; Density heatmap
(-> random-points
    (plotly/base {:=layout {:geo {:scope "usa"
                                 :projection {:type "albers usa"}
                                 :center {:lat 40.7 :lon -74.0}
                                 :lonaxis {:range [-74.1 -73.9]}
                                 :lataxis {:range [40.6 40.8]}
                                 :showland true}
                           :title "Point Density Heatmap"}})
    (plotly/layer-geo-density {:=lat :lat
                              :=lon :lon
                              :=radius 10
                              :=color-scale "Hot"})
    (plotly/plot))
```

## Tips for Geo-Spatial Visualization
- Choose the appropriate map projection for your data context
- Use color scales that make sense for your data (sequential, diverging, etc.)
- Include proper hover information for interactive exploration
- Consider the visual hierarchy when combining multiple data layers
- For choropleth maps, ensure your location IDs match the expected format
- For world maps, use ISO-3 country codes for best compatibility
- When working with custom regions, provide proper GeoJSON with matching IDs
- Use animation frames to show changes over time or different scenarios
- Adjust zoom level and center coordinates to focus on regions of interest