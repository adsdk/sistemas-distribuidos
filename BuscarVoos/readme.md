<h3>1)Instalar MongoDB conforme documentação:</h3> <br/>
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/ <br/>

<h3>2)Importar CSV de carriers, airports e ontime (ano desejado):</h3> <br/>
http://christiano.me/importando-um-csv-no-mongodb-com-mongoimport/ <br/>

ex: <br/>
#mongoimport -d sisdis -c ontime --type csv --headerline --file 2002.csv <br/>
#mongoimport -d sisdis -c carriers --type csv --headerline --file carriers.csv <br/>
#mongoimport -d sisdis -c airports --type csv --headerline --file airports.csv <br/>

<h3>3)Acessar via terminal o shell do mongo. (comando mongo --shell)</h3> <br/>
    <p>Inicialmente selecionar a database: comando "use sisdis"</p>
<h4>Normalizar campos para uso facilitado de seus objetos no JAVA:</h4> <br/>
    <p>db.carriers.updateMany({}, {$rename:{"Code": "code"}}); </p>
    <p>db.carriers.updateMany({}, {$rename:{"Description": "description"}}); </p>
    <p>db.airports.updateMany({}, {$rename:{"long": "lng"}}); </p>
<h4>Criar índices para a collection ontime:</h4> <br/>
    <p>db.ontime.createIndex({"ArrDelay": -1}); </p>
    <p>db.ontime.createIndex({"DepDelay": -1}); </p>
    <p>db.ontime.createIndex({"Year": 1}); </p>
    <p>db.ontime.createIndex({"Month": 1}); </p>
    <p>db.ontime.createIndex({"DayofMonth": 1}); </p>

<br/><br/>
COMANDOS ÚTEIS: <br/>
iniciar mongodb: sudo service mongod start <br/>
