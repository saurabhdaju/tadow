# tadow
This is a desktop application based on Client-Server Architecture that uses Java Socket Programming to enable real-time communication.

## Features
* Easy to set up and run
* Real-time communication between Client and Server
* User-friendly graphical interface

## Prerequisites
* Java Development Kit (JDK) installed on both Client and Server machines
* Both Client and Server should be connected to the same Wi-Fi network
* Client needs to know the Server's IPv4 address  
  
Obtain the Server's IPv4 address using the following command in the terminal:
```bash 
ipconfig
```

## How to Run
### Server Setup
1. Open the terminal on the Server machine and navigate to the project directory
2. Compile the `Server.java` file using the command:
   ```bash
   javac Server.java
   ```
3. Run the Server using the command:
   ```bash 
   java Server
   ```
   The Server application window will open and wait for Client to connect

### Client Setup
1. Ensure the Client machine is connected to the same Wi-Fi network as the Server.
2. Open the `Client.java` file in a text editor.
3. Find the following line of code:
   ```java 
   Socket socket = new Socket("127.0.0.1", 7777);
   ```
4. Replace `127.0.0.1` with the Server's IPv4 address.
5. Save the file.
6. Open the terminal on the Client machine and navigate to the project directory.
7. Compile the `Client.java` file using the command:
   ```bash 
   javac Client.java
   ```
8. Run the client using the command:
   ```bash 
   java Client
   ```
Once both the server and client applications are running, they will be connected, and you can start chatting!
