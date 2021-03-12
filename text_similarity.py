from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import re
import sys

path = '/home/phuong/Documents/Master-thesis/implementation/'

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