Task 3: ATM Interface
A console-based banking simulation built with **Core Java** using an Object-Oriented architecture across 5 distinct classes (`ATM`, `Account`, `Transaction`, `Bank`, and `Main`)[cite: 3].

Key Features
* **PIN Authentication:** Validates User ID and PIN on startup, locking access after 3 consecutive failed attempts[cite: 3].
* **Banking Operations:** Interactive `switch-case` menu supporting:
  1. **Transaction History:** Displays a timestamped log of all deposits, withdrawals, and transfers stored in an `ArrayList`[cite: 3].
  2. **Withdraw:** Validates available funds before deducting amounts and prevents overdrafts with an `"Insufficient Funds"` alert[cite: 3].
  3. **Deposit:** Credits funds to the account and logs the updated balance[cite: 3].
  4. **Transfer:** Validates recipient account ID and sender balance before transferring funds between accounts[cite: 3].
  5. **Quit:** Safely exits the ATM session[cite: 3].
* **OOP Design:** Demonstrates encapsulation, separation of concerns, and clean class relationships[cite: 3].

Tech Stack
* **Language:** Java (Console Application)
* **Core Concepts:** Object-Oriented Programming (Encapsulation), Java Collections (`ArrayList`, `HashMap`), `java.time`
