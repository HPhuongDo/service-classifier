#! /bin/bash

# Queries to MongoDB
# db.createCollection('cities')
# db.getCollectionNames()
# db.cities.insert({ name: 'New York', country: 'USA' })
# db.cities.insert({ name: 'Berlin', country: 'France' })
# db.cities.find()
# db.cities.updateOne(
#    { name: "Berlin" },
#    {$set: { "country": "Germany"}}
# )
# db.cities.deleteOne( { name: "Berlin" } )
# db.cities.drop()
function mongo_queries {
    mongo <<EOF
    db.cities.find();
EOF
}

function mysql_queries {
    # mysql --defaults-file=mysql_config.cnf -h 127.0.0.1 -P 3306 mydb
    mysql -h 127.0.0.1 -P 3306 -u root --password=password mydb <<EOF
    USE mydb;
    SELECT * FROM cities;
EOF
}

function postgres_queries {
    sudo -u postgres psql <<EOF
    \c mydb;
    SELECT * FROM cities;
EOF
}

echo "Start sending queries to databases..."
# mongo_queries
mysql_queries
echo "Finished testing databases."



