rem lancement easyconduite windows

set PATH_TO_JAVA=C:\Users\V902832\Documents\jdk-11.0.6+10-jre
set PATH_TO_JAVAFX=.\lib

%PATH_TO_JAVA%\bin\java.exe -Djava.library.path=C:\tmp --module-path %PATH_TO_JAVAFX% --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.media -jar easyconduite-${project.version}-win.jar

