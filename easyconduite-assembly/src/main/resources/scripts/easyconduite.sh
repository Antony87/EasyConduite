#!/bin/bash
#########################################################
# This script executes Easyconduite-${project.version}.
# It supposes that the jar is located within :
# $HOME/Easyconduite-${project.version}/bin/
#
# Antony Fons 2017
# antony.fons@antonyweb.net
#########################################################

DIALOG=${DIALOG=dialog}
destDir=$HOME
appliDir=$HOME"/Easyconduite"

# check for Java 1.8
if ! java -version 2>&1 >/dev/null | grep -q "1.8" ; 
then
	$DIALOG --title "Avertissement" --msgbox "Impossible de lancer Easyconduite-${project.version} car Java 1.8 ou sup. n'est pas installé"  14 48
	clear
	exit 0
fi

java -Xms512M -Xmx512M -jar ./bin/easyconduite-${project.version}.jar 2>&1 >/dev/null
exit 0
