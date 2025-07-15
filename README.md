# java-chat
Realtime CLI Chat App in Java

## Current State
Sockets might not be long lived bidirectional connections in the way that WebSockets are which is why I think my initial structure isn't working. Going forward I should use WebSockets or connect to the socket again everytime I use it.

## Goals
- Implement multithreading
- Implement a design pattern

## Functional Requirements
- Client can send messages to all other clients using the terminal
- Client will recieve messages and see incoming messages in realtime
- Client will see the total number of clients on the server
