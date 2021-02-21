class CommonWords:
   def find(self, s0, s1):
      s0 = s0.lower()
      s1 = s1.lower()
      s0List = s0.split(" ")
      s1List = s1.split(" ")
      return list(set(s0List)&set(s1List))
cw = CommonWords()
with open('logs/mysql/mysql_queries.log', 'r') as file:
    mysql_log = file.read().replace('\n', '')
with open('logs/postgres/postgres.log', 'r') as file:
    postgres_log = file.read().replace('\n', '')
with open('logs/mongo/mongo_queries.log', 'r') as file:
    mongo_log = file.read().replace('\n', '')

print(cw.find(mysql_log, postgres_log))
print(cw.find(mysql_log, mongo_log))
print(cw.find(mongo_log, postgres_log))
print(mongo_log.find("query"))