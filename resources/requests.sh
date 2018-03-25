#!/bin/bash

REQUESTS=requests-examples


credit() {
  json=${REQUESTS}/credit-transactions.json
  echo -e "Sending request: `cat $json`\n"
	response=`curl -X POST -d @"$json" localhost:5000/transactions --header "Content-Type:application/json";`
  echo -e "\nReceived response: $response"
}

debit() {
  json=${REQUESTS}/debit-transactions.json
  echo -e "Sending request: `cat $json`\n"
	response=`curl -X POST -d @"$json" localhost:5000/transactions --header "Content-Type:application/json";`
  echo -e "\nReceived response: $response"
}

balance() {
  json=${REQUESTS}/balances.json
  echo -e "Sending request: `cat $json`\n"
	response=`curl -X POST -d @"$json" localhost:5000/balances --header "Content-Type:application/json";`
  echo -e "\nReceived response: $response"
}

statement() {
  json=${REQUESTS}/statements.json
  echo -e "Sending request: `cat $json`\n"
	response=`curl -X POST -d @"$json" localhost:5000/statements --header "Content-Type:application/json";`
  echo -e "\nReceived response: $response"
}

debts() {
  json=${REQUESTS}/debts.json
  echo -e "Sending request: `cat $json`\n"
	response=`curl -X POST -d @"$json" localhost:5000/debts --header "Content-Type:application/json";`
  echo -e "\nReceived response: $response"
}

