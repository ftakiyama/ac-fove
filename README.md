AC-FOVE API
===========

What
----
This is an implementation of the AC-FOVE algorithm proposed by Kisynski (2010). AC-FOVE stands for Aggregation/Counting First Order Variable Elimination.

How
---
The package is provided as an API (Java) that can be used in other applications. See the [Wiki](https://github.com/ftakiyama/AC-FOVE/wiki) for further details.

Dependencies
------------
The core package does not depend on any external libraries. There are some dependencies if you try to run the test cases:
- [junit 4.11](https://github.com/junit-team/junit/wiki): testing framework.
- [hamcrest 1.3](https://code.google.com/p/hamcrest/): matchers used to make tests more readable.
- [velocity 1.7](http://velocity.apache.org/): templating framework used to build HTML logs.

References
----------
Kisynski, J. Aggregation and Constraint Processing in Lifted Probabilistic Inference. PhD Thesis. University of British Columbia, 2010.
