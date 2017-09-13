@echo off
title easyconduite-${project.version}


:: Detection de java installe

echo %path% | find /I /C "java" >NUL
if %errorlevel%==1 (
call :errormessage "Java ne semble pas être installé"
goto :end
)


where javaw >NUL
if %errorlevel%==1 (
call :errormessage "Easyconduite ne trouve pas l'exécutable Java"
goto :end
)

:: verification version de java min 1.8.0 = 18000
java -fullversion 2>&1 | find /I /C "1.8" >NUL
if %errorlevel%==1 (
call :errormessage "Easyconduite a besoin de la version 1.8.0 minimum de Java"
goto :end
)

:starteasyconduite
start /B java -jar ./bin/easyconduite-${project.version}.jar -spalsh:images/easyconduitelarge.png

goto :end

:errormessage
echo msgbox %1,16,"Impossibilité d'execution" > %tmp%\tmp.vbs
cscript /nologo %tmp%\tmp.vbs
del %tmp%\tmp.vbs

:end
