# Web Bank


A simple web application in Clojure for managing bank accounts.

## Installation

This project uses **Leiningen** to build and manage its dependencies. Before continuing, make sure you have [Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html) version 1.6 or later installed. You can check your Java version by running the command:

    $ java -version


Then install Leiningen using the instructions on [Leiningen home page](https://leiningen.org/).

The code here presented has been developed and tested on Linux (Ubuntu 14.04), with Leiningen 2.7.1 on JRE 1.8 version.


## Usage

After cloning the repository, first navigate to the root project directory and download all project dependencies.

    $ lein deps

After which you can run the server locally.

    $ lein run

The application runs on port 5000. So any POST requests must be made to *localhost:5000*.


## Routes

All HTTP endpoints accept and return JSON payloads as requests and responses.

Endpoint   | Bank Operation
---------- | ------------------
/transactions | Adds a credit or debit transaction to a given account
/balances | Gets current balance of a given account
/statements | Gets bank account statement for a given period
/debts | Gets the periods where balances of given account has been negative


## Requests

[Here](https://github.com/DanLux/nubank/tree/master/resources/requests-examples) you have json examples for all requests available.

For a credit<sup>[1](https://github.com/DanLux/nubank/blob/master/resources/requests-examples/credit-transactions.json)</sup> or debit<sup>[1'](https://github.com/DanLux/nubank/blob/master/resources/requests-examples/debit-transactions.json)</sup> transaction, current account balance<sup>[2](https://github.com/DanLux/nubank/blob/master/resources/requests-examples/balances.json)</sup>, account statement<sup>[3](https://github.com/DanLux/nubank/blob/master/resources/requests-examples/statements.json)</sup> and periods of debt<sup>[4](https://github.com/DanLux/nubank/blob/master/resources/requests-examples/debts.json)</sup>.

> All values in the examples are arbitrary.
One must only pay attention to the json string keys and the json value types and formats.


## Responses

[Here](https://github.com/DanLux/nubank/tree/master/resources/responses-examples) you have json examples for the expected responses.

For a credit or debit transaction<sup>[1](https://github.com/DanLux/nubank/blob/master/resources/responses-examples/transactions.json)</sup>, current account balance<sup>[2](https://github.com/DanLux/nubank/blob/master/resources/responses-examples/balances.json)</sup>, account statement<sup>[3](https://github.com/DanLux/nubank/blob/master/resources/responses-examples/statements.json)</sup> and periods of debt<sup>[4](https://github.com/DanLux/nubank/blob/master/resources/responses-examples/debts.json)</sup>.


## Testing

You can run all project tests with the command:

    $ lein test

And a report with all results will be automatically displayed.


## License

Distributed under the Eclipse Public License.
