<?php

header('Content-Type: application/json');

// Initialize variables to hold the parameters
$agent = '127.0.0.1:161'; // Default value
$communityR = 'public'; // Default value
$communityRW = 'publicRW'; // Default value

// SNMP Object OID's for the System Group, excluding System Services
$oids = [
    'sysDescr' => '.1.3.6.1.2.1.1.1.0',
    'sysObjectID' => '.1.3.6.1.2.1.1.2.0',
    'sysUpTime' => '.1.3.6.1.2.1.1.3.0',
    'sysContact' => '.1.3.6.1.2.1.1.4.0',
    'sysName' => '.1.3.6.1.2.1.1.5.0',
    'sysLocation' => '.1.3.6.1.2.1.1.6.0',
];

// Check the request method
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Fetch parameters from POST body
    $postData = json_decode(file_get_contents('php://input'), true);
    
    $agent = isset($postData['agentIp']) ? $postData['agentIp'] : $agent;
    $communityR = isset($postData['communityR']) ? $postData['communityR'] : $communityR;
    $communityRW = isset($postData['communityRW']) ? $postData['communityRW'] : $communityRW;

    try {
        if (isset($postData['sysContact'])) {
            snmp2_set($agent, $communityRW, $oids['sysContact'], 's', $postData['sysContact']);
        }
        if (isset($postData['sysName'])) {
            snmp2_set($agent, $communityRW, $oids['sysName'], 's', $postData['sysName']);
        }
        if (isset($postData['sysLocation'])) {
            snmp2_set($agent, $communityRW, $oids['sysLocation'], 's', $postData['sysLocation']);
        }

        echo json_encode(['message' => 'System group updated successfully.', 'status' => 'OK']);
    } catch (Exception $e) {
        echo json_encode(['message' => 'Server Error while updating data.', 'status' => 'NO']);
    }
    
    exit;
} else {
    // Fetch parameters from URL (GET request)
    $agent = filter_input(INPUT_GET, 'agentIp', FILTER_VALIDATE_IP, FILTER_FLAG_IPV4) ? 
            filter_input(INPUT_GET, 'agentIp') : $agent;
    $communityR = filter_input(INPUT_GET, 'communityR', FILTER_SANITIZE_STRING) ?: $communityR;
    $communityRW = filter_input(INPUT_GET, 'communityRW', FILTER_SANITIZE_STRING) ?: $communityRW;
}

$results = [];

foreach ($oids as $key => $oid) {
    $value = snmp2_get($agent, $communityR, $oid);
    if ($value === false) {
        $results[$key] = 'Unable to fetch';
    } else {
        // Removing the leading and trailing " and spaces
        $results[$key] = trim($value, " \t\n\r\0\x0B\"");
    }
}

echo json_encode($results);
