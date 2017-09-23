#!/bin/bash
#########################################################
# This script executes Easyconduite-${project.version}.
# It supposes that the jar is located within :
# $HOME/Easyconduite-${project.version}/bin/
#
# Antony Fons 2017
# antony.fons@antonyweb.net
#########################################################

java -Xms512M -Xmx512M -jar ./lib/easyconduite-1.2.jar 2>&1 >/dev/null
exit 0
