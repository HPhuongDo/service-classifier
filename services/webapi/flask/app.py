import os
from flask import Flask, jsonify
app = Flask(__name__)

@app.route('/')
def heartbeat():
    # return a json
    print("Receiving a heartbeat message")
    return jsonify('Heartbeat successful!')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port='5000')
