#!/bin/bash
# Build common-lib first (dependency for all services)
mvn -pl common-lib install -DskipTests

# Build all services
mvn -pl auth-service,inventory-service,procurement-service package -DskipTests