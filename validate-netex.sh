#!/bin/bash

# NetEx Validator CLI Script

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

JAR_FILE=$(find "$SCRIPT_DIR/target" -name "netex-validator-java-*-shaded.jar" -type f -print -quit 2>/dev/null)

if [ ! -f "$JAR_FILE" ]; then
    echo "Shadow jar not found. Build with 'mvn clean package -DskipTests'"
    exit 1
fi

java -jar "$JAR_FILE" "$@"
