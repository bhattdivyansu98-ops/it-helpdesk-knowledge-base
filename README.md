# IT Help Desk Knowledge Base

Searchable knowledge base for first-level IT support, built with **Java Swing, MySQL and JDBC**.

## Features
- 10 ready step-by-step fixes: Wi-Fi, printer, password reset, software installation, slow PC, shared drive, boot issues
- Keyword search (multi-word, matches title, keywords and steps)
- Category filter: Network, Printer, Account, Software, Windows, Other
- Add, edit and delete articles
- Customer-friendly troubleshooting steps with escalation notes

## Run
1. Run `schema.sql` in MySQL (creates `helpdesk_kb` with sample articles).
2. Set DB password if it is not `root`:
   - Windows: `set DB_PASS=yourpassword`
   - Linux/Mac: `export DB_PASS=yourpassword`
3. `mvn compile exec:java`

Or in an IDE: add `mysql-connector-j` jar to the project and run `com.helpdesk.KnowledgeBaseApp`.

## Tech
Java 11+, Swing, JDBC (PreparedStatement), MySQL 8
