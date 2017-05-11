# RingChat
RingChat is a command line interface, client-server chat program that utilizes the Socket API. This was an assignment for CS4850 at the University of Missouri.

## Assignment Overview
In this project, you will implement a chat room that includes
multiple clients and a server that utilizes the socket API. The socket API is implemented in many
programming languages.
The client program provides commands: login (allow users to join the chat room), send (unicast
or broadcast a message; actually send the message to the server and the server forwards the
message), logout (quit the chat room), and who (list all the clients in the chat room).
The server runs a chat room service, manages all the clients and distributes the messages.

## Usage

### Starting the Server
- Edit server/config.properties (localhost by default)
- Compile RingServer.java: `javac server/RingServer.java`
- Start the server: `java RingServer`

### Starting the Client
- Edit client/config.properties (localhost by default)
- Compile RingClient.java `javac client/RingClient.java`
- Start the client: `RingClient.java`

### Client Commands
- login `login <username> <password>`
- logout `logout`
- Create a new user account `newuser <username> <password>`
- Broadcast message to entire chatroom `send all <message>`
- Send private message `send <username> <message>`
- See online users `who`
