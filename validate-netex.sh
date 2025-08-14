#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

if [[ ! -f "target/classes/org/entur/netex/validation/cli/NetexValidatorCLI.class" ]]; then
    echo "Building project..."
    mvn compile -q || { echo "ERROR: Build failed"; exit 1; }
fi

CLASSPATH="target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)"
java -cp "$CLASSPATH" org.entur.netex.validation.cli.NetexValidatorCLI "$@"
