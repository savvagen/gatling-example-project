#!/usr/bin/env bash
# RUN GATLING SCENARIO
./gatling-3.2.1/bin/gatling.sh -sf src/gatling/simulations/gatling -s gatling.JsonServerSimulation -rf $(pwd)