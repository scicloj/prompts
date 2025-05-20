# Creating Datasets with Tablecloth

## Purpose
This prompt helps you create datasets from various data sources using Tablecloth, a Clojure library for dataset manipulation.

## Setup
```clojure
(require '[tablecloth.api :as tc])
```

## Creating Datasets

### Empty Dataset
```clojure
(tc/dataset)
```

### From Column Map
Create a dataset from a map where keys are column names and values are sequences:
```clojure
(tc/dataset {:A [1 2 3] 
             :B ["X" "Y" "Z"]})
```

### From Row Maps
Create a dataset from a sequence of maps (each map represents a row):
```clojure
(tc/dataset [{:a 1 :b 3} 
             {:b 2 :a 99}])
```

### With Missing Values
Missing values are automatically handled:
```clojure
(tc/dataset [{:a nil :b 1} 
             {:a 3 :b 4} 
             {:a 11}])
```

### Using let-dataset Macro
Create columns based on expressions:
```clojure
(require '[tech.v3.datatype.functional :as dfn])

(tc/let-dataset [x (range 1 6)
                 y 1
                 z (dfn/+ x y)])
```

### From Files
```clojure
;; From CSV
(tc/dataset "data.csv")

;; With options
(tc/dataset "data.csv" {:key-fn keyword 
                        :parser-fn {:col1 :int64
                                    :col2 :float64}})
```

## Tips
- Column names can be strings, keywords, or symbols
- Missing values are represented as nil
- Use the :parser-fn option to specify column types when loading from files