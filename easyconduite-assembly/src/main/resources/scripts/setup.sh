#!/bin/bash
#########################################################
# This script install Easyconduite on HOME/USER/ Directory
# Antony Fons 2017
# antony.fons@antonyweb.net
#########################################################

# get system language
langue=$LANGUAGE

easyconduiteDir="$HOME/easyconduite"

# get home's owner
homeOwner=$(find $HOME -maxdepth 0 -printf '%u')
# import internationalization bundles
source ./installer/i18_linux/bundle_$langue.conf

# Functions
checkErrorRollBack() {
if [ $1 != "0" ]; then
	echo -e '\033[1;33;40m'"$errorRollBack"
	echo $cancelInstall
	echo -e '\033[0m'
	if [ -e $easyconduiteDir ] && [ -d $easyconduiteDir ]; then
		rm -r $easyconduiteDir
	fi
	exit 1
fi

}

installer () {

# check $HOME/Easyconduite not exist, and if create
if [ ! -e $easyconduiteDir ] || [ ! -d $easyconduiteDir ]; then
	# create Easyconduite directory
	mkdir $easyconduiteDir "$easyconduiteDir/docs" "$easyconduiteDir/images" "$easyconduiteDir/lib"
	checkErrorRollBack $?
fi
cp -u ./images/*.* $easyconduiteDir/images/
checkErrorRollBack $?
cp -u ./docs/*.pdf $easyconduiteDir/docs/
cp -u ./docs/*.txt $easyconduiteDir/docs/
cp -u ./lib/*.jar $easyconduiteDir/lib/
checkErrorRollBack $?
cp -u ./*.sh $easyconduiteDir
checkErrorRollBack $?
cp -u ./installer/*.desktop $easyconduiteDir
checkErrorRollBack $?
echo -e "\n$installationFinished"'\033[1;32;40m'"[OK]"'\033[0m'"\n"

}

messageJava(){
echo -e '\033[1;33;40m'"$alertJava"'\033[0m'
read -p "$confirm" -n 1 r
if [ $r != "y" ] && [ $r != "Y" ]; then
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

# installation confirmation
read -p "$confirm" -n 1 r
if [ $r = "y" ] || [ $r = "Y" ]; then
	installer
else
	echo -e "\n$cancelInstall"
	exit 0
fi

#check for java
command -v java 2>&1 >/dev/null
if [ $? = "1" ]; then
	echo -e "\n"
	messageJava
fi

if ! java -version 2>&1 >/dev/null | grep -q "1.8" ; 
then
	echo -e "\n"	
	messageJava
fi

exit 0
