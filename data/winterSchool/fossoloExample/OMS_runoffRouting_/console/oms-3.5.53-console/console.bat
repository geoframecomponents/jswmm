@echo off
setlocal

set dist=%~dp0

if exist "%dist%\jre\bin\javaw.exe" goto privatejre
set java=javaw
goto fi2
:privatejre
set java=%dist%\jre\bin\javaw.exe
:fi2

rem --------------------------------------------------------------------------
rem Notes about the Java[tm] command line:
rem
rem * Replace 'start "" /b "%java%"' by 'start java' if you need to see
rem   Java exception stack traces and other low-level error messages
rem   printed on the console.
rem
rem * Add -Dsun.java2d.d3d=false as workaround for possible
rem   display driver bugs.
rem --------------------------------------------------------------------------

start "" /b "%java%" -client -Xms128M -Xms512M -Dsun.java2d.d3d=false -jar oms.console.jar %*
