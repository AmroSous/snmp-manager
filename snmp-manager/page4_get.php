<?php

header('Content-Type: application/json');

$agent = filter_input(INPUT_GET, 'agent', FILTER_VALIDATE_IP, FILTER_FLAG_IPV4) ? filter_input(INPUT_GET, 'agent') : '127.0.0.1';
$community = filter_input(INPUT_GET, 'community', FILTER_SANITIZE_STRING) ?: 'public';

// Base OID for SNMP Statistics
$baseOID = '1.3.6.1.2.1.11';

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

$snmpStats = [];

try {
    foreach ($snmpStatisticNames as $index => $name) {
        $fullOID = "$baseOID.$index".".0";
        $value = snmp2_get($agent, $community, $fullOID);

        if ($value === false) {
            throw new Exception("Unable to fetch SNMP Statistics for OID $fullOID.");
        }

        $valueClean = trim($value, 'Counter32: ');
        $snmpStats[] = [
            'oid' => $index, 
            'name' => $name,
            'value' => $valueClean
        ];
    }

    echo json_encode(['status' => 'success', 'snmpStatistics' => $snmpStats]);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
