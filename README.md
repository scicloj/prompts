# Scicloj Prompts

(experimental proof-of-concept)

A library of recommended prompts for Clojure data analysis tasks using various libraries in the Scicloj ecosystem.

## Overview

Scicloj Prompts is a collection of markdown files containing code examples, patterns, and explanations for common data science tasks in Clojure. The prompts are organized by topic and can be accessed programmatically through a simple API.

The prompts cover tasks related to:
- Dataset manipulation with Tablecloth
- Data visualization with Tableplot's Plotly API
- Random number generation with fastmath.random
- And more...

## Initial example

[notebooks/explore.clj](notebooks/explore.clj)

[rendered](https://scicloj.github.io/prompts/explore)

## Usage
TBA

## Available Prompts

The library currently includes prompts for:

**Data Manipulation with Tablecloth:**
- Creating datasets from various data sources
- Filtering and selecting rows from datasets
- Adding, updating, and manipulating dataset columns
- Grouping datasets and performing aggregations
- Joining and combining datasets in various ways

**Data Visualization with Tableplot:**
- Creating interactive data visualizations with Plotly
- Visualizing time series data with interactive features
- Creating statistical plots for data analysis
- Visualizing geo-spatial data on interactive maps

**Random Number Generation with fastmath.random:**
- Basic random number generation
- Sampling from statistical distributions
- Generating random and quasi-random sequences
- Generating various types of noise for procedural generation
- Advanced random sampling techniques and utilities

## Contributing

Contributions are welcome! If you'd like to add a new prompt or improve an existing one, please follow these steps:

1. Create a new markdown file in the `resources/prompts/` directory
2. Add metadata to the `resources/prompts-catalog.edn` file
3. Submit a pull request

## License

Copyright © 2025 Scicloj

Distributed under the Eclipse Public License version 1.0.
