#!/bin/sh

usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Options:"
    echo "  -url, --url VALUE        Target MCP server URL (Required)"
    echo "  -u,   --username VALUE   Basic auth username (Required)"
    echo "  -p,   --password VALUE   Basic auth password (Required)"
    echo "  -h,   --help             Show this help message"
    exit 1
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        -url|--url)
            url="$2"
            shift 2
            ;;
        -u|--username)
            username="$2"
            shift 2
            ;;
        -p|--password)
            password="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Error: Invalid option '$1'"
            usage
            ;;
    esac
done

if [[ -z "$url" ]] || [[ -z "$username" ]] || [[ -z "$password" ]]; then
    echo "Error: Missing required arguments."
    usage
fi

encoded_credentials=$(printf "%s:%s" "${username}" "${password}" | base64)

# Only necessary if using stateful protocol.  If enabled, use the -H "Mcp-Session-Id: $mcp_session_id" header in subsequent requests
#echo "Initializing MCP session..."
#mcp_session_id=$(curl -sS "$MCP_URL" -H "Content-Type: application/json" -H "Accept: application/json, text/event-stream" -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-11-25","capabilities":{},"clientInfo":{"name":"spring-ai-mcp-client","version":"1.0.0"}}}' 2>&1 | grep "Mcp-Session-Id:" | awk '{print $NF}' | xargs)
#echo "Initialized MCP session $mcp_session_id"

echo "Calling MCP server at $url...\n"

echo "Available MCP tools:"
curl -sS "$url" -H "Content-Type: application/json" -H "Authorization: Basic $encoded_credentials" -H "Accept: application/json, text/event-stream" -d '{"jsonrpc":"2.0","id": 1,"method": "tools/list","params": {}}' | jq '.result.tools'

echo "\n"

current_date=$(date -I)

for target in "BLUESKY" "TWITTER"
do
  echo "$target events for $current_date:"
  curl -sS "$url" -H "Content-Type: application/json" -H "Authorization: Basic $encoded_credentials" -H "Accept: application/json, text/event-stream" -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"get_events","arguments":{"dateString":"'"$current_date"'","target":"'"$target"'"}}}' | jq '.result.content.[].text'
  echo "\n"
done