@echo off

set OPENFAST_HOME=%~dp0%..
if not "%JAVA_HOME%" == "" goto GotJava
echo Please set the JAVA_HOME environment variable to the install location of a valid JRE.
exit /B 1

:GotJava

if exist "%JAVA_HOME%\bin\java.exe" goto JavaGood
echo The JAVA_HOME environment variable is not set correctly.
exit /B 1

:JavaGood

set JAVAEXE="%JAVA_HOME%\bin\java"
set CP=%OPENFAST_HOME%\${project.artifactId}-${project.version}.jar;%OPENFAST_HOME%\lib\xstream-1.3.jar;%OPENFAST_HOME%\lib\xpp3_min-1.1.4c.jar
set MAIN=org.openfast.examples.xml.FastToXmlMain

%JAVAEXE% -classpath "%CP%" %MAIN% %*
