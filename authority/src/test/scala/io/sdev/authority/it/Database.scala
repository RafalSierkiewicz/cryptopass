package io.sdev.authority.it

import org.testcontainers.containers.PostgreSQLContainer
import cats.effect.IO
import doobie._
import doobie.implicits._
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import cats.effect.kernel.Resource
import doobie.util.transactor.Transactor
import munit.CatsEffectSuite
import org.flywaydb.core.Flyway
import cats.syntax.apply

trait DatabaseMock extends CatsEffectSuite {

  case class MockedDb(dbName: String, driver: String, url: String, user: String, password: String)

  var xa: Transactor[IO] = null
  var onclose: IO[Unit] = null
  var mock: MockedDb = null

  override def beforeAll(): Unit = {
    val transactorWithClean = transactor.allocated
    val transactorWithDb = transactorWithClean.map(_._1).unsafeRunSync()
    xa = transactorWithDb._1
    mock = transactorWithDb._2
    onclose = transactorWithClean.flatMap(_._2)
  }

  override def afterAll(): Unit = {
    onclose.unsafeRunSync()
  }

  override def munitFixtures = Seq(dropAndRecreate)

  private val dropAndRecreate = new Fixture[Unit]("dropAndRecreate") {
    def apply(): Unit = ()
    override def beforeEach(context: BeforeEach): Unit = {
      (for {
        create <- sql"CREATE SCHEMA public".update.run.transact(xa)
        migrated <- migrate(mock.url, mock.user, mock.password)
      } yield migrated).unsafeRunSync()
    }
    override def afterEach(context: AfterEach): Unit = {
      sql"DROP SCHEMA public CASCADE".update.run.transact(xa).unsafeRunSync()
    }
  }

  private val transactor: Resource[IO, (Transactor[IO], MockedDb)] = for {
    db <- Resource.make(IO {
      val container: PostgreSQLContainer[Nothing] = new PostgreSQLContainer("postgres:14")
      container
    })(c => IO(c.stop()))
    _ <- Resource.eval(IO(db.start))
    ec <- ExecutionContexts.fixedThreadPool[IO](32)
    ht <- HikariTransactor
      .newHikariTransactor[IO](db.getDriverClassName, db.getJdbcUrl, db.getUsername, db.getPassword, ec)
    cleandb <-
      Resource.eval(sql"DROP SCHEMA public CASCADE".update.run.transact(ht))

  } yield (ht, MockedDb(db.getDatabaseName, db.getDriverClassName, db.getJdbcUrl, db.getUsername, db.getPassword))

  private def migrate(url: String, user: String, password: String) = {
    for {
      migrations <- IO.blocking(
        Flyway
          .configure(getClass.getClassLoader)
          .dataSource(url, user, password)
          .load
      )
      status <- IO.blocking(migrations.migrate).map(_.success)
    } yield status
  }
}
