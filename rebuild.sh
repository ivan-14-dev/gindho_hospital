#!/bin/bash
echo "🔄 Rebuilding GinDHO project..."

# Clean everything
rm -rf backend/target
rm -rf javafx-client/target

# Verify all Java files exist
echo "Checking entity files..."
ls -la backend/src/main/java/com/gindho/model/*.java

echo "Checking repository files..."
ls -la backend/src/main/java/com/gindho/repository/*.java

echo "Checking service files..."
ls -la backend/src/main/java/com/gindho/service/*.java

echo "✅ Files verified"
