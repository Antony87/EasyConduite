rem lancement easyconduite windows

set PATH_TO_EXE_JAVA=C:\Users\V902832\Documents\OpenJDK11U-jdk_x64_windows_hotspot_11.0.4_11\jdk-11.0.4+11\bin\java.exe
set PATH_TO_JAVAFX=.\lib

%PATH_TO_EXE_JAVA% -Djava.library.path=C:\tmp --module-path %PATH_TO_JAVAFX% --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.media -jar easyconduite-${project.version}-win.jar

