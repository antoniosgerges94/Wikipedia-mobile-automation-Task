@echo off
where allure >nul 2>&1 || (echo Allure CLI is not on PATH & exit /b 1)
allure generate target\allure-results --clean -o target\allure-report
if errorlevel 1 exit /b %errorlevel%
echo Report: target\allure-report\index.html
