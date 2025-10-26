@echo off
title LibraryFlow - Modern Library Management System
echo.
echo ========================================================
echo          Welcome to LibraryFlow
echo          Modern Library Management System
echo ========================================================
echo.
echo Starting LibraryFlow...
echo.

cd /d "%~dp0"
java -cp "classes;lib\sqlite-jdbc.jar" Main

echo.
echo LibraryFlow has closed.
echo Press any key to exit...
pause >nul