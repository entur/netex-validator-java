#!/bin/bash

# Simple NeTEx Validator Script
set -e

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Change to project directory
cd "$SCRIPT_DIR"

# Build if needed
if [[ ! -f "target/classes/org/entur/netex/validation/cli/NetexValidatorCLI.class" ]]; then
    echo "Building project..."
    mvn compile -q || { echo "ERROR: Build failed"; exit 1; }
fi

# Get classpath and run validator
CLASSPATH="target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)"
java -cp "$CLASSPATH" org.entur.netex.validation.cli.NetexValidatorCLI "$@"
