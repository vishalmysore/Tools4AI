#!/bin/bash

# Assuming Maven generates XML test reports in the "target/surefire-reports" directory
TEST_REPORT_DIR="target/surefire-reports"
# Print current directory
echo "Current directory: $(pwd)"

xml_files=$(find "$(pwd)" -name "TEST-*.xml")

# Concatenate all XML files and count the occurrences of "<testcase" using grep
test_count=$(cat $xml_files | grep -c "<testcase")
echo "Total number of tests: $test_count"




echo "GIST_ID: $GIST_ID"


json_data='{"schemaVersion": "1", "label": "testcount", "message": "'$test_count'", "color": "orange"}'

echo "JSON data: $json_data"

# Prevent any output from the script
exec > /dev/null
exec 2>&1

# Create or update the Gist with the test count
curl -s -X PATCH \
     -H "Authorization: token $GIST_TOKEN" \
     -H "Content-Type: application/vnd.github+json" \
     -d '{"files":{"test.json":{"content": "{\"schemaVersion\": 1,\"label\": \"testcount\", \"message\": \"'$test_count'\", \"color\":\"orange\"}" }}}' \
     "https://api.github.com/gists/$GIST_ID"