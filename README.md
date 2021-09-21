# Service Similarity Prediction
- runs and retrieves logs of Docker containers in the script docker-run.sh
- calculates and evaluates the following measures in the Jupyter Notebook:
    - LSI-weighted Jaccard Similarity
    - TF-IDF Cosine Similarity
    - GloVe Cosine Similarity
    - TF-IDF Multinomial Naive Bayes Classifier
    - TF-IDF Decision Tree Classifier

## To start and stop the services and logging (in the background)
$ ./docker-run.sh {start|stop} relative-path-to-docker-compose.yml

## To send CRUD queries to the databases for logging purposes
- Install mongo, mysql-client, psql
- Run ./services/databases/queries.sh