
import sbt._

class BarberShopProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject  {

  val specs = "org.scala-tools.testing" % "specs_2.8.1" % "1.6.7" % "test"
  val scalaTest = "org.scalatest" % "scalatest" % "1.2" %  "test"



  /*
val akkaStm = akkaModule("stm")
  val akkaTypedActor = akkaModule("typed-actor")
val akkaRemote = akkaModule("remote")
val akkaHttp = akkaModule("http")
val akkaAmqp = akkaModule("amqp")
val akkaCamel = akkaModule("camel")
val akkaCamelTyped = akkaModule("camel-typed")
val akkaSpring = akkaModule("spring")
val akkaJta = akkaModule("jta")
val akkaCassandra = akkaModule("persistence-cassandra")
val akkaMongo = akkaModule("persistence-mongo")
val akkaRedis = akkaModule("persistence-redis")
*/

}
