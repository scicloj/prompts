# Statistical Distribution Sampling with fastmath.random

## Purpose
This prompt demonstrates how to work with statistical distributions for generating random values following specific probability distributions.

## Setup
```clojure
(require '[fastmath.random :as r])
```

## Creating Distributions

### Basic Distribution Creation
```clojure
;; Create distributions with default parameters
(def normal-dist (r/distribution :normal))
(def uniform-dist (r/distribution :uniform))
(def exponential-dist (r/distribution :exponential))

;; Create distributions with custom parameters
(def custom-normal (r/distribution :normal {:mu 10.0 :sd 2.0}))
(def custom-uniform (r/distribution :uniform {:lower 5.0 :upper 15.0}))
(def gamma-dist (r/distribution :gamma {:shape 2.0 :scale 1.5}))
```

### Available Distribution Types

#### Continuous Distributions
```clojure
;; Common continuous distributions
(r/distribution :normal)      ;; Normal/Gaussian distribution
(r/distribution :uniform)     ;; Uniform distribution
(r/distribution :exponential) ;; Exponential distribution
(r/distribution :gamma)       ;; Gamma distribution
(r/distribution :beta)        ;; Beta distribution
(r/distribution :cauchy)      ;; Cauchy distribution
(r/distribution :logistic)    ;; Logistic distribution
(r/distribution :log-normal)  ;; Log-normal distribution
(r/distribution :triangular)  ;; Triangular distribution
(r/distribution :weibull)     ;; Weibull distribution
```

#### Discrete Distributions
```clojure
;; Common discrete distributions
(r/distribution :binomial)    ;; Binomial distribution
(r/distribution :poisson)     ;; Poisson distribution
(r/distribution :geometric)   ;; Geometric distribution
(r/distribution :pascal)      ;; Pascal (negative binomial) distribution
(r/distribution :zipf)        ;; Zipf distribution
```

## Working with Distributions

### Sampling from Distributions
```clojure
;; Generate a single sample
(r/sample normal-dist)
(r/sample custom-uniform)

;; Generate multiple samples
(repeatedly 5 #(r/sample normal-dist))

;; Generate samples with custom RNG
(def my-rng (r/rng :mersenne 42))
(r/sample normal-dist my-rng)
```

### Distribution Properties and Functions

#### Probability Density/Mass Function (PDF/PMF)
```clojure
;; Evaluate the probability density at a point
(r/pdf normal-dist 0.0)       ;; PDF at x=0 for normal distribution
(r/pdf exponential-dist 1.0)  ;; PDF at x=1 for exponential distribution
```

#### Cumulative Distribution Function (CDF)
```clojure
;; Evaluate the cumulative probability at a point
(r/cdf normal-dist 0.0)       ;; Probability of x ≤ 0
(r/cdf custom-uniform 10.0)   ;; Probability of x ≤ 10
```

#### Inverse CDF (Quantile Function)
```clojure
;; Find the value at a given probability level
(r/icdf normal-dist 0.5)      ;; Median (50th percentile)
(r/icdf normal-dist 0.95)     ;; 95th percentile
```

#### Distribution Statistics
```clojure
;; Calculate distribution mean
(r/mean normal-dist)
(r/mean gamma-dist)

;; Calculate distribution variance
(r/variance normal-dist)
(r/variance gamma-dist)
```

## Examples: Common Use Cases

### Generating Data with Specific Distribution
```clojure
;; Generate 1000 samples from a normal distribution
(def samples (repeatedly 1000 #(r/sample normal-dist)))

;; Calculate sample statistics
(require '[fastmath.stats :as stats])
(stats/mean samples)
(stats/standard-deviation samples)
```

### Creating a Mixture of Distributions
```clojure
;; Create a mixture of two normal distributions
(defn sample-mixture []
  (if (< (r/drand) 0.7)
    (r/sample (r/distribution :normal {:mu 0 :sd 1}))
    (r/sample (r/distribution :normal {:mu 5 :sd 0.5}))))

(def mixture-samples (repeatedly 1000 sample-mixture))
```

## Tips
- Use appropriate distributions for your data generating process
- For reproducible samples, use a seeded RNG with your distribution
- Some distributions have constraints on parameter values
- Use distribution statistics to verify your sampling process