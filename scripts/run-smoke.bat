@echo off
call mvn clean test "-Dcucumber.filter.tags=@smoke"
exit /b %errorlevel%
