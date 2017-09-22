#!/bin/bash
#########################################################
# This script executes Easyconduite-${project.version}
# 
# 
#
# Antony Fons 2017
# antony.fons@antonyweb.net
#########################################################

# get system language
langue=$LANGUAGE

# get home's owner
homeOwner=$(find $HOME -maxdepth 0 -printf '%u')
# import internationalization bundles
source ./installer/i18_linux/bundle_$langue.conf

# Functions
installer () {
# TODO
echo -e "\nAppel installer"
}
messageJava(){
echo -e '\033[1;33;40m'"$alertJava"'\033[0m'
read -p "$confirm" -n 1 r
if [ $r != "y" ] && [ $r != "Y" ]
then
	echo -e "\n$cancelInstall"
	exit 0
fi
}


printf '\033[8;40;80t'
clear
echo -e '\033[1;36;40m'
echo $bar
echo "$title"
echo "$description"
echo "$copyright"
echo $bar
echo -e '\033[0m'
echo "$informInstall ($HOME)"


#resize window terminal to 40 lines, 100 car.
#printf '\033[8;40;100t'

#dilaog library ckeching
#command -v dialog 2>&1 >/dev/null
#if [ $? = "1" ]
#then
#		echo -e '\033[1;33;40m' $dialogrequired '\033[0m'
#		read -p "désirez-vous continuer (dialog sera installée) y/n ?" -n 1 r
#		echo -e '\n'$r
#fi

destDir=$HOME
# installation confirmation
read -p "$confirm" -n 1 r
if [ $r = "y" ] || [ $r = "Y" ]
then
	installer
else
	echo -e "\n$cancelInstall"
	exit 0
fi

#check for java
command -v java 2>&1 >/dev/null
if [ $? = "1" ]
then
	messageJava
else
	java -version
fi

if ! java -version 2>&1 >/dev/null | grep -q "1.8" ; 
then
	echo -e "\n"	
	messageJava
fi

cp ./easyconduite.bat $HOME/Easyconduite/bin
#java -Xms512M -Xmx512M -jar $appliDir/bin/easyconduite-${project.version}.jar &
exit 0
