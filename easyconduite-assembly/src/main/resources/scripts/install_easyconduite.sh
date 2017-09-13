#!/bin/bash
#########################################################
# This script executes Easyconduite-1.1.
# It supposes that the jar is located within :
# $HOME/Easyconduite-1.1/bin/
#
# Antony Fons 2017
# antony.fons@antonyweb.net
#########################################################

#DIALOG=${DIALOG=dialog}

#XDialog ckecking
if [ -z $DISPLAY ]
then
DIALOG=dialog
else
DIALOG=Xdialog
fi


destDir=$HOME

installer () {
# TODO
echo Appel installer $1
}

cancelMsg () {
$DIALOG --cr-wrap --title "Avertissement" --msgbox \
			"\n\nVous avez stopper l'installation de Easyconduite-1.1"  14 48
clear
exit 0
}

# check for Java 1.8
if ! java -version 2>&1 >/dev/null | grep -q "1.9" ; 
then
	$DIALOG --title "Avertissement" --colors --msgbox \
			"\n\nImpossible d'installer Easyconduite-1.1 car \n\Z1Java 1.8 ou sup. n'est pas présent sur votre ordinateur\Zn"  14 48
	clear
	exit 0
fi
$DIALOG --cr-wrap --title "Installation EasyConduite-1.1 pour Linux" --yesno \
			"\n\nBienvenue dans l'installation de \nEasyconduite-1.1 ?\
			\n\nVoulez-vous poursuivre" 14 48
			
if [ $? = "0" ]
	then
		DESTREP=`$DIALOG --stdout --title "Choisissez un répertoire de destination" --dselect $destDir/ 14 48`

		case $? in
			0)
				installer $DESTREP;;
			1)
				cancelMsg;;
			255)
				cancelMsg;;
		esac
	else
		cancelMsg
fi

	


#java -Xms512M -Xmx512M -jar $appliDir/bin/easyconduite-1.1-SNAPSHOT.jar &
exit 0