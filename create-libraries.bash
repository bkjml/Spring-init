#!/usr/bin/env bash

spring init \
--boot-version=3.4.4 \
--type=gradle-project \
--java-version=21 \
--packaging=jar \
--name=api \
--package-name=se.mathias.api \
--groupId=se.mathias.microservices.api \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
apis

spring init \
--boot-version=3.4.4 \
--type=gradle-project \
--java-version=21 \
--packaging=jar \
--name=util \
--package-name=se.mathias.util \
--groupId=se.mathias.microservices.util \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
utils