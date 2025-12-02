#!/bin/bash
# LonghornNetwork - Start Both Servers

echo "================================"
echo "LonghornNetwork Startup Script"
echo "================================"
echo ""
echo "This script will help you start both the Java backend and React frontend."
echo ""
echo "Step 1: Building the Java backend..."
cd c:/Users/aarav/LonghornNetwork
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Java build failed. Check compilation errors above."
    exit 1
fi

echo ""
echo "✅ Java build successful!"
echo ""
echo "Step 2: Starting React frontend..."
echo ""
echo "Open a NEW terminal window and run:"
echo "  cd c:/Users/aarav/LonghornNetwork/longhornnetwork-web"
echo "  npm install (if you haven't already)"
echo "  npm run dev"
echo ""
echo "Step 3: Starting Java backend..."
echo ""
echo "In another NEW terminal window, run:"
echo "  cd c:/Users/aarav/LonghornNetwork"
echo "  java -cp target/classes Main"
echo ""
echo "Step 4: Access the application"
echo "  Open your browser to http://localhost:5173"
echo ""
echo "================================"
