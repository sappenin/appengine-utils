appengine-utils
===============
[![Build Status](https://travis-ci.org/sappenin/appengine-utils.png)](https://travis-ci.org/sappenin/appengine-utils)
[![Coverage Status](https://coveralls.io/repos/sappenin/appengine-utils/badge.png?branch=master)](https://coveralls.io/r/sappenin/appengine-utils?branch=master)

Various utilities for interacting with Google App Engine

Change Log
----------
**Version 1.2.1**
+ Add simple ConfigurationService framework for appengine.
+ Add NameSpaceWork and VoidNameSpaceWork to help perform units of work in a specific namespace.
+ Updated Dependencies: java-utils v1.12; appengine v1.9.28.

**Version 1.2.0**
+ Add basic password encryption services using jasypt library.
+ Add support for async task scheduling and handling
+ Fix AbstractEntity test harness design
+ Update dependencies

**Version 1.1.0**
+ Add new methods to AbstractObjectifyDao for entity existence testing.
+ Fix bug in ObjectifyEntityLockHelper Preconditions.
+ Update test harnesses for Objectify 5.1.
+ Improve unit test coverage.

**Version 1.0.4**
+ No Changes.

**Version 1.0.3**
+ Updated Guava to v18.0; Joda-Time to v2.4; Lombok to v1.14.8.
+ Added new classes for Aggregate Task handling.