#!/bin/bash

CONTRACT_PATH="cryptopass-web/src/main/resources/solidity"

docker run -v $PWD/$CONTRACT_PATH:/sources ethereum/solc:stable -o /sources --abi --bin /sources/PasswordManager.sol