<?php

include_once 'dao/IDao.php';
include_once 'classe/Position.php';
include_once 'connexion/Connexion.php';

class PositionService implements IDao {
    private $listPosition = array();
    private $connexion;
    private $position;

    public function __construct() {
        $this->connexion = new Connexion();
        $this->position = new Position("", "", "", "", "");
    }

    public function create($position) {
        $query = "INSERT INTO position (latitude, longitude, date, imei) VALUES ("
            . $position->getLatitude() . "," . $position->getLongitude() . ",'" . $position->getDate() . "','" . $position->getImei() . "')";
        $req = $this->connexion->getConnextion()->prepare($query);
        $req->execute() or die('SQL');
    }

    public function getAll() {
        $query = "select * from position";
        $req = $this->connexion->getConnextion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }

    public function delete($obj) {
        // Mettez votre logique de suppression ici
    }

    public function getById($obj) {
        // Mettez votre logique pour obtenir une position par ID ici
    }

    public function update($obj) {
        // Mettez votre logique de mise à jour ici
    }
}

$positionService = new PositionService();
$allPositions = $positionService->getAll();

foreach ($allPositions as $position) {
    echo "Latitude : " . $position['latitude'] . "<br>";
    echo "Longitude : " . $position['longitude'] . "<br>";
    echo "Date : " . $position['date'] . "<br>";
    echo "IMEI : " . $position['imei'] . "<br>";
    echo "<hr>";
}
