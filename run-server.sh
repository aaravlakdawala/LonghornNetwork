#!/bin/bash
# Run WebSocket Server with all dependencies

cd "$(dirname "$0")"

echo "🚀 Starting WebSocket Server..."
echo "Building classpath..."

# Create classpath from target/lib and target/classes
CLASSPATH="target/classes"
for jar in target/lib/*.jar; do
  CLASSPATH="$CLASSPATH:$jar"
done

echo "Classpath: $CLASSPATH"
echo ""
echo "Starting Java process..."
echo "---"

java -cp "$CLASSPATH" WebSocketServer

echo "---"
echo "Server stopped"
