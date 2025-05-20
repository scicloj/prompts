# Noise Generation with fastmath.random

## Purpose
This prompt demonstrates how to generate various types of noise using fastmath.random, useful for procedural generation, simulations, and creative applications.

## Setup
```clojure
(require '[fastmath.random :as r])
```

## Single Noise Generation

### Basic Noise Types

```clojure
;; Create a basic value noise function
(def value-noise (r/single-noise {:noise-type :value}))

;; Create a gradient (Perlin) noise function
(def gradient-noise (r/single-noise {:noise-type :gradient}))

;; Create a simplex noise function
(def simplex-noise (r/single-noise {:noise-type :simplex}))

;; Evaluate noise at specific coordinates
(value-noise 0.5 0.5)
(gradient-noise 0.5 0.5)
(simplex-noise 0.5 0.5)
```

### Noise Configuration Options

```clojure
;; Configure noise with a seed for reproducibility
(def seeded-noise (r/single-noise {:noise-type :simplex
                                   :seed 42}))

;; Configure interpolation type
(def linear-noise (r/single-noise {:noise-type :value
                                   :interpolation :linear}))
(def hermite-noise (r/single-noise {:noise-type :value
                                    :interpolation :hermite}))
(def quintic-noise (r/single-noise {:noise-type :value
                                    :interpolation :quintic}))

;; Available interpolation types:
;; :none, :linear, :hermite, :quintic
```

### Higher-Dimensional Noise

```clojure
;; Create 1D, 2D, 3D and 4D noise functions
(def noise-1d (r/single-noise {:noise-type :simplex
                               :dimensions 1}))
(def noise-2d (r/single-noise {:noise-type :simplex
                               :dimensions 2}))
(def noise-3d (r/single-noise {:noise-type :simplex
                               :dimensions 3}))
(def noise-4d (r/single-noise {:noise-type :simplex
                               :dimensions 4}))

;; Evaluate noise at different dimensions
(noise-1d 0.5)
(noise-2d 0.5 0.5)
(noise-3d 0.5 0.5 0.5)
(noise-4d 0.5 0.5 0.5 0.5)
```

## Fractal Noise Generation

Fractal noise combines multiple octaves of noise to create more natural patterns.

### Fractal Brownian Motion (FBM)

```clojure
;; Create FBM noise function
(def fbm-noise (r/fbm-noise))

;; With configuration options
(def custom-fbm (r/fbm-noise {:seed 42
                              :noise-type :simplex
                              :octaves 6
                              :gain 0.5
                              :lacunarity 2.0}))

;; Evaluate FBM noise
(fbm-noise 0.5 0.5)
(custom-fbm 0.5 0.5)
```

### Billow Noise

```clojure
;; Create billow noise function
(def billow-noise (r/billow-noise))

;; With configuration options
(def custom-billow (r/billow-noise {:seed 1337
                                    :noise-type :gradient
                                    :octaves 4}))

;; Evaluate billow noise
(billow-noise 0.5 0.5)
(custom-billow 0.5 0.5)
```

### Ridged Multi Noise

```clojure
;; Create ridged multi noise function
(def ridged-noise (r/ridged-multi-noise))

;; With configuration options
(def custom-ridged (r/ridged-multi-noise {:seed 789
                                          :noise-type :simplex
                                          :octaves 5
                                          :gain 0.6}))

;; Evaluate ridged multi noise
(ridged-noise 0.5 0.5)
(custom-ridged 0.5 0.5)
```

## Advanced Techniques

### Domain Warping

```clojure
;; Create base noise functions
(def noise-a (r/fbm-noise {:seed 42}))
(def noise-b (r/fbm-noise {:seed 123}))
(def noise-c (r/fbm-noise {:seed 789}))

;; Create domain warping function
(defn warped-noise [x y]
  (let [warp-amount 0.5
        wx (+ x (* warp-amount (noise-a (+ x 0.5) (- y 0.5))))
        wy (+ y (* warp-amount (noise-b (- x 0.3) (+ y 0.7))))]
    (noise-c wx wy)))

;; Evaluate warped noise
(warped-noise 0.5 0.5)
```

### Creating a Noise Pipeline

```clojure
;; Create a noise pipeline that combines different noise types
(defn terrain-noise [x y]
  (let [base (r/fbm-noise {:octaves 6 :seed 42})
        detail (r/fbm-noise {:octaves 8 :seed 123 :gain 0.4})
        mountains (r/ridged-multi-noise {:octaves 4 :seed 789})
        base-value (base x y)
        mountain-mask (-> (+ (* 0.5 base-value) 0.5)
                          (Math/pow 2))
        detail-value (* 0.2 (detail (* 2 x) (* 2 y)))
        mountain-value (* mountain-mask (mountains x y))]
    (+ base-value detail-value mountain-value)))

;; Evaluate combined noise
(terrain-noise 0.5 0.5)
```

## Examples: Common Use Cases

### Generating Terrain Heightmap

```clojure
;; Generate terrain heightmap for a 10x10 grid
(defn gen-heightmap [width height noise-fn]
  (for [y (range height)]
    (for [x (range width)]
      (let [nx (/ x width)
            ny (/ y height)]
        ;; Scale to [0, 1] range for height
        (/ (+ 1 (noise-fn nx ny)) 2)))))

(def terrain (gen-heightmap 10 10 (r/fbm-noise {:octaves 6})))
```

### Creating a Cloud Texture

```clojure
;; Generate a cloud texture with more detail at higher frequencies
(def cloud-noise 
  (r/fbm-noise {:noise-type :simplex
                :octaves 6
                :gain 0.7        ;; Higher gain emphasizes details
                :lacunarity 2.5  ;; Increased frequency change between octaves
                :seed 42}))

(defn cloud-value [x y]
  (let [base (cloud-noise x y)
        ;; Remap from [-1,1] to [0,1] with contrast adjustment
        remapped (Math/pow (/ (+ base 1.0) 2.0) 1.5)]
    ;; Threshold to create cloud shapes
    (if (> remapped 0.55) remapped 0.0)))

;; Generate a 20x20 cloud texture
(def cloud-texture 
  (for [y (range 20)]
    (for [x (range 20)]
      (cloud-value (/ x 20) (/ y 20)))))
```

## Tips
- Different noise types have different characteristics:
  - Value noise: Blocky, faster but less smooth
  - Gradient/Perlin noise: Smooth, classic noise pattern
  - Simplex noise: Smoother gradient, less directional artifacts, better for higher dimensions
- Higher octave counts give more detail but are more computationally expensive
- Seed values ensure reproducible noise patterns
- For animations, use an additional time dimension in your noise function