<?php

header('Content-Type: application/json');

$agent = filter_input(INPUT_GET, 'agent', FILTER_VALIDATE_IP, FILTER_FLAG_IPV4) ? filter_input(INPUT_GET, 'agent') : '127.0.0.1';
$community = filter_input(INPUT_GET, 'community', FILTER_SANITIZE_STRING) ?: 'public';

// Base OID for SNMP Statistics
$baseOID = '1.3.6.1.2.1.11';

// Mapping of SNMP Statistics names to their OID endings
$snmpStatisticNames = [
    '1' => 'snmpInPkts',
    '2' => 'snmpOutPkts',
    '3' => 'snmpInBadVersions',
    '4' => 'snmpInBadCommunityNames',
    '5' => 'snmpInBadCommunityUses',
    '6' => 'snmpInASNParseErrs',
    // 7
    '8' => 'snmpInTooBigs',
    '9' => 'snmpInNoSuchNames',
    '10' => 'snmpInBadValues',
    '11' => 'snmpInReadOnlys',
    '12' => 'snmpInGenErrs',
    '13' => 'snmpInTotalReqVars',
    '14' => 'snmpInTotalSetVars',
    '15' => 'snmpInGetRequests',
    '16' => 'snmpInGetNexts',
    '17' => 'snmpInSetRequests',
    '18' => 'snmpInGetResponses',
    '19' => 'snmpInTraps',
    '20' => 'snmpOutTooBigs',
    '21' => 'snmpOutNoSuchNames',
    '22' => 'snmpOutBadValues',
    // 23
    '24' => 'snmpOutGenErrs',
    '25' => 'snmpOutGetRequests',
    '26' => 'snmpOutGetNexts',
    '27' => 'snmpOutSetRequests',
    '28' => 'snmpOutGetResponses',
    '29' => 'snmpOutTraps',
    '30' => 'snmpEnableAuthenTraps'
];

try {
    // Fetch all SNMP statistics
    $stats = snmp2_walk($agent, $community, $baseOID);

    if (!$stats) {
        throw new Exception("Unable to fetch SNMP Statistics.");
    }

    $snmpStats = [];
    $ind = 0; 
    
    foreach ($stats as $index => $value) {
        
        $oidIndex = $index + 1;
        
        if (array_key_exists((string)$oidIndex, $snmpStatisticNames)) {
            $name = $snmpStatisticNames[(string)$oidIndex];
            
            $valueClean = trim($value, 'Counter32: ');
            $snmpStats[] = [
                '#item' => $ind,
                'name' => $name,
                'value' => $valueClean
            ];
            $ind++;
        }
    }

    echo json_encode(['status' => 'success', 'snmpStatistics' => $snmpStats]);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
