# ServiceClassifier
- uses StanfordNLP to extract keywords
- calculates (Java and external Python):
    - Jaccard Similarity
    - TF-IDF Cosine Similarity
    - GloVe Cosine Similarity
    - Weighted Jaccard Similarity using keywords generated from Latent Semantic Indexing ("database" topic)

## To start the services and logging (in the background)
$ ./start.sh

## To stop the servies and logging
$ ./stop.sh

## To send CRUD queries to the databases for logging purposes
- Install mongo, mysql-client, psql
- Run ./services/databases/queries.sh

## Run ServiceClassifier: Find common words
- Change the path in Main accordingly   // TODO: change to relative path
- Run Main to find common words

The first finder (common_words.py) only split the words using white spaces. This finder is more thorough!
