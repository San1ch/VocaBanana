@echo off
REM --------------------------------
REM VocaBanana Feature Generator
REM --------------------------------

REM Git Bash terminal    ./feature_creator.bat <feature_name> <include_room>
REM Arguments \/

REM Arguments:
REM %1 - Feature name (e.g., Text)
REM %2 - Include Room (true/false)
set FEATURE=%1
set INCLUDE_ROOM=%2

REM Enable delayed expansion
setlocal enabledelayedexpansion

REM --------------------------------
REM Convert feature name to lowercase for folder names (Rem Converter Feature)
REM --------------------------------
set "FEATURE_LC=%FEATURE%"
for %%A in (%FEATURE%) do (
    set "FEATURE_LC=%%A"
)
REM Use Git Bash-compatible lowercase conversion
for /f %%L in ('echo %FEATURE_LC% ^| tr A-Z a-z') do set "FEATURE_LC=%%L"

REM --------------------------------
REM Source and Destination folders
REM --------------------------------
set TEMPLATE_DIR=templates\feature_template_name
set DEST_DIR=app\src\main\java\com\example\vocabanana\feature\%FEATURE_LC%
if not exist "%DEST_DIR%" mkdir "%DEST_DIR%"

REM --------------------------------
REM Copy template recursively
REM --------------------------------
xcopy "%TEMPLATE_DIR%\*" "%DEST_DIR%\" /E /I /Y >nul

REM --------------------------------
REM Pause briefly to avoid file locks in Git Bash
REM --------------------------------
ping -n 2 127.0.0.1 >nul

REM --------------------------------
REM Rename DAO, Entity, RepositoryModule, RepositoryImpl, Repository, Domain files
REM --------------------------------
for /R "%DEST_DIR%" %%f in (*.*) do (
    if /I "%%~nxf"=="Dao.kt" ren "%%f" "%FEATURE%Dao.kt"
    if /I "%%~nxf"=="Entity.kt" ren "%%f" "%FEATURE%Entity.kt"
    if /I "%%~nxf"=="RepositoryModule.kt" ren "%%f" "%FEATURE%RepositoryModule.kt"
    if /I "%%~nxf"=="RepositoryImpl.kt" ren "%%f" "%FEATURE%RepositoryImpl.kt"
    if /I "%%~nxf"=="Repository.kt" ren "%%f" "%FEATURE%Repository.kt"
    if /I "%%~nxf"=="Domain.kt" ren "%%f" "%FEATURE%Domain.kt"
)

REM --------------------------------
REM Replace <feature> and <low_feature> in all files
REM Using Git Bash sed-compatible approach
REM --------------------------------
for /R "%DEST_DIR%" %%f in (*.*) do (
    REM Using temporary file to avoid Windows file lock issues
    sed -i "s/<feature>/%FEATURE%/g" "%%f"
    sed -i "s/<low_feature>/%FEATURE_LC%/g" "%%f"
)

REM --------------------------------
REM Remove Room folders if INCLUDE_ROOM=false
REM --------------------------------
if /I "%INCLUDE_ROOM%"=="false" (
    if exist "%DEST_DIR%\data\local" rmdir /S /Q "%DEST_DIR%\data\local"
    if exist "%DEST_DIR%\data\module" rmdir /S /Q "%DEST_DIR%\data\module"
    if exist "%DEST_DIR%\data\repository" rmdir /S /Q "%DEST_DIR%\data\repository"
    if exist "%DEST_DIR%\domain\Repository" rmdir /S /Q "%DEST_DIR%\domain\Repository"
)

echo Feature %FEATURE% created successfully at %DEST_DIR%
endlocal