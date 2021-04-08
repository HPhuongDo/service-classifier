import numpy as np
import scipy.spatial
import re

# path1 = '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/keywords_user-db.txt' 
path1 = '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/keywords_mysql.txt' 
path2 = '/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/keywords_mongo.txt' 

with open(path1, 'r') as file:
    file1 = file.read().split()

with open(path2, 'r') as file:
    file2 = file.read().split()

gloveFile = "vectors_db.txt"
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

def cosine_distance_wordembedding_method(file1, file2):
    vector_1 = np.mean([model.get(word, np.zeros(50)) for word in file1],axis=0)
    vector_2 = np.mean([model.get(word, np.zeros(50)) for word in file2],axis=0)
    cosine = scipy.spatial.distance.cosine(vector_1, vector_2)
    print('GloVe Embedding with Cosine Similarity: ',round((1-cosine)*100,2),'%')



model = loadGloveModel(gloveFile)
cosine_distance_wordembedding_method(file1, file2)