# DMH

Digital Money House (DMH) is designed to manage users, accounts, cards, and financial transactions in a
secure and scalable digital wallet system.

The system models financial operations through a structured Entity-Relationship design that reflects 
real-world constranints and banking rules.

##  Entity - Relation Diagram
![Diagrams/MER-DMH.png](https://github.com/RODRIGONAHUELKIRSCH/DMH/blob/main/Diagrams/MER-DMH.png)

## Relational Diagram
![Diagrams/MR-DMH.png](https://github.com/RODRIGONAHUELKIRSCH/DMH/blob/main/Diagrams/MR-DMH.png)

## User
Represents a registered user of the platform.

**Attributes:**
- id (PK)
- name
- lastname
- dni
- email
- password
- verified_email

A user may optionally verify their email address.  
The User entity acts as the root entity of the system.

---

## Account
Represents a digital wallet account.

**Attributes:**
- id (PK)
- cvu
- amount
- alias

An account:
- Belongs to exactly one user.
- Can perform multiple transactions.
- Can have multiple associated cards.

The CVU and alias provide two alternative mechanisms for transferring funds.

---

## Card
Represents a debit/credit card associated with an account.

**Attributes:**
- id (PK)
- card_number
- card_type
- card_company
- card_due_date

A card:
- Belongs to exactly one account.
- Can perform transactions independently from the account entity.

---

## Transactions
Represents financial operations within the system.

**Attributes:**
- id (PK)
- date
- type
- description
- amount
- state

The `state` attribute models the lifecycle of a transaction, allowing the system to manage asynchronous approval flows and business validations.

---

## Business Rules

- A User can have multiple Accounts (1:N).
- An Account belongs to exactly one User.
- An Account can perform multiple Transactions.
- A Card belongs to exactly one Account.
- A Card can perform multiple Transactions.
- A Transaction must be executed by either an Account or a Card.

---

## Design Decisions

### Card

Card was modeled as a separate entity instead of being embedded within Account because:

- It has its own lifecycle (expiration date, provider, type).
- It can independently perform transactions.
- It avoids overloading the Account entity.
- It maintains normalization principles.

---

### Transactions

Transactions were designed as an independent entity because:

- They represent immutable financial history.
- They centralize financial operations.
- Both Account and Card can generate transactions.

---

## Cardinality Decisions

Cardinalities were defined according to real-world financial constraints:

- An account cannot exist without a user.
- A card cannot exist without an account.
- A transaction must always belong to a single executor entity.
- A user may exist without having accounts.

---

## Model Limitations

- Currency type is not explicitly modeled (assumed to be Argentine pesos).
- Transaction fees are not represented.
- No joint accounts are supported.