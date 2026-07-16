#!/bin/sh

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P) || exit 1
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD=java
fi

if ! command -v "$JAVACMD" >/dev/null 2>&1 && [ ! -x "$JAVACMD" ]; then
    echo "ERROR: No se encontró Java. Configure JAVA_HOME con JDK 21." >&2
    exit 1
fi

exec "$JAVACMD" -Dfile.encoding=UTF-8 -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
