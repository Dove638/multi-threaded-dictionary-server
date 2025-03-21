# Multi-threaded Dictionary Server

This project is a client-server dictionary system implemented in Java using sockets and multithreading. It was developed for the COMP90015 Distributed Systems course.

## 🔧 Overview

The system consists of:
- A **multi-threaded server** that handles dictionary operations
- A **graphical client** that allows users to connect to the server and perform dictionary queries

All communication between the client and server is done over **reliable TCP sockets**, and concurrency is handled explicitly using **Java threads**.

## 📚 Features

The client can perform the following operations:
- **Query** the meaning(s) of a word
- **Add** a new word with one or more meanings
- **Remove** a word and all of its meanings
- **Add meaning** to an existing word (no duplicates)
- **Update meaning** by replacing an existing one

All updates are shared — if one client modifies the dictionary, other clients will see the changes.

## 🖥️ User Interface

A GUI is provided for the client using Java Swing. It allows users to:
- Enter the word and related information
- Select operations (e.g. query, add, update)
- View results or errors in a text area

## ⚙️ Implementation Notes

- **Server** loads the initial dictionary from a file and stores data in memory.
- **Thread-per-client model**: Each client is handled in its own thread using a `ClientHandler`.
- **Custom protocol**: Messages between client and server are serialized as JSON.
- **Error handling** is implemented for all common failure cases:
  - Network issues
  - Missing files
  - Invalid inputs

## 🚀 Running the Project

### 🖥️ Server
```bash
java -jar DictionaryServer.jar <port> <dictionary-file>

### 🖥️ Client
```bash
java -jar DictionaryClient.jar <server-address> <port>


## Repo Directory
Multi-threaded Dictionary Server/
├── client/                  # Client-side code and GUI
├── server/                  # Server-side logic
├── data/                    # Dictionary input file
├── jars/                    # Compiled .jar files for submission
├── report/                  # PDF report
├── zip_submission/          # Zipped source files for LMS
└── README.md                # You're reading it!
