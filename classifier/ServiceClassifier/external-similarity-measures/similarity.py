from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import sys

import numpy as np
import scipy.spatial
import re

import os.path
from gensim import corpora
from gensim.models import LsiModel
from gensim.models.coherencemodel import CoherenceModel
import matplotlib.pyplot as plt

#####################################################
## TF-IDF Cosine Similarity

def tfidf():
    """
    Input: log files
    """
    with open(sys.argv[1], 'r') as file:
        file1 = re.sub('\\x1b\\[[0-9;]*m', ' ', file.read().replace('\n', ' ')).lower()
    with open(sys.argv[2], 'r') as file:
        file2 = re.sub('\\x1b\\[[0-9;]*m', ' ', file.read().replace('\n', ' ')).lower()

    # Create TfidfVectorizer object
    vectorizer = TfidfVectorizer()

    # Generate matrix of word vectors
    tfidf_matrix = vectorizer.fit_transform([file1, file2])

    # compute and print the cosine similarity matrix
    cosine_sim = cosine_similarity(tfidf_matrix, tfidf_matrix)
    print(cosine_sim[0][1])


#####################################################
## GloVe Cosine Similarity

# path1 = '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/keywords_mysql.txt'
# path2 = '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/keywords_mongo.txt'

def loadGloveModel(gloveFile):
    print ("Loading Glove Model")
    with open(gloveFile, encoding="utf8" ) as f:
        content = f.readlines()
    model = {}
    for line in content:
        splitLine = line.split()
        word = splitLine[0]
        embedding = np.array([float(val) for val in splitLine[1:]])
        model[word] = embedding
    print ("Done.",len(model)," words loaded!")
    return model

def cosine_distance_between_two_words(word1, word2):
    return (1- scipy.spatial.distance.cosine(model[word1], model[word2]))

def cosine_distance_wordembedding_method(model, file1, file2):
    vector_1 = np.mean([model.get(word, np.zeros(50)) for word in file1],axis=0)
    vector_2 = np.mean([model.get(word, np.zeros(50)) for word in file2],axis=0)
    cosine = scipy.spatial.distance.cosine(vector_1, vector_2)
    print('GloVe Embedding with Cosine Similarity: ',1 - cosine)

def glove():
    """
    Input: BoW of log files (not stemmed)
    """
    with open(sys.argv[1], 'r') as file:
        file1 = file.read().split()
    with open(sys.argv[2], 'r') as file:
        file2 = file.read().split()
    gloveFile = "vectors_db.txt"
    model = loadGloveModel(gloveFile)
    cosine_distance_wordembedding_method(model, file1, file2)

#####################################################
## Latent Semantic Indexing
def prepare_corpus(files):
    # Creating the term dictionary of our corpus, where every unique term is assigned an index
    dictionary = corpora.Dictionary(files)
    # Converting list of documents (corpus) into Document Term Matrix using dictionary prepared above
    doc_term_matrix = [dictionary.doc2bow(doc) for doc in files]
    # generate LDA model
    return dictionary,doc_term_matrix

def create_gensim_lsa_model(files,num_topics,num_keywords):
    dictionary,doc_term_matrix=prepare_corpus(files)
    # generate LSI model
    lsimodel = LsiModel(doc_term_matrix, num_topics=num_topics, id2word = dictionary)  # train model
    topics = lsimodel.print_topics(num_topics=num_topics, num_words=num_keywords)
    print(topics[0])
    return lsimodel

def lsi():
    """
    Input: BoW of log files (not stemmed)
    Output: keywords of type database
    """
    files = []  ## list of tokenized log files
    # import glob
    # allfiles = glob.glob('/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/*.txt')
    # for file in allfiles:
    #     f = open(file, 'r')
    #     files.append(f.read().split())
    
    paths = ['/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/BoW_carts-db.txt',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/BoW_orders-db.txt',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/BoW_user-db.txt']
    for path in paths:
        with open(path, 'r') as file:
            files.append(file.read().split())
    num_topics = 1  # number of topics
    num_keywords = 20   # number of generated keywords
    model = create_gensim_lsa_model(files, num_topics, num_keywords)

    import csv
    with open('/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/categories/database/weighted-keywords_db.csv', 'w', newline='') as csv_file:
        writer = csv.writer(csv_file,delimiter=';')
        data = model.show_topic(0, num_keywords)
        for row in data:
            writer.writerow(row)

def report(classifier, actual, predictions):
    from sklearn.metrics import classification_report, accuracy_score
    print(classifier)
    
    actual = np.array(actual)
    
    print(classification_report(actual, predictions))
    print("Accuracy: " + str(round(accuracy_score(actual, predictions),2)))

def log_format_classifier(algorithm):
    """
    Supervised training to recognize log format
    Input: log files
    Output: 
    """
    import pandas as pd
    from sklearn.pipeline import Pipeline
    from sklearn.feature_extraction.text import CountVectorizer
    from sklearn.feature_extraction.text import TfidfTransformer
    from sklearn.model_selection import train_test_split

    log_collection = pd.DataFrame()  ## list of log files with type
    logs = pd.DataFrame()
    spring_paths = ['/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/docker-compose_carts_1.log',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/docker-compose_orders_1.log',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/docker-compose_shipping_1.log',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/springboot-reference-backend.log']
    for logfile in spring_paths:
        logs = pd.read_csv(logfile, sep="\n", header=None, names=['data'])
        logs['type'] = 'spring' # train spring backend log files
        log_collection = log_collection.append(logs)

    db_paths = ['/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/docker-compose_carts-db_1.log',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/docker-compose_orders-db_1.log',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/logs/microservices-startup/docker-compose_user-db_1.log',
            '/home/phuong/Documents/Master-thesis/implementation/services/databases/logs/mongo/mongo_startup.log',
            '/home/phuong/Documents/Master-thesis/implementation/services/databases/logs/mysql/mysql_startup.log']
    for logfile in db_paths:
        logs = pd.read_csv(logfile, sep="\n", header=None, names=['data'])
        logs['type'] = 'db' # train database log files
        log_collection = log_collection.append(logs)
    # Remove empty lines
    log_collection = log_collection.dropna()
    
    X_train, X_test, y_train, y_test = train_test_split(log_collection['data'], log_collection['type'], test_size=5, random_state=42)
    # Train
    model = Pipeline([('vect', CountVectorizer()), ('tfidf', TfidfTransformer()), ('clf', algorithm)])
    model.fit(X_train, y_train)
    # Test
    predictions = model.predict(X_test)
    print("X_test", X_test)
    print("PREDICTION", predictions)
    report((str(algorithm).split('(')[0]), y_test, predictions)


if __name__ == "__main__":
    from sklearn import naive_bayes
    from sklearn.naive_bayes import MultinomialNB
    log_format_classifier(naive_bayes.MultinomialNB())