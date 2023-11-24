// Sample server using Express, Socket.IO, and MongoDB

const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const mongoose = require('mongoose');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

// Connect to MongoDB
mongoose.connect('mongodb://localhost:27017/your-database-name', { useNewUrlParser: true, useUnifiedTopology: true });

// Define a MongoDB schema and model without the 'heading' field
const dataSchema = new mongoose.Schema({
    number: String,
    name: String
});

const DataModel = mongoose.model('Data', dataSchema);

// Socket.IO connection
io.on('connection', (socket) => {
    console.log('A user connected');

    // Listen for the submitData event
    socket.on('submitData', (number, name) => {
        // Save data to MongoDB
        const newData = new DataModel({
            number: number,
            name: name
        });

        newData.save()
            .then(() => {
                // Emit a response back to the client
                socket.emit('serverResponse', 'Data saved successfully');
            })
            .catch((error) => {
                console.error('Error saving data:', error);
                // Emit an error response back to the client
                socket.emit('serverResponse', 'Error saving data');
            });
    });

    // Disconnect event
    socket.on('disconnect', () => {
        console.log('A user disconnected');
    });
});

const PORT = 3000;
server.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
