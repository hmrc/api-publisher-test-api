#!/bin/bash

sbt "~run -Drun.mode=Dev -Dhttp.port=15509 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes $*"
