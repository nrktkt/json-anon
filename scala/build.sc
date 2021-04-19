import mill._, scalalib._

object anon extends ScalaModule {
  def scalaVersion = "2.13.5"

  def ivyDeps = Agg(ivy"io.github.kag0::ninny:0.2.10")
}
