@echo off
call mvn clean test
exit /b %errorlevel%
