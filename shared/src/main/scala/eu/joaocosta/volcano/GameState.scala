package eu.joaocosta.volcano

import eu.joaocosta.minart.input._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._

final case class GameState(player: GameState.Player, level: Level) {
  lazy val applyGravity = {
    val nextVy = math.min(1.0, player.vy + 0.1)
    copy(player = player.copy(vy = nextVy))
  }

  lazy val canJump = Set(
      level.tiles((player.y.toInt + 32) / 16)(player.x / 16),
      level.tiles((player.y.toInt + 32) / 16)((player.x + 15) / 16)
    ).exists(_ != 0)

  def movePlayer(dx: Int): GameState = {
    val dy = player.vy
    val playerXMove = if (dx != 0) {
      val newX = player.x + dx 
      lazy val tiles = Set(
        level.tiles(player.y.toInt / 16)(newX / 16),
        level.tiles(player.y.toInt / 16)((newX + 15) / 16),
        level.tiles((player.y.toInt + 31) / 16)(newX / 16),
        level.tiles((player.y.toInt + 31) / 16)((newX + 15) / 16)
      )
      if (newX < 0 || newX + 15 > level.width * 16 || tiles != Set(0)) player
      else player.copy(x = newX)
    } else player

    val newY = playerXMove.y + dy
    lazy val tiles = Set(
      level.tiles(newY.toInt / 16)(playerXMove.x / 16),
      level.tiles((newY.toInt) / 16)((playerXMove.x + 15) / 16),
      level.tiles((newY.toInt + 31) / 16)(playerXMove.x / 16),
      level.tiles((newY.toInt + 31) / 16)((playerXMove.x + 15) / 16)
    )
    val newPlayer = if (newY < 0 || newY + 31 > level.height * 16 || tiles != Set(0))
      playerXMove
    else playerXMove.copy(y = newY)
    copy(player = newPlayer)
  }

  def processInput(key: KeyboardInput) = 
    if (key.isDown(KeyboardInput.Key.Space) && canJump)
      copy(player = player.copy(vy = -3.0)).movePlayer(0)
    else if (key.isDown(KeyboardInput.Key.Right)) movePlayer(1)
    else if (key.isDown(KeyboardInput.Key.Left)) movePlayer(-1)
    else movePlayer(0)
}

object GameState {
  final case class Player(x: Int, y: Double, vy: Double)
}
