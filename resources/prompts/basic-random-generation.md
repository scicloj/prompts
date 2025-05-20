# Basic Random Number Generation with fastmath.random

## Purpose
This prompt demonstrates how to generate various types of random numbers using fastmath.random.

## Setup
```clojure
(require '[fastmath.random :as r])
```

## Creating Random Number Generators (RNGs)

### Default RNG
```clojure
;; Use default random number generator
(r/frand)      ;; Random float in [0, 1)
(r/drand)      ;; Random double in [0, 1)
(r/irand)      ;; Random integer (32-bit)
(r/lrand)      ;; Random long (64-bit)
(r/grand)      ;; Random value from Gaussian distribution
(r/brand 0.3)  ;; Random boolean with given probability (0.3)
```

### Custom RNG with Specific Algorithm
```clojure
;; Create a custom RNG with specific algorithm
(def my-rng (r/rng :mersenne))  ;; Mersenne Twister algorithm
(def isaac-rng (r/rng :isaac))  ;; ISAAC algorithm
(def well-rng (r/rng :well512a)) ;; WELL algorithm

;; Available algorithms
;; :jdk, :mersenne, :isaac, :well512a, :well1024a, :well19937a, :well19937c, :well44497a, :well44497b
```

### Seeded RNG for Reproducible Results
```clojure
;; Create RNG with a specific seed for reproducibility
(def seeded-rng (r/rng :mersenne 42))

;; Generate values from the seeded RNG
(r/frand seeded-rng)
(r/irand seeded-rng)
```

## Generating Random Values in Ranges

### Numeric Ranges
```clojure
;; Random integer in [0, 100)
(r/irand 100)

;; Random integer in [20, 30)
(r/irand 20 30)

;; Random double in [0.0, 10.0)
(r/drand 10.0)

;; Random double in [5.0, 15.0)
(r/drand 5.0 15.0)
```

### Gaussian Random Values
```clojure
;; Standard normal distribution (mean=0, sd=1)
(r/grand)

;; Custom normal distribution
(r/grand 10.0 2.0)  ;; mean=10, sd=2
```

## Generating Multiple Random Values

```clojure
;; Generate a sequence of 5 random doubles
(repeatedly 5 r/drand)

;; Generate a sequence of 10 random integers in [1, 100]
(repeatedly 10 #(inc (r/irand 100)))

;; Generate a sequence with custom RNG
(repeatedly 5 #(r/drand my-rng))
```

## Tips
- Use seeded RNGs for reproducible results
- Different RNG algorithms have different performance and statistical properties
- For statistical distributions, use the distributions API (see distribution-sampling.md)
- Thread-safety depends on the RNG algorithm, some are thread-safe by default