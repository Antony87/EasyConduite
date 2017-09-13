[Setup]
WizardImageFile=compiler:WizModernImage-IS.bmp
WizardSmallImageFile=compiler:WizModernSmallImage-IS.bmp
AppName=EasyConduite
AppVersion=1.2
LicenseFile=userdocs:EasyConduite\docs\LICENSE
RestartIfNeededByRun=False
SetupIconFile=userdocs:EasyConduite\images\easyconduite32.ico
DefaultDirName={userdocs}\EasyConduite-1.2
UsePreviousSetupType=False
UsePreviousAppDir=False
ShowUndisplayableLanguages=True
UsePreviousLanguage=False
OutputDir=userdocs:EasyConduite
OutputBaseFilename=install_Easyconduite
CloseApplications=False
Compression=lzma2/normal
VersionInfoVersion=1.2

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"

[Files]
Source: "bin\*"; DestDir: "{app}\bin"
Source: "docs\*"; DestDir: "{app}\docs"
Source: "images\*"; DestDir: "{app}\images"

[Icons]
Name: "{app}\EasyConduite-1.2"; Filename: "{app}\bin\easyconduite-1.2.bat"; WorkingDir: "{app}"; IconFilename: "{app}\images\easyconduite32.ico"