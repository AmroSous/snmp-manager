<?php

$page = isset($_GET['page']) ? $_GET['page'] : 'page1';

function fetchData($url) {
  
    $curl = curl_init();

    curl_setopt($curl, CURLOPT_URL, $url); // Set the URL to fetch
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, true); // Return the transfer as a string
    curl_setopt($curl, CURLOPT_HEADER, false); // Don't include the header in the output
    
    $response = curl_exec($curl);

    if (curl_errno($curl)) {
        curl_close($curl);
        return [];
    }

    curl_close($curl);

    $data = json_decode($response, true);

    return $data;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && $page === 'page1') {
    $sysName = $_POST['sysName'] ?? '';
    $sysLocation = $_POST['sysLocation'] ?? '';
    $sysContact = $_POST['sysContact'] ?? '';

    // Prepare the data as an array and then json_encode it
    $postData = json_encode([
        'sysName' => $sysName,
        'sysLocation' => $sysLocation,
        'sysContact' => $sysContact,
    ]);

    $ch = curl_init('http://localhost/snmp-manager/page1.php');
    
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    // Indicate that you're sending JSON data
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']); // Important

    $response = curl_exec($ch);
    
    if (curl_errno($ch)) {
        error_log(curl_error($ch));
    }

    curl_close($ch);

    // Redirect or handle response as needed
    header("Location: http://localhost/snmp-manager/index.php?page=page1");
    exit();
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SNMP Manager</title>
    <link rel="stylesheet" href="style.css" />
</head>
<body>

<ul class="navigation-bar">
    <li onclick="window.location.href='http://localhost/snmp-manager/index.php?page=page1'">System Group</li>
    <li onclick="window.location.href='http://localhost/snmp-manager/index.php?page=page2'">TCP Table</li>
    <li onclick="window.location.href='http://localhost/snmp-manager/index.php?page=page3'">UDP Table</li>
    <li onclick="window.location.href='http://localhost/snmp-manager/index.php?page=page4'">SNMP Statistics</li>
</ul>

<div id="content">
    <?php
    
    switch ($page) {
        case 'page1':
            
            $data = fetchData('http://localhost/snmp-manager/page1.php');
            echo "<div class='form-container'><form method='POST' action='http://localhost/snmp-manager/index.php?page1.php'>";

            foreach ($data as $key => $value) {
                $cleanedValue = preg_replace('/^[^:]+: /', '', $value, 1);
                if (strpos($cleanedValue, '"\"') === 0) {
                    $cleanedValue = substr($cleanedValue, 3);
                }
                echo "<div class='form-group'>";
                if (in_array($key, ['sysContact', 'sysName', 'sysLocation'])) {
                    echo "<label for='{$key}'>{$key}</label><input type='text' name='{$key}' value='{$cleanedValue}' class='editable'>";
                } else {
                    echo "<label>{$key}</label><p>{$cleanedValue}</p>";
                }
                echo "</div>";
            }
            echo "<input type='submit' value='Update'>";
            echo "</form></div>";
            break;
            
        case 'page2':
            echo "<h2>TCP Table</h2>";

            $tcpTableData = fetchData('http://localhost/snmp-manager/page2.php');
    
            if (!empty($tcpTableData)) {
                echo "<table>";
                $headers = array_keys($tcpTableData[0]);
                echo "<thead><tr>";
                foreach ($headers as $header) {
                    echo "<th>" . htmlspecialchars($header) . "</th>";
                }
                echo "</tr></thead>";

                echo "<tbody>";
                foreach ($tcpTableData as $row) {
                    echo "<tr>";
                    foreach ($row as $cell) {
                        echo "<td>" . htmlspecialchars($cell) . "</td>";
                    }
                    echo "</tr>";
                }
                echo "</tbody>";
                echo "</table>";
            } else {
                echo "<p>No TCP Table data available.</p>";
            }

            break;

        case 'page3':
            echo "<h2>UDP Table</h2>";

            $udpTableData = fetchData('http://localhost/snmp-manager/page3.php')["arpEntries"];

            if (!empty($udpTableData)) {
                echo "<table>";
                
                $headers = array_keys($udpTableData[0]);
                echo "<thead><tr>";
                foreach ($headers as $header) {
                    echo "<th>" . htmlspecialchars($header) . "</th>";
                }
                echo "</tr></thead>";

                echo "<tbody>";
                foreach ($udpTableData as $row) {
                    echo "<tr>";
                    foreach ($row as $cell) {
                        echo "<td>" . htmlspecialchars($cell) . "</td>";
                    }
                    echo "</tr>";
                }
                echo "</tbody>";
                echo "</table>";
            } else {
                echo "<p>No UDP Table data available.</p>";
            }

            break;

        case 'page4':
            echo "<h2>SNMP Statistics</h2>";

            $dataGet = fetchData('http://localhost/snmp-manager/page4_get.php')["snmpStatistics"];
            $dataWalk = fetchData('http://localhost/snmp-manager/page4_walk.php')["snmpStatistics"];

            echo "<div style='display: flex; justify-content: space-around;'>"; // Flex container

            echo "<div>"; 
            echo "<h3>By-Get Method</h3>";
            echo "<table><thead><tr><th>OID</th><th>Object Name</th><th>Value</th></tr></thead><tbody>";
            foreach ($dataGet as $item) {
                echo "<tr><td>{$item['oid']}</td><td>{$item['name']}</td><td>{$item['value']}</td></tr>";
            }
            echo "</tbody></table>";
            echo "</div>"; 

            echo "<div>"; 
            echo "<h3>By-Walk Method</h3>";
            echo "<table><thead><tr><th>Index</th><th>Object Name</th><th>Value</th></tr></thead><tbody>";
            foreach ($dataWalk as $item) {
                echo "<tr><td>{$item['#item']}</td><td>{$item['name']}</td><td>{$item['value']}</td></tr>";
            }
            echo "</tbody></table>";
            echo "</div>"; 

            echo "</div>";

            break;

        default:
            echo "<p>Select an option from the navigation bar.</p>";
            break;
    }
    ?>
</div>

</body>
</html>
