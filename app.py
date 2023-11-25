# app.py

from flask import Flask, render_template
from flask_socketio import SocketIO
from pymongo import MongoClient
import uuid

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)

# Connect to MongoDB
client = MongoClient('mongodb://localhost:27017/')
db = client['users_details']
collection = db['user_data']

@app.route('/')
def index():
    return render_template('index.html')

@socketio.on('message')
def handle_message(data):
    print('Received data:', data)

    # If the data contains a unique ID, update the existing data
    if '_id' in data:
        existing_data = collection.find_one({'_id': data['_id']})
        if existing_data:
            # Update the existing data with the new values
            collection.update_one({'_id': existing_data['_id']}, {'$set': data})

            # Emit the updated data with the original unique ID
            updated_data = {**existing_data, **data}
            socketio.emit('updateResponse', updated_data)
        else:
            print(f"Data with ID {data['_id']} not found for update.")
    else:
        # Generate a unique ID
        data['_id'] = str(uuid.uuid4())

        # Store data in MongoDB
        collection.insert_one(data)

        # Emit the data with the unique ID
        socketio.emit('message', data)

@socketio.on('update')
def handle_update(data):
    unique_id = data['uniqueId']
    user_data = data['data']

    print('Updating data for ID:', unique_id)

    # Retrieve the existing data for the given unique ID
    existing_data = collection.find_one({'_id': unique_id})

    if existing_data:
        # Update the existing data with the new values
        collection.update_one({'_id': existing_data['_id']}, {'$set': user_data})

        # Emit the updated data with the original unique ID
        updated_data = {**existing_data, **user_data}
        socketio.emit('updateResponse', updated_data)
    else:
        print(f"Data with ID {unique_id} not found for update.")

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=5000, debug=True)
