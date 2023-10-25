<?php

if ($_SERVER["REQUEST_METHOD"] == "POST" || $_SERVER["REQUEST_METHOD"] == "GET") {
    include_once 'service/PositionService.php';
    create();
}

function create() {
    if (isset($_REQUEST['latitude']) && isset($_REQUEST['longitude']) && isset($_REQUEST['date']) && isset($_REQUEST['imei'])) {
        $latitude = $_REQUEST['latitude'];
        $longitude = $_REQUEST['longitude'];
        $date = $_REQUEST['date'];
        $imei = $_REQUEST['imei'];

        // Assurez-vous d'inclure la définition de la classe Position et PositionService ici
        // Remplacez les valeurs 1, $latitude, $longitude, $date, $imei par les valeurs appropriées
        // lors de la création de l'objet Position
        $ss = new PositionService();
        $ss->create(new Position(1, $latitude, $longitude, $date, $imei));
    } else {
        echo "Toutes les données requises ne sont pas fournies.";
    }
}
