<?php

    include "../../accesseur/ScoreDAO.php";
    $ScoreDAO = new ScoreDAO();
    $score = new stdClass();

    $score->valeur= $_GET['valeur'];
    $score->utilisateur_id = $_GET['utilisateur_id'];

    $succes = $ScoreDAO->ajouterScore($score);

?>
<?php 
    header("Content-type: text/xml");
    echo '<?xml version="1.0" encoding="UTF-8"?>';
?>
<actions>
    <action>
	    <type>ajouter</type>
        <succes><?=$succes?></succes>
	    <message></message>
    </action>
</actions>
