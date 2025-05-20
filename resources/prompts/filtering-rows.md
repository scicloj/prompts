# Filtering and Selecting Rows with Tablecloth

## Purpose
This prompt demonstrates how to filter and select rows from datasets using Tablecloth.

## Setup
```clojure
(require '[tablecloth.api :as tc])

;; Sample dataset for examples
(def DS (tc/dataset {:V1 [1 2 3 4 5]
                     :V2 [10 20 30 40 50]
                     :V3 [0.1 0.2 0.3 0.4 0.5]
                     :V4 ["A" "B" "C" "D" "E"]}))
```

## Selecting Rows

### By Index
Select a single row by index:
```clojure
(tc/select-rows DS 4)
```

Select multiple rows by indices:
```clojure
(tc/select-rows DS [1 4 5])
```

### Using Boolean Mask
Select rows using a boolean sequence (true = keep, nil/false = drop):
```clojure
(tc/select-rows DS [true nil nil true])
```

### Using Predicates
Select rows with a predicate function (applied to each row):
```clojure
;; Rows where V3 column value is less than 1
(tc/select-rows DS (comp #(< % 1) :V3))

;; Multiple conditions
(tc/select-rows DS #(and (> (% :V1) 2) 
                         (< (% :V3) 0.5)))
```

## Filtering Rows

### Dropping Rows
Remove rows by index:
```clojure
(tc/drop-rows DS 0)
(tc/drop-rows DS [0 2 4])
```

### Filtering Missing Values
Drop rows with any missing values:
```clojure
(tc/drop-missing DS)
```

Drop rows with missing values in specific columns:
```clojure
(tc/drop-missing DS [:V1 :V2])
```

## Working with Grouped Data
Select from grouped datasets:
```clojure
;; Select first group and ungroup
(->> DS
     (tc/group-by :V1)
     (tc/select-rows 0)
     (tc/ungroup))
```

## Tips
- Row indices are 0-based
- Use comp with column keywords for concise selection
- Chaining operations with -> or ->> allows for expressive data manipulations