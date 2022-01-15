package io.crypto.pass.controllers

import zhttp.http._
import zhttp.endpoint._

import zio._

class PasswordController {
  private val passPath = "passwords"
  def getByIdx = Method.GET / passPath / *[String] to { idx =>
    Response.text(s"Got response ${idx.params}")
  }

  def routes = getByIdx
}
