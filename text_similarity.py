from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

path = '/home/phuong/Documents/Master-thesis/implementation/'

with open(path+'services/databases/logs/mysql/mysql_queries.log', 'r') as file:
    file1 = file.read().replace('\n', ' ').lower()
with open(path+'services/databases/logs/mongo/mongo_queries.log', 'r') as file:
    file2 = file.read().replace('\n', ' ').lower()

# Create TfidfVectorizer object
vectorizer = TfidfVectorizer()

# Generate matrix of word vectors
tfidf_matrix = vectorizer.fit_transform([file1, file2])

# compute and print the cosine similarity matrix
cosine_sim = cosine_similarity(tfidf_matrix, tfidf_matrix)
print(cosine_sim[0][1])