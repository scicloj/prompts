# Random Sequence Generation with fastmath.random

## Purpose
This prompt covers generating various types of random sequences, including low-discrepancy and quasi-random sequences for more uniform sampling of high-dimensional spaces.

## Setup
```clojure
(require '[fastmath.random :as r])
```

## Low-Discrepancy Sequence Generators

Low-discrepancy sequences provide more uniform coverage of a space than purely random sequences, which is valuable for numerical integration, optimization, and sampling.

### Basic Sequence Generation
```clojure
;; Create a sequence generator for 2D Sobol sequence
(def sobol-2d (r/sequence-generator :sobol 2))

;; Generate the first 5 points in the sequence
(take 5 sobol-2d)

;; Available low-discrepancy sequence types:
;; :halton, :sobol, :r2
```

### Multi-Dimensional Sequences
```clojure
;; Create sequence generators of different dimensions
(def halton-3d (r/sequence-generator :halton 3))
(def sobol-5d (r/sequence-generator :sobol 5))
(def r2-2d (r/sequence-generator :r2 2))

;; Get points from each sequence
(first halton-3d)  ;; 3-dimensional point
(first sobol-5d)   ;; 5-dimensional point
```

### Jittered Sequences
Add controlled randomness to low-discrepancy sequences for certain applications:

```clojure
;; Create jittered sequence generators
(def jittered-halton (r/jittered-sequence-generator :halton 2))
(def jittered-sobol (r/jittered-sequence-generator :sobol 3 0.01))

;; Generate points
(take 3 jittered-halton)
(take 3 jittered-sobol)
```

## Sphere and Ball Sequence Generators

Generate points uniformly distributed on a sphere or within a ball:

### Sphere Sequences (surface only)
```clojure
;; Generate points on a 3D sphere (uniformly distributed)
(def sphere-3d (r/sequence-generator :sphere 3))

;; Get points from the sequence
(take 5 sphere-3d)

;; With options (different dimensions)
(def sphere-2d (r/sequence-generator :sphere 2))  ;; Points on a circle
(def sphere-4d (r/sequence-generator :sphere 4))  ;; Points on a 4D hypersphere
```

### Ball Sequences (entire volume)
```clojure
;; Generate points within a 3D ball (uniformly distributed)
(def ball-3d (r/sequence-generator :ball 3))

;; Get points from the sequence
(take 5 ball-3d)

;; With different dimensions
(def ball-2d (r/sequence-generator :ball 2))  ;; Points within a disk
(def ball-4d (r/sequence-generator :ball 4))  ;; Points within a 4D hyperball
```

## Gaussian/Normal Sequence Generators

```clojure
;; Generate points from multivariate normal distribution
(def gaussian-2d (r/sequence-generator :gaussian 2))

;; Get points from the sequence
(take 5 gaussian-2d)
```

## Advanced Usage: Sequence Transformations

### Mapping to Different Ranges
```clojure
;; Map 2D Sobol sequence to custom range
(def sobol-gen (r/sequence-generator :sobol 2))
(defn transform-point [[x y]]
  [(+ 10 (* 5 x))    ;; Map x from [0,1] to [10,15]
   (* 100 y)])       ;; Map y from [0,1] to [0,100]

(def transformed-seq (map transform-point sobol-gen))
(take 3 transformed-seq)
```

### Creating Constrained Random Points
```clojure
;; Generate points within an ellipse
(def ball-gen (r/sequence-generator :ball 2))
(defn ellipse-point [[x y]]
  [(* 3 x)    ;; Semi-major axis = 3
   (* 1 y)])  ;; Semi-minor axis = 1

(def ellipse-seq (map ellipse-point ball-gen))
(take 5 ellipse-seq)
```

## Examples: Common Use Cases

### Monte Carlo Integration
```clojure
;; Estimate π using points in a unit square
(defn inside-quarter-circle? [[x y]]
  (<= (+ (* x x) (* y y)) 1.0))

(defn estimate-pi [n]
  (let [points (take n (r/sequence-generator :sobol 2))
        inside-count (count (filter inside-quarter-circle? points))]
    (* 4.0 (/ inside-count n))))

(estimate-pi 1000)  ;; Should be close to π
```

### Sampling for Optimization
```clojure
;; Find minimum of a function using quasi-random sampling
(defn objective-fn [[x y]]
  (+ (* x x) (* y y) (* -2 x) (* 3 y)))

(defn find-minimum [n]
  (let [samples (take n (r/sequence-generator :sobol 2))
        ;; Map [0,1] range to [-5,5]
        transformed (map (fn [[x y]] [(- (* 10 x) 5) (- (* 10 y) 5)]) samples)
        evaluated (map (fn [p] {:point p :value (objective-fn p)}) transformed)]
    (apply min-key :value evaluated)))

(find-minimum 1000)
```

## Tips
- Low-discrepancy sequences are deterministic but appear random
- For true randomness with good coverage, use jittered sequences
- Sequence generators are lazy sequences and can generate infinite points
- Higher dimensions may require more points for adequate coverage
- Different sequence types have different properties and are suitable for different problems