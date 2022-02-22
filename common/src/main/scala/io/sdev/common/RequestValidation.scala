package io.sdev.common

import cats.data._
import cats.implicits._

type ValidationResult[T] = Either[NonEmptyList[FieldError], T]
type FieldValidationResult = Option[FieldError]

case class FieldError(field: String, message: String)

trait RequestValidator[T] {
  def validate(model: T): ValidationResult[T]
}

trait FieldValidator[T] {
  def validate(field: T, name: String): FieldValidationResult
}

case object NonEmpty extends FieldValidator[String] {
  override def validate(field: String, name: String): FieldValidationResult = {
    if (field.isEmpty) FieldError(name, "Field is empty").some else None
  }
}

case class WithLength(min: Int, max: Int) extends FieldValidator[String] {
  override def validate(field: String, name: String): FieldValidationResult = {
    if (field.length < min || field.length > max) FieldError(name, "Field execceds expected length").some else None
  }
}
