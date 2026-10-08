# Credit Card Processing System (OOSE Mini Project)

Built from the mini project report (class diagram, modules in 4.1, tables in 4.2).

## java-app/  (Java console software)
Classes: Customer, CreditCard, Bank, Merchant, PaymentGateway, Payment, Transaction, Bill
Modules: PaymentService (validate -> fraud check -> authorize -> capture -> record),
         FraudDetector, ReceiptGenerator, ReportService, Main (menu)

Run in VS Code:
1. Install JDK 17+ and the "Extension Pack for Java".
2. File > Open Folder > this folder, open java-app/Main.java, click "Run".
   Or in the terminal:  cd java-app  ->  javac *.java  ->  java Main
3. Login with anything. Menu: register customer, add card, validate card,
   make payment, history, statement, bill, reports.

Test data: card 4111111111111111, expiry 12/28 (any future date), CVV 123, limit 1000.
- 4111111111111112 fails the Luhn check -> payment FAILED
- amount above available credit -> DECLINED
- amount above 40% of the limit (or 3+ payments in a minute) -> FLAGGED as suspicious

## database/schema.sql  (MySQL 8.0)
mysql -u root -p < database/schema.sql
(or open it in MySQL Workbench and run it). Creates all 7 tables + sample data.
The Java app keeps data in memory; JDBC wiring is the next step if you want persistence.

## website/  (browser demo of the screens)
Open website/index.html with the "Live Server" extension (or double-click it).

Demo only - no real payments are processed.
