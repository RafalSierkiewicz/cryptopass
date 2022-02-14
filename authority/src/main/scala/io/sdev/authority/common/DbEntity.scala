package io.sdev.authority.common

import doobie.util.{Get, Put}

trait DbEntity {
  opaque type Id = Int

  object Id {
    def apply(i: Int): Id = i
  }
  extension (id: Id) {
    def value: Int = id
  }

  given dbGet: Get[Id]
  given dbPut: Put[Id]
}
