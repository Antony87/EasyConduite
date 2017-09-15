[Setup]
WizardImageFile=compiler:WizModernImage-IS.bmp
WizardSmallImageFile=compiler:WizModernSmallImage-IS.bmp
AppName=EasyConduite
AppVersion=1.2
LicenseFile=..\docs\LICENSE.txt
RestartIfNeededByRun=False
SetupIconFile=..\images\easyconduite32.ico
DefaultDirName={userdocs}\EasyConduite-{#SetupSetting("AppVersion")}
UsePreviousSetupType=False
UsePreviousAppDir=False
ShowUndisplayableLanguages=True
UsePreviousLanguage=False
OutputDir=.\
OutputBaseFilename=install_Easyconduite
CloseApplications=False
Compression=lzma2/normal
VersionInfoVersion={#SetupSetting("AppVersion")}
AppPublisher=Antony Fons
AppPublisherURL=http://site.antonyweb.net/index.php?static1/easyconduite
UsePreviousGroup=False
DisableProgramGroupPage=yes
DisableReadyPage=True
DisableStartupPrompt=False

[Tasks]
Name: desktopicon; Description: "{cm:CreateDesktopIcon}"

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"; InfoBeforeFile: "..\docs\warning_en.rtf"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"; InfoBeforeFile: "..\docs\warning_fr.rtf"

[Files]
Source: "..\bin\*"; DestDir: "{app}\bin"
Source: "..\docs\*"; DestDir: "{app}\docs"
Source: "..\images\*"; DestDir: "{app}\images"

[Icons]
Name: "{app}\EasyConduite-{#SetupSetting("AppVersion")}"; Filename: "{app}\bin\easyconduite-{#SetupSetting("AppVersion")}.bat"; WorkingDir: "{app}"; IconFilename: "{app}\images\easyconduite32.ico"
Name: "{userdesktop}\EasyConduite-{#SetupSetting("AppVersion")}"; Filename: "{app}\bin\easyconduite-{#SetupSetting("AppVersion")}.bat"; WorkingDir: "{app}"; IconFilename: "{app}\images\easyconduite32.ico"; Tasks: desktopicon

