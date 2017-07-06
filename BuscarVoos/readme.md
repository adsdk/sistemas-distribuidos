<h3>1)Instalar MongoDB conforme documentação:</h3> <br/>
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/ <br/>

<h3>2)Importar CSV de carriers, airports e ontime (ano desejado):</h3> <br/>
http://christiano.me/importando-um-csv-no-mongodb-com-mongoimport/ <br/>

ex: <br/>
#mongoimport -d sisdis -c ontime --type csv --headerline --file 2002.csv <br/>
#mongoimport -d sisdis -c carriers --type csv --headerline --file carriers.csv <br/>
#mongoimport -d sisdis -c airports --type csv --headerline --file airports.csv <br/>

<h3>3)Acessar via terminal o shell do mongo. (comando mongo --shell)</h3> <br/>
<h4>Normalizar campos para uso facilitado de seus objetos no JAVA:</h4> <br/>
    db.carriers.updateMany({}, {$rename:{"Code": "code"}}); <br/>
    db.carriers.updateMany({}, {$rename:{"Description": "description"}}); <br/>
    db.airports.updateMany({}, {$rename:{"long": "lng"}}); <br/>
<h4>Criar índices para as collections:</h4> <br/>
    db.ontime.createIndex({"ArrDelay": -1}) <br/>
    db.ontime.createIndex({"DepDelay": -1}) <br/>
    db.ontime.createIndex({"Year": 1}) <br/>
    db.ontime.createIndex({"Month": 1}) <br/>
    db.ontime.createIndex({"DayofMonth": 1}) <br/>


COMANDOS ÚTEIS: <br/>
iniciar mongodb: sudo service mongod start <br/>
