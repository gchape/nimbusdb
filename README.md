# NimbusDB

NimbusDB is a lightweight, educational database system designed to provide a hands-on experience in understanding and building database systems. Inspired by the concepts in _CSCI 3357: Database System Implementation_ at **Boston College**, NimbusDB helps users explore the fundamental components and functionality of a database system.

## What It Supports

NimbusDB is built with a focus on simplicity and educational value. It supports the following core features:

- [x] **1. Storage Management**
   - NimbusDB manages data storage at the block level, simulating how an OS handles physical data storage. Data is addressed in blocks, read/write operations are handled by `FileMgr` and data is tranfered through pages.
     `Page` has support for strings(charset=`US_ASCII`) as well as for _short_, _int_, _long_, _float_ and _double_ primitive types.

     ![Untitled-2025-03-21-1507](https://github.com/user-attachments/assets/c517ba41-813a-453c-8082-7b7760c5081c)

- [ ] **2. Transaction Management**
   - NimbusDB includes basic support for transactions, ensuring that operations on the database are executed in a way that maintains data consistency. It handles commit and rollback mechanisms.

- [ ] **3. Indexing**
   - The database includes simple indexing mechanisms to help speed up query operations. It supports basic index structures, such as B+ trees, to demonstrate how indexing can optimize data retrieval and enhance performance in database systems.

- [ ] **4. Basic Querying**
   - NimbusDB allows users to perform basic querying operations, such as retrieving data from tables, based on simple conditions. This enables users to understand how queries are processed and executed within a database system.

- [ ] **5. SQL-like Operations**
   - While NimbusDB is not a full-fledged SQL database, it supports a subset of SQL-like operations, including:
     - **Insertions**: Adding new records to tables.
     - **Deletions**: Removing records from tables.
     - **Updates**: Modifying existing records in tables.
     - **Select Queries**: Retrieving data based on specific conditions.

- [ ] **6. Transaction Logs**
   - NimbusDB implements transaction logging to ensure durability and recoverability. Every transaction is logged, which enables the system to recover to a consistent state in the event of a failure.

## Features

- Lightweight and easy-to-understand architecture.
- Simple API to interact with the database for educational purposes.
- Transactional support with commit and rollback.
- Basic indexing to improve query performance.
- Custom data types for storing and manipulating records.
- Focus on understanding the inner workings of database management systems.

### Note
I hope to finish implementing the remaining features of NimbusDB soon, including transaction management, indexing, basic querying, and SQL-like operations. Once completed, these features will provide a more comprehensive and hands-on experience, to better understand the key components and workings of a database system. 

_If I don’t finish it this semester, I’ll consider myself a bit of a loser, but I’m determined to get it done!_

### Prerequisites

- Java 21 or higher.
- Maven (for building with Maven).

### Cloning the Repository

Clone this repository to your local machine:

```bash
https://github.com/gchape/nimbusdb.git
