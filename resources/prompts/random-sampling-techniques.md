# Random Sampling Techniques with fastmath.random

## Purpose
This prompt covers various sampling techniques and utilities using fastmath.random, including reservoir sampling, weighted sampling, and shuffling.

## Setup
```clojure
(require '[fastmath.random :as r])
```

## Shuffling

### Basic Shuffling
```clojure
;; Shuffle a collection
(r/shuffle (range 10))

;; Shuffle with custom RNG
(def my-rng (r/rng :mersenne 42))
(r/shuffle [1 2 3 4 5] my-rng)
```

### Partial Shuffling
```clojure
;; Shuffle just the first N elements
(defn partial-shuffle [coll n]
  (let [v (vec coll)
        shuffled (r/shuffle (take n v))
        rest-items (drop n v)]
    (concat shuffled rest-items)))

(partial-shuffle (range 10) 3)
```

## Sampling Without Replacement

### Choose K Items at Random
```clojure
;; Select k items randomly without replacement
(r/sample-wo-replacement (range 100) 5)

;; With custom RNG
(r/sample-wo-replacement (range 100) 10 my-rng)
```

### Reservoir Sampling
Efficient sampling when the total size is unknown:

```clojure
;; Reservoir sampling implementation
(defn reservoir-sample [coll k]
  (let [res (atom (vec (take k coll)))
        rest-items (drop k coll)]
    (loop [items rest-items
           idx k]
      (if (seq items)
        (let [item (first items)
              j (r/irand (inc idx))]
          (when (< j k)
            (swap! res assoc j item))
          (recur (rest items) (inc idx)))
        @res))))

;; Sample 5 items from a larger collection
(reservoir-sample (range 1000) 5)
```

## Weighted Sampling

### Sampling with Replacement

```clojure
;; Weighted sampling with replacement
(def items [:a :b :c :d])
(def weights [0.1 0.4 0.3 0.2])

;; Create a weighted sampler function
(def weighted-sampler (r/weighted-sampler items weights))

;; Generate samples
(weighted-sampler)
(repeatedly 10 weighted-sampler)

;; With custom RNG
(def custom-sampler (r/weighted-sampler items weights my-rng))
(repeatedly 5 custom-sampler)
```

### Weighted Sampling Without Replacement

```clojure
;; Weighted sampling without replacement
(defn weighted-sample-wo-replacement [items weights n]
  (loop [remaining-items (vec items)
         remaining-weights (vec weights)
         result []]
    (if (or (empty? remaining-items) (= (count result) n))
      result
      (let [sampler (r/weighted-sampler remaining-items remaining-weights)
            item (sampler)
            idx (.indexOf remaining-items item)]
        (recur (vec (concat (subvec remaining-items 0 idx) 
                           (subvec remaining-items (inc idx))))
               (vec (concat (subvec remaining-weights 0 idx) 
                           (subvec remaining-weights (inc idx))))
               (conj result item))))))

;; Sample 2 items based on weights
(weighted-sample-wo-replacement items weights 2)
```

## Importance Sampling

```clojure
;; Importance sampling example
(defn target-distribution [x]
  (let [mu 3.0
        sigma 0.5]
    (/ (Math/exp (- (/ (Math/pow (- x mu) 2) 
                       (* 2 sigma sigma))))
       (* sigma (Math/sqrt (* 2 Math/PI))))))

;; Use a normal distribution as proposal distribution
(def proposal-dist (r/distribution :normal {:mu 2.5 :sd 1.0}))

;; Importance sampling function
(defn importance-sample [n]
  (let [samples (repeatedly n #(r/sample proposal-dist))
        weights (map #(/ (target-distribution %)
                         (r/pdf proposal-dist %)) 
                    samples)]
    {:samples samples
     :weights weights
     :estimate (/ (reduce + weights) n)}))

;; Perform importance sampling
(importance-sample 1000)
```

## Stratified Sampling

```clojure
;; Stratified sampling in 2D
(defn stratified-samples-2d [nx ny jitter]
  (for [y (range ny)
        x (range nx)]
    (let [jx (if (pos? jitter) (* jitter (r/drand -0.5 0.5)) 0)
          jy (if (pos? jitter) (* jitter (r/drand -0.5 0.5)) 0)]
      [(/ (+ x 0.5 jx) nx)
       (/ (+ y 0.5 jy) ny)])))

;; Generate 4x4 stratified samples with jitter
(stratified-samples-2d 4 4 0.9)
```

## Rejection Sampling

```clojure
;; Rejection sampling from an arbitrary distribution
(defn rejection-sample [pdf max-pdf domain]
  (let [[min-x max-x] domain]
    (loop []
      (let [x (r/drand min-x max-x)
            y (r/drand max-pdf)]
        (if (<= y (pdf x))
          x
          (recur))))))

;; Sample from a bimodal distribution
(defn bimodal-pdf [x]
  (let [pdf1 (/ (Math/exp (- (/ (Math/pow (- x 2) 2) 2))) (Math/sqrt (* 2 Math/PI)))
        pdf2 (/ (Math/exp (- (/ (Math/pow (- x 6) 2) 2))) (Math/sqrt (* 2 Math/PI)))]
    (/ (+ pdf1 pdf2) 2)))

;; Generate samples
(repeatedly 10 #(rejection-sample bimodal-pdf 0.2 [0 8]))
```

## Examples: Common Use Cases

### Monte Carlo Integration Using Stratified Sampling

```clojure
;; Integrate a function using stratified sampling
(defn f [x y] 
  (Math/exp (- (+ (* x x) (* y y)))))

(defn monte-carlo-integrate [f samples]
  (let [n (count samples)
        volume 1.0  ;; Assuming [0,1] x [0,1] domain
        sum (reduce + (map (fn [[x y]] (f x y)) samples))]
    (* volume (/ sum n))))

;; Using stratified sampling for better convergence
(monte-carlo-integrate f (stratified-samples-2d 10 10 0.8))
```

### Generating Random Points on a Mesh

```clojure
;; Generate random points on triangular faces of a mesh
(defn random-point-on-triangle [vertices]
  (let [[a b c] vertices
        ;; Barycentric coordinates
        r1 (r/drand)
        r2 (r/drand)
        ;; If r1+r2 > 1, reflect point to stay in triangle
        [r1' r2'] (if (> (+ r1 r2) 1)
                    [(- 1 r1) (- 1 r2)]
                    [r1 r2])
        r3' (- 1 r1' r2')]
    (mapv + 
          (mapv (partial * r1') a)
          (mapv (partial * r2') b)
          (mapv (partial * r3') c))))

;; Sample mesh with multiple triangles
(def mesh [[[0 0 0] [1 0 0] [0 1 0]]
           [[1 0 0] [1 1 0] [0 1 0]]
           [[0 0 0] [0 0 1] [1 0 0]]
           [[0 0 1] [1 0 1] [1 0 0]]])

;; Weight triangles by area for uniform sampling
(defn triangle-area [[[x1 y1 z1] [x2 y2 z2] [x3 y3 z3]]]
  (let [a [(- x2 x1) (- y2 y1) (- z2 z1)]
        b [(- x3 x1) (- y3 y1) (- z3 z1)]
        ;; Cross product for area
        cp [(- (* (a 1) (b 2)) (* (a 2) (b 1)))
            (- (* (a 2) (b 0)) (* (a 0) (b 2)))
            (- (* (a 0) (b 1)) (* (a 1) (b 0)))]]
    (* 0.5 (Math/sqrt (+ (* (cp 0) (cp 0))
                         (* (cp 1) (cp 1))
                         (* (cp 2) (cp 2)))))))

;; Sample points on mesh with probability proportional to face area
(defn sample-points-on-mesh [mesh n]
  (let [areas (map triangle-area mesh)
        sampler (r/weighted-sampler mesh areas)]
    (repeatedly n #(random-point-on-triangle (sampler)))))
```

## Tips
- Use weighted sampling for importance sampling and modeling non-uniform probabilities
- Stratified sampling reduces variance in Monte Carlo methods
- Rejection sampling works for arbitrary distributions but can be inefficient
- For large datasets, reservoir sampling allows sampling with constant memory
- Consider using specialized sequence generators for better coverage in high dimensions