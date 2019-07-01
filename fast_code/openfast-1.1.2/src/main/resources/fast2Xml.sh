#!/bin/sh

if [ ! $JAVA_HOME ]; then
echo "Please set the JAVA_HOME environment variable to point to a valid Java install location."
exit 1
fi

if [ ! -f $JAVA_HOME/bin/java ]; then
echo "Please set the JAVA_HOME environment variable to point to a valid Java install location."
exit 1
fi

OPENFAST_HOME=$( cd -P -- "$(dirname -- "$0")" && pwd -P)/..
JAVA="$JAVA_HOME/bin/java"

CLASSPATH=$OPENFAST_HOME/lib/xstream-1.3.jar:$OPENFAST_HOME/lib/xpp3_min-1.1.4c.jar:$OPENFAST_HOME/${project.artifactId}-${project.version}.jar
MAIN=org.openfast.examples.xml.FastToXmlMain
$JAVA $JAVA_OPTS -classpath $CLASSPATH $MAIN $*

