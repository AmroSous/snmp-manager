<?php

header('Content-Type: application/json');

$agent = filter_input(INPUT_GET, 'agent', FILTER_VALIDATE_IP, FILTER_FLAG_IPV4) ? filter_input(INPUT_GET, 'agent') : '127.0.0.1';
$community = filter_input(INPUT_GET, 'community', FILTER_SANITIZE_STRING) ?: 'public';

// OID for ARP Table. Adjust if necessary based on your target device's MIB
$arpTableOID = '1.3.6.1.2.1.4.22.1';

try {
    $arpTable = snmp2_real_walk($agent, $community, $arpTableOID);

    if (!$arpTable) {
        throw new Exception("Unable to fetch ARP Table.");
    }

    $arpEntries = [];
    foreach ($arpTable as $oid => $value) {
        $parts = explode('.', $oid);
        $ipAddress = implode('.', array_slice($parts, -4)); 
        $arpEntries[] = [
            'ip' => $ipAddress,
            'value' => trim($value, 'STRING: ')
        ];
    }

    echo json_encode(['status' => 'success', 'arpEntries' => $arpEntries]);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
