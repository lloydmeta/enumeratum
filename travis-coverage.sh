#!/usr/bin/env bash

set -xe;

for Project in $PROJECTS
do
  sbt ++${TRAVIS_SCALA_VERSION} "project ${Project}" test:compile test:doc;
  sbt ++${TRAVIS_SCALA_VERSION} "project ${Project}" coverage test coverageReport;
done

sbt ++${TRAVIS_SCALA_VERSION} coverageAggregate;

set +xe;