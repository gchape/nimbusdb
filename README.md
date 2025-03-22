# NimbusDB

NimbusDB is a lightweight, educational database system designed to provide a hands-on experience in understanding and building database systems. Inspired by the concepts in _CSCI 3357: Database System Implementation_.

_If I don’t finish it this semester, I’ll be a loser!!!_

## What It Supports

NimbusDB is built with a focus on simplicity and educational value. It supports the following core features:

- [x] **1. Storage Management**
- NimbusDB manages data storage at the block level, simulating how an operating system handles physical data storage. Data is addressed in blocks, and read/write operations are managed by FileMgr, with data transferred through Pages. Each Page supports various data types, including strings (charset=US_ASCII), as well as primitive types like short, int, long, float, and double.
  
  ![Untitled-2025-03-21-1507](https://github.com/user-attachments/assets/a2a33922-8320-4d5b-851c-3a5a312b8b09)

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

## Summary

- Basic indexing to improve query performance.
- Transactional support with commit and rollback.
- Lightweight and easy-to-understand architecture.
- Simple API to interact with the database for educational purposes.
- Focus on understanding the inner workings of database management systems.

## References

1. **CS186 Berkeley - Notes on Database Systems** [Read more](https://cs186berkeley.net/notes/note5/)

### Cloning the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/gchape/nimbusdb.git
