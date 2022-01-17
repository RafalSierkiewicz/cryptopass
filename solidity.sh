#!/bin/bash

CONTRACT_PATH="cryptopass-web/src/main/resources/solidity"

docker run -v $PWD/$CONTRACT_PATH:/sources ethereum/solc:stable -o $PWD/$CONTRACT_PATH --abi --bin /sources/PasswordManager.sol