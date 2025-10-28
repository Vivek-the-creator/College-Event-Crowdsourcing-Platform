#!/bin/bash

echo "Starting College Event Crowdsourcing Platform..."
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed or not in PATH."
    echo "Please install Maven and try again."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH."
    echo "Please install Java 17 or higher and try again."
    exit 1
fi

echo "Building the project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the error messages above."
    exit 1
fi

echo
echo "Starting the application..."
mvn javafx:run

if [ $? -ne 0 ]; then
    echo "Application failed to start. Please check the error messages above."
    exit 1
fi


