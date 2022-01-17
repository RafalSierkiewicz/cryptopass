#!/bin/bash

CONTRACT_PATH="contracts/password-manager"

docker run -v $PWD/$CONTRACT_PATH:/sources ethereum/solc:stable -o /sources --abi --bin /sources/PasswordManager.sol
sbt "web/runMain io.crypto.pass.services.blockchain.Generator"