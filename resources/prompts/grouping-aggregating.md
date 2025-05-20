# Grouping and Aggregating Data with Tablecloth

## Purpose
This prompt demonstrates how to group datasets and perform aggregations using Tablecloth.

## Setup
```clojure
(require '[tablecloth.api :as tc])

;; Sample dataset for examples
(def DS (tc/dataset {:V1 [1 2 1 3 2]
                     :V2 [10 20 30 40 50]
                     :V3 [0.1 0.2 0.3 0.4 0.5]
                     :V4 ["A" "B" "A" "C" "B"]}))
```

## Grouping Data

### Group by Single Column
```clojure
(tc/group-by DS :V1)
```

### Group by Multiple Columns
```clojure
(tc/group-by DS [:V1 :V4])
```

### Access Groups
```clojure
;; Get first group
(-> DS
    (tc/group-by :V1)
    (tc/select-rows 0))

;; Get values for a specific group
(->> DS
     (tc/group-by :V1)
     (filter #(= 1 (:V1 (:name %)))))
```

### Ungrouping
```clojure
(->> DS
     (tc/group-by :V1)
     (tc/ungroup))
```

## Aggregating Data

### Simple Aggregation
```clojure
;; Sum one column
(tc/aggregate DS #(reduce + (% :V2)))
```

### Named Aggregations
```clojure
(tc/aggregate DS {:sum-of-V2 #(reduce + (% :V2))
                  :mean-of-V3 #(/ (reduce + (% :V3)) (count (% :V3)))})
```

### Group and Aggregate
```clojure
;; Group and apply single aggregation
(->> DS
     (tc/group-by :V4)
     (tc/aggregate #(reduce + (% :V2))))

;; Multiple aggregations with names
(->> DS
     (tc/group-by [:V4])
     (tc/aggregate {:sum-v1 #(reduce + (% :V1))
                    :prod-v3 #(reduce * (% :V3))
                    :count #(count (% :V1))}))
```

### Common Aggregation Functions
```clojure
(require '[tech.v3.datatype.functional :as dfn])

(->> DS
     (tc/group-by :V4)
     (tc/aggregate {:count count
                    :sum-v2 #(dfn/sum (% :V2))
                    :mean-v2 #(dfn/mean (% :V2))
                    :min-v2 #(dfn/min (% :V2))
                    :max-v2 #(dfn/max (% :V2))
                    :std-v2 #(dfn/standard-deviation (% :V2))}))
```

## Tips
- Grouped datasets retain all columns in each group
- Use tech.v3.datatype.functional for efficient numeric operations
- Combine aggregations with other operations for complex analysis
- Groups are named by their grouping values