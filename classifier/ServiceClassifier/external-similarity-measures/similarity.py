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
    print(lsimodel.print_topics(num_topics=num_topics, num_words=num_keywords))
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
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/BoW_user-db.txt',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/BoW_mongo.txt',
            '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/BoW_mysql.txt']
    for path in paths:
        with open(path, 'r') as file:
            files.append(file.read().split())
    num_topics = 1  # number of topics
    num_keywords = 20   # number of generated keywords
    model = create_gensim_lsa_model(files, num_topics, num_keywords)

if __name__ == "__main__":
    lsi()