# Description
=============

A checking account from a bank allows for putting (deposits, salaries, credits)
or taking (purchases, withdrawals, debits) money at any given time.

You can also check what is your current balance or your account statement,
containing all operations that happened between two dates, along with the
account's daily balance.

While you should only be able to take an amount of money that was put there
previously, some banks give you a "free" credit line, so it can lend you some
money instantly, at a "reasonable" interest rate.

This exercise consists in implementing those basic features of a checking account:

- First step: adding the operations on that checking account

  Create a HTTP service in which you can add an operation to a given checking
  account, identified by the account number. This operation will contain the
  account number, a short description, a amount and the date it happened. Keep
  in mind you have to support both credit and debit operations, i.e, both
  putting and taking money out of the account.
  E.g:
    Deposit 1000.00 at 15/10
    Purchase on Amazon 3.34 at 16/10
    Purchase on Uber 45.23 at 16/10
    Withdrawal 180.00 at 17/10

  Deposits can take days to be acknowledged properly, so you should support
  insertion in any date order.

- Second step: Get the current balance

  Create a HTTP endpoint which returns the current balance of a given account.
  This balance is the sum of all operations until today, so the customer can
  know how much money they still have.

  E.g: for the sample above, the customer would have
  1000.00 - 3.34 - 45.23 - 180.00 = 771.43

- Third step: Get the bank statement

  Create an HTTP endpoint which returns the bank statement of an account given
  a period of dates. This statement will contain the operations of each day
  and the balance at the end of each day.

  E.g:
  15/10:
  - Deposit 1000.00
  Balance: 1000.00

  16/10:
  - Purchase on Amazon 3.34
  - Purchase on Uber 45.23
  Balance: 951.43

  17/10:
  - Withdrawal 180.00
  Balance: 771.43

- Fourth step: Compute periods of debt.

  Create a HTTP endpoint which returns the periods which the account's balance
  was negative, i.e, periods when the bank can charge interest on that account.

  E.g: if we have three more operations (current balance is 771.43):
  Purchase of a flight ticket 800.00 at 18/10
  Purchase of a espresso 10.00 at 22/10
  Deposit 100.00 at 25/10

  The endpoint would return:
  - Principal: 28.57
    Start: 18/10
    End: 21/10

  - Principal: 38.57
    Start: 22/10
    End: 24/10

  This endpoint should return multiple periods, if applicable, and omit the "End:"
  date if the account's balance is currently negative.

All HTTP endpoints should accept and return JSON payloads as requests and
responses. There is no need for HTML visualizations.

You should deliver a git repository, or a link to a shared private repository on
github, bitbucket or similar, with your code and a short README file outlining
the solution and explaining how to build and run the code. You should deliver
your code in a functional programming language — Clojure, Haskell, Elixir and Scala
are acceptable.

This is actually a problem we've already solved here, and we're giving you a
chance to present a solution. We will evaluate your code in a similar way that we usually evaluate
code that we send to production: as we rely heavily on automated tests and our CI tool to ship
code to production multiple times per day, having tests that make sure your code works is a must.
Also, pay attention to code organization and make sure it is readable and clean. You can aim for
simplicity now, so you shouldn't be worried if your code can handle thousands of connections, however
you need to make sure your code handles transactions in a concurrent-safe way.

We also consider that this might be your first tackle on a functional language, so it's ok
if your code is not idiomatic, but do try to program in a functional style. Feel free to ask any questions, but please note that we won't be able to give you
feedback about your code before your deliver. However, we're more than willing to help
you understanding the domain or picking a library, for instance.

You can persist the data in memory, so there is no need to use a database (these mentioned languages already have
data structures that provide concurrency-safe ways to store and transact data). But if you feel that using
a database can help your exercise, feel free to do so. We only ask you to not delegate the balance computation
to the database: we want to see how you solve the problem in a functional way.

We also don't want to make you reimplement what already exists, so feel free to use any libraries/frameworks
that are available as long you don't delegate the balance computation to them.

Lastly, there is no need to rush with the solution: delivering your exercise earlier than the due date
is not a criteria we take into account when evaluating the exercise: so if you finish earlier than that,
please take some time to see what you could improve. If you think the 2 weeks timeframe may not be enough
by any reason, don't hesitate to ask for more time.
