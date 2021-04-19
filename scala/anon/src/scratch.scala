import io.github.kag0.ninny.ast._
import io.github.kag0.ninny._

object Main extends App {

  /*

{
  "userSchema": [
    "name",
    "age",
    { "address": ["street", "zip"] },
    { "siblings": "userSchema", "type": "array" },
    { "mother": "userSchema" }
  ]
}

{
  type: array/object/value,
  name: schema
}
   */

  lazy val userSchema: SeatingChart = SeatingChart(
    Seq(
      Value("name"),
      Value("age"),
      SeatingChart.named(
        "address",
        Seq(
          Value("street"),
          Value("zip")
        )
      ),
      Array.named("siblings", Left(Ref("userSchema"))),
      Ref.named("mother", "userSchema")
    )
  )

  def anon(
      schema: Schema,
      value: JsonValue,
      schemas: Map[String, SeatingChart]
  ): JsonValue =
    (value, schema) match {
      case (_, Ref(schemaName)) if schemas.contains(schemaName) =>
        anon(schemas(schemaName), value, schemas)

      case (_, _: Value) => value
      case (value: JsonObject, s: SeatingChart) =>
        JsonArray(s.schema.map {
          case n: Named =>
            value.values
              .get(n.name)
              .map(js => anon(n, js, schemas))
              .getOrElse(JsonObject(Map.empty))
          case _ => JsonObject(Map.empty)
        })

      case (JsonArray(values), Array(Right(s))) =>
        JsonArray(values.map(anon(s, _, schemas)))

      case (JsonArray(values), Array(Left(Ref(schemaName))))
          if schemas.contains(schemaName) =>
        JsonArray(values.map(anon(schemas(schemaName), _, schemas)))

      case (_, Array(Left(Ref(schemaName)))) => value
      case _                                 => JsonObject(Map.empty)
    }

  type Id[A] = A

  sealed trait DeAnonReturn[-S, R[_]] {
    def pure[A](a: A): R[A]
  }
  implicit val returnNamed = new DeAnonReturn[Named, Id] {
    def pure[A](a: A) = a
  }
  implicit val returnChart = new DeAnonReturn[SeatingChart, Id] {
    def pure[A](a: A) = a
  }
  implicit val returnDep = new DeAnonReturn[Deprecated, Option] {
    def pure[A](a: A) = None
  }

  def deanon[S <: Schema, R[_]](
      schema: S,
      value: JsonValue,
      schemaIndex: Map[String, SeatingChart]
  )(implicit
      ret: DeAnonReturn[S, R]
  ): R[JsonValue] =
    (value, schema) match {
      case (JsonArray(values), SeatingChart(schemas)) =>
        ret.pure(
          JsonObject(
            schemas
              .zip(values)
              .collect {
                case (s: Named, js) if js != JsonObject(Map.empty) =>
                  s.name -> deanon(s, js, schemaIndex)
              }
              .toMap
          )
        )

      case (JsonArray(values), Array(Right(s))) =>
        ret.pure(
          JsonArray(
            values.map(
              deanon[SeatingChart, Id](s, _, schemaIndex)
            )
          )
        )

      case (JsonArray(values), Array(Left(Ref(schemaName))))
          if schemaIndex.contains(schemaName) =>
        ret.pure(
          JsonArray(
            values.map(
              deanon[SeatingChart, Id](schemaIndex(schemaName), _, schemaIndex)
            )
          )
        )

      case (_, Ref(schemaName)) if schemaIndex.contains(schemaName) =>
        ret.pure(deanon(schemaIndex(schemaName), value, schemaIndex))

      case _ => ret.pure(value)
    }

  val json =
    obj(
      "name"    -> "jimmy",
      "address" -> obj("street" -> 123, "zip" -> 456),
      "siblings" -> arr(
        obj("name" -> "John", "address" -> obj("zip" -> 92130)),
        obj("name" -> "Jane")
      ),
      "mother" -> obj("name" -> "mom")
    )

  val schemas = Map("userSchema" -> userSchema)

  val anond = anon(userSchema, json, schemas)
  println(anond)
  println(deanon(userSchema, anond, schemas))
}

sealed trait Schema
sealed trait Named {
  def name: String
}

case object Deprecated extends Schema

case class Array(schema: Either[Ref, SeatingChart]) extends Schema
object Array {
  def named(_name: String, schema: Either[Ref, SeatingChart]) = new Array(
    schema
  ) with Named {
    def name = _name
  }
}

case class Ref(schemaName: String) extends Schema
object Ref {
  def named(_name: String, schema: String) = new Ref(schema) with Named {
    def name = _name
  }
}

case class Value(name: String) extends Schema with Named

case class SeatingChart(schema: Seq[Schema]) extends Schema
object SeatingChart {
  def named(_name: String, schema: Seq[Schema]) =
    new SeatingChart(schema) with Named {
      def name = _name
    }
}
