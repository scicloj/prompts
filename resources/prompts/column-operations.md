# Column Operations with Tablecloth

## Purpose
This prompt covers adding, updating, and manipulating columns in datasets using Tablecloth.

## Setup
```clojure
(require '[tablecloth.api :as tc])

;; Sample dataset for examples
(def DS (tc/dataset {:V1 [1 2 3 4 5]
                     :V2 [10 20 30 40 50]
                     :V3 [0.1 0.2 0.3 0.4 0.5]
                     :V4 ["A" "B" "C" "D" "E"]}))
```

## Adding Columns

### Add a Single Column
Add a constant value:
```clojure
(tc/add-column DS :V5 "X")
```

Add a sequence (must match dataset row count):
```clojure
(tc/add-column DS :V5 (range 5))
```

Add a column based on a function:
```clojure
(tc/add-column DS :V5 #(map inc (% :V1)))
```

### Add Multiple Columns
```clojure
(tc/add-columns DS 
  {:V5 #(map (comp keyword str) (% :V4))
   :V6 11
   :V7 #(map + (% :V1) (% :V2))})
```

## Selecting Columns

```clojure
;; Select by name
(tc/select-columns DS [:V1 :V3])

;; Select by predicate
(tc/select-columns DS #(= % :V1))

;; Select by regex pattern
(tc/select-columns DS #"^V[13]$")
```

## Renaming Columns

```clojure
;; Rename specific columns
(tc/rename-columns DS {:V1 "v1" :V2 "v2"})

;; Rename using a function
(tc/rename-columns DS name)
```

## Updating Columns

### Update a Single Column
```clojure
(tc/update-column DS :V1 inc)
```

### Update Multiple Columns
```clojure
;; Update all columns
(tc/update-columns DS :all reverse)

;; Update specific columns
(tc/update-columns DS [:V1 :V2] inc)

;; Update by column type
(tc/update-columns DS :type/numerical 
  [(partial map dec)
   (partial map inc)])
```

### Dropping Columns
```clojure
(tc/drop-columns DS :V1)
(tc/drop-columns DS [:V1 :V3])
```

## Converting Column Types
```clojure
(tc/convert-types DS {:V1 :float64
                      :V4 :categorical})
```

## Tips
- Use :all keyword to operate on all columns
- Use :type/numerical, :type/categorical, etc. to target columns by type
- Column functions receive the whole dataset as parameter
- Chain operations with -> or ->> for complex transformations