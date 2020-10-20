# java-parametric-databases

[![Build Status](https://travis-ci.org/microfocus-idol/java-parametric-databases.svg?branch=master)](https://travis-ci.org/microfocus-idol/java-parametric-databases)

Java Client for parametric values.

This library consists of two specific use cases of the HOD APIs.

The first is a combination of the Resources Service and the Index Fields Service. This provides indexes,
and for each index its index fields.

The second use case provides the parametric values for a set of given indexes and a set of given index fields.
By retaining a mapping of the indexes to their index field from the request from the first use, the client can
build the index fields for given indexes needed by this api itself.

## License

(c) Copyright 2014-2018 Micro Focus or one of its affiliates.

Licensed under the MIT License (the "License"); you may not use this project except in compliance with the License.
