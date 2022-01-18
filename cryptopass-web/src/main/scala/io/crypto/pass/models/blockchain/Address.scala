package io.crypto.pass.models.blockchain

object Address:
    opaque type Address = String

    extension(a: Address)
        def solidityAddress: String = a
