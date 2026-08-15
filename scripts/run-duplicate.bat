@echo off
call mvn clean test "-Dcucumber.filter.tags=@duplicate"
exit /b %errorlevel%
