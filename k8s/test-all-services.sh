#!/usr/bin/env bash
set -euo pipefail

RESULT_FILE="/media/ivan/Ultimate/Ivan/script/common/GinDHO/test-results/TEST-RESULTS.md"
: > "$RESULT_FILE"
echo "# Service Test Results" >> "$RESULT_FILE"
echo "" >> "$RESULT_FILE"
echo "Date: $(date)" >> "$RESULT_FILE"
echo "" >> "$RESULT_FILE"

# Function to test a service
test_service() {
    local name="$1"
    local ns="$2"
    local port="$3"
    local endpoint="/actuator/health"
    local status="UNKNOWN"

    echo "Testing $name in namespace $ns on port $port" | tee -a "$RESULT_FILE"
    echo "" >> "$RESULT_FILE"

    # Check pod status
    if kubectl get pod -n "$ns" -l app="$name" -o jsonpath='{.items[0].status.phase}' 2>/dev/null | grep -q "Running"; then
        status="RUNNING"
        echo "  Pod: ✅ RUNNING" | tee -a "$RESULT_FILE"
    else
        status="NOT RUNNING"
        echo "  Pod: ❌ NOT RUNNING" | tee -a "$RESULT_FILE"
        kubectl get pods -n "$ns" -l app="$name" 2>&1 | tee -a "$RESULT_FILE"
        echo "" >> "$RESULT_FILE"
        return
    fi

    # Get pod name
    local pod_name
    pod_name=$(kubectl get pod -n "$ns" -l app="$name" -o jsonpath='{.items[0].metadata.name}')

    # Try health endpoint
    if kubectl exec -n "$ns" "$pod_name" -- sh -c "wget -qO- http://localhost:$port$endpoint 2>/dev/null | head -c 200" 2>/dev/null; then
        echo "  Health: ✅ OK" | tee -a "$RESULT_FILE"
    else
        # Try root endpoint as fallback
        if kubectl exec -n "$ns" "$pod_name" -- sh -c "wget -qO- http://localhost:$port/ 2>/dev/null | head -c 200" 2>/dev/null; then
            echo "  Root: ✅ OK" | tee -a "$RESULT_FILE"
        else
            echo "  Health: ❌ FAILED" | tee -a "$RESULT_FILE"
            echo "    Logs:" >> "$RESULT_FILE"
            kubectl logs -n "$ns" "$pod_name" --tail=5 2>&1 | sed 's/^/      /' | tee -a "$RESULT_FILE"
        fi
    fi

    echo "" >> "$RESULT_FILE"
    echo "----------------------------------------" >> "$RESULT_FILE"
}

# Services to test
test_service "identity-service" "security" "9023"
test_service "authorization-service" "security" "9030"
test_service "audit-service" "security" "9029"
test_service "patient-service" "patient" "9004"
test_service "admission-service" "patient" "9007"
test_service "emergency-service" "patient" "9008"
test_service "ward-service" "patient" "9009"
test_service "bed-service" "patient" "9010"
test_service "round-service" "patient" "9011"
test_service "surgery-service" "patient" "9012"
test_service "ambulance-service" "patient" "9023"
test_service "appointment-service" "appointment" "9005"
test_service "medical-record-service" "emr" "9006"
test_service "imaging-service" "emr" "9016"
test_service "billing-service" "billing" "9017"
test_service "insurance-service" "billing" "9018"
test_service "payment-service" "billing" "9019"
test_service "pharmacy-service" "pharmacy" "9014"
test_service "prescription-service" "pharmacy" "9013"
test_service "laboratory-service" "laboratory" "9015"
test_service "hr-service" "hr" "9024"
test_service "scheduling-service" "hr" "9025"
test_service "inventory-service" "inventory" "9020"
test_service "procurement-service" "inventory" "9021"
test_service "asset-service" "inventory" "9022"
test_service "event-service" "infrastructure" "9026"
test_service "notification-service" "infrastructure" "9027"
test_service "reporting-service" "reporting" "9028"

echo "Test complete. Results saved to $RESULT_FILE"
