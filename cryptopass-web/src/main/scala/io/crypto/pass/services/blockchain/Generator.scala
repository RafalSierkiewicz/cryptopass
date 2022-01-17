package io.crypto.pass.services.blockchain

import org.web3j.codegen.SolidityFunctionWrapperGenerator
import java.io.File

object Generator extends App {
  val basePath = "contracts/password-manager/"

  generate(s"${basePath}PasswordManager.abi", s"${basePath}PasswordManager.bin", "PasswordManager")

  def generate(abi: String, binary: String, contractName: String) = {
    new SolidityFunctionWrapperGenerator(
      new File(binary),
      new File(abi),
      new File("src/main/java"),
      contractName,
      "io.crypto.pass.contracts",
      true,
      false,
      20
    ).generate()
  }
}
