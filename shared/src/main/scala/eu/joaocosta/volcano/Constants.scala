package eu.joaocosta.volcano

object Constants {
  val canvasWidth  = 320
  val canvasHeight = 180

  val tileSize = 16

  val speedMultiplier     = 2
  val animationMultiplier = 4

  val gravity          = 0.05
  val terminalVelocity = 1.0
  val jumpSpeed        = 2.0
  val acceleration     = 0.25
  val drag             = 0.2
  val maxSpeed         = 1.0

  val maximumTime   = 90 * 60 // 75 s
  val timeRecharge  = 15 * 60 // 15 s
  val rechargeSpeed = 5

  lazy val levels = List(
    Resources.introLevel,
    Resources.forestLevel,
    Resources.templeLevel,
    Resources.volcanoLevel
  )
}
