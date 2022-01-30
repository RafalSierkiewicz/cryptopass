package io.crypto.models

import org.web3j.protocol.core.methods.response.{Transaction => Web3jTransaction}
import java.math.BigInteger
import akka.actor.Actor
import akka.actor.typed.ActorRef

sealed trait CollectCommand
