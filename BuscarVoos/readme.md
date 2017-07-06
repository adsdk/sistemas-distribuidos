1)Instalar MongoDB conforme documentação:
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

2)Importar CSV de carriers, airports e ontime (ano desejado):
http://christiano.me/importando-um-csv-no-mongodb-com-mongoimport/

ex: mongoimport -d sisdis -c ontime --type csv --headerline --file 2002.csv
    mongoimport -d sisdis -c carriers --type csv --headerline --file carriers.csv
    mongoimport -d sisdis -c airports --type csv --headerline --file airports.csv

Acessar via terminal o shell do mongo. (comando mongo --shell)
  Normalizar campos para uso facilitado de seus objetos no JAVA:
    db.carriers.updateMany({}, {$rename:{"Code": "code"}});
    db.carriers.updateMany({}, {$rename:{"Description": "description"}});
    db.airports.updateMany({}, {$rename:{"long": "lng"}});
  Criar índices para as collections:
    db.ontime.createIndex({"ArrDelay": -1})
    db.ontime.createIndex({"DepDelay": -1})
    db.ontime.createIndex({"Year": 1})
    db.ontime.createIndex({"Month": 1})
    db.ontime.createIndex({"DayofMonth": 1})


COMANDOS ÚTEIS:
iniciar mongodb: sudo service mongod start
