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
test_service "identity-service" "security" "8100"
test_service "authorization-service" "security" "8107"
test_service "audit-service" "security" "8106"
test_service "patient-service" "patient" "8081"
test_service "admission-service" "patient" "8084"
test_service "emergency-service" "patient" "8085"
test_service "ward-service" "patient" "8086"
test_service "bed-service" "patient" "8087"
test_service "round-service" "patient" "8088"
test_service "surgery-service" "patient" "8089"
test_service "ambulance-service" "patient" "8100"
test_service "appointment-service" "appointment" "8082"
test_service "medical-record-service" "emr" "8083"
test_service "imaging-service" "emr" "8093"
test_service "billing-service" "billing" "8094"
test_service "insurance-service" "billing" "8095"
test_service "payment-service" "billing" "8096"
test_service "pharmacy-service" "pharmacy" "8091"
test_service "prescription-service" "pharmacy" "8090"
test_service "laboratory-service" "laboratory" "8092"
test_service "hr-service" "hr" "8101"
test_service "scheduling-service" "hr" "8102"
test_service "inventory-service" "inventory" "8097"
test_service "procurement-service" "inventory" "8098"
test_service "asset-service" "inventory" "8099"
test_service "event-service" "infrastructure" "8103"
test_service "notification-service" "infrastructure" "8104"
test_service "reporting-service" "reporting" "8105"

echo "Test complete. Results saved to $RESULT_FILE"
