# OMS Skeleton

[![Build Status](https://travis-ci.org/underscorenico/skeleton-oms-java.svg?branch=master)](https://travis-ci.org/underscorenico/skeleton-oms-java)
[![codecov](https://codecov.io/gh/underscorenico/skeleton-oms-java/branch/master/graph/badge.svg)](https://codecov.io/gh/underscorenico/skeleton-oms-java)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/underscorenico/skeleton-oms-java/master/LICENSE.txt)

## Overview

This skeleton provides a micro-service for order management.
The stack is the following:

- Spring boot
- Camunda BPM
- Kafka

Camunda will use an embedded H2 database by default in the tests. Each environment's database can be configured from the application properties.

## Usage

Its usage is pretty straight-forward, just clean install and launch using the tomcat maven plugin (from the camunda-server module):

```
mvn clean install
cd camunda-server/
mvn tomcat7:run
```

## Contributing 

Everyone is welcome to contribute, either by adding features, solving bugs or helping with documentation.
<br>
This project embraces [the open code of conduct][codeofconduct] from the [TODO group][todogroup], therefore all of its channels should respect its guidelines.
<br>

[codeofconduct]: http://todogroup.org/opencodeofconduct
[todogroup]: http://todogroup.org
