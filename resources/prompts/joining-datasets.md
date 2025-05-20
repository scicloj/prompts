# Joining Datasets with Tablecloth

## Purpose
This prompt covers how to join and combine datasets using Tablecloth.

## Setup
```clojure
(require '[tablecloth.api :as tc])

;; Sample datasets for examples
(def ds1 (tc/dataset {:a [1 2 3 4]
                      :b ["A" "B" "C" "D"]
                      :c [10 20 30 40]}))

(def ds2 (tc/dataset {:e [1 2 5 6]
                      :b ["A" "B" "E" "F"] 
                      :d [100 200 300 400]}))
```

## Join Types

### Left Join
Keeps all rows from the left dataset:
```clojure
;; Join on column 'b'
(tc/left-join ds1 ds2 :b)

;; Join on multiple columns
(tc/left-join ds1 ds2 [:a :b])

;; Join with different column names
(tc/left-join ds1 ds2 {:left :a :right :e})
```

### Inner Join
Keeps only rows that match in both datasets:
```clojure
(tc/inner-join ds1 ds2 :b)
```

### Right Join
Keeps all rows from the right dataset:
```clojure
(tc/right-join ds1 ds2 :b)
```

### Full Join
Keeps all rows from both datasets:
```clojure
(tc/full-join ds1 ds2 [:a :b])
```

### Semi Join
Keeps rows from the left dataset that have a match in the right dataset:
```clojure
(tc/semi-join ds1 ds2 :b)
```

### Anti Join
Keeps rows from the left dataset that don't have a match in the right dataset:
```clojure
(tc/anti-join ds1 ds2 :b)
```

### Cross Join
Cartesian product of both datasets:
```clojure
(tc/cross-join ds1 ds2)
```

## Combining Datasets

### Row Concatenation
Append rows from one dataset to another:
```clojure
(tc/concat ds1 ds2)

;; With column mapping
(tc/concat ds1 
          (tc/rename-columns ds2 {:e :a :d :c}))
```

### Column Concatenation
Add columns from one dataset to another:
```clojure
(tc/add-columns ds1 (tc/select-columns ds2 [:e :d]))

;; Select shared rows by index
(tc/add-columns (tc/select-rows ds1 (range 2))
                (tc/select-rows ds2 (range 2)))
```

## Tips
- Specify matching columns as a keyword, sequence, or map
- Use rename-columns before joining if column names don't match
- For concatenation, ensure compatible column types
- Missing values are filled with nil when joining