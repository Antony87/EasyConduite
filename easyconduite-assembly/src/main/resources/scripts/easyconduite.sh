#!/bin/bash
#########################################################
# This script executes Easyconduite-${project.version}.
# It supposes that the jar is located within :
# $HOME/Easyconduite-${project.version}/bin/
#
# Antony Fons 2017
# antony.fons@antonyweb.net
#########################################################

java -Xms512M -Xmx512M -jar ./bin/easyconduite-${project.version}.jar 2>&1 >/dev/null
exit 0
