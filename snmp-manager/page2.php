<?php

header('Content-Type: application/json');

// Fetching query parameters securely
$agent = filter_input(INPUT_GET, 'agent', FILTER_VALIDATE_IP, FILTER_FLAG_IPV4) ? 
        filter_input(INPUT_GET, 'agent') : '127.0.0.1:161';
$communityR = filter_input(INPUT_GET, 'communityR', FILTER_SANITIZE_STRING) ?: 'public';

// Base OID for TCP connection table entries
$baseOid = '.1.3.6.1.2.1.6.13.1';

// Mapping for the TCP connection table attributes we're interested in
$tcpAttributes = [
    'tcpConnState' => 1,
    'tcpConnLocalAddress' => 2,
    'tcpConnLocalPort' => 3,
    'tcpConnRemAddress' => 4,
    'tcpConnRemPort' => 5,
];

$tcpTable = [];
foreach ($tcpAttributes as $attribute => $index) {
    $oid = "$baseOid.$index";
    $entries = snmp2_walk($agent, $communityR, $oid);
    foreach ($entries as $i => $value) {
        $tcpTable[$i][$attribute] = trim($value, '"');
    }
}

echo json_encode(array_values($tcpTable)); // Use array_values to re-index the array starting from 0
