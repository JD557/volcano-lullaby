package eu.joaocosta.volcano

import eu.joaocosta.minart.input._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._

final case class GameState(player: GameState.Player, level: Level) {

  lazy val cameraPosition = {
    val indendedX = player.x - (Constants.tileSize / 2) - (Constants.canvasWidth / 2)
    val indendedY = player.y.toInt - (Constants.tileSize) - (Constants.canvasHeight / 2)
    (math.max(0,math.min(indendedX, level.width * Constants.tileSize - Constants.canvasWidth)),
      math.max(0, math.min(indendedY, level.height * Constants.tileSize - Constants.canvasHeight))
      )
  }

  lazy val applyGravity = {
    val nextVy = math.min(Constants.terminalVelocity, player.vy + Constants.gravity)
    copy(player = player.copy(vy = nextVy))
  }

  lazy val canJump = Set(
      level.tiles((player.y.toInt + 32) / Constants.tileSize)(player.x / Constants.tileSize),
      level.tiles((player.y.toInt + 32) / Constants.tileSize)((player.x + 15) / Constants.tileSize)
    ).exists(_ != 0)

  def movePlayer(dx: Int): GameState = {
    val dy = player.vy
    val playerXMove = if (dx != 0) {
      val newX = player.x + dx 
      lazy val tiles = Set(
        level.tiles(player.y.toInt / Constants.tileSize)(newX / Constants.tileSize),
        level.tiles(player.y.toInt / Constants.tileSize)((newX + 15) / Constants.tileSize),
        level.tiles((player.y.toInt + 31) / Constants.tileSize)(newX / Constants.tileSize),
        level.tiles((player.y.toInt + 31) / Constants.tileSize)((newX + 15) / Constants.tileSize)
      )
      if (newX < 0 || newX + 15 >= level.width * Constants.tileSize || tiles != Set(0)) player
      else player.copy(x = newX)
    } else player

    val newY = playerXMove.y + dy
    lazy val tiles = Set(
      level.tiles(newY.toInt / Constants.tileSize)(playerXMove.x / Constants.tileSize),
      level.tiles((newY.toInt) / Constants.tileSize)((playerXMove.x + 15) / Constants.tileSize),
      level.tiles((newY.toInt + 31) / Constants.tileSize)(playerXMove.x / Constants.tileSize),
      level.tiles((newY.toInt + 31) / Constants.tileSize)((playerXMove.x + 15) / Constants.tileSize)
    )
    val newPlayer = if (newY < 0 || newY + 31 >= level.height * Constants.tileSize || tiles != Set(0))
      playerXMove
    else playerXMove.copy(y = newY)
    copy(player = newPlayer)
  }

  def processInput(key: KeyboardInput) = 
    if (key.isDown(KeyboardInput.Key.Space) && canJump)
      copy(player = player.copy(vy = -Constants.jumpSpeed)).movePlayer(0)
    else if (key.isDown(KeyboardInput.Key.Right)) movePlayer(1)
    else if (key.isDown(KeyboardInput.Key.Left)) movePlayer(-1)
    else movePlayer(0)
}

object GameState {
  final case class Player(x: Int, y: Double, vy: Double)
}
