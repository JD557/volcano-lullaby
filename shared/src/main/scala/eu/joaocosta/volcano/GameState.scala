package eu.joaocosta.volcano

import eu.joaocosta.minart.input._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._

final case class GameState(player: GameState.Player, level: Level, frame: Int) {

  lazy val cameraPosition = {
    val indendedX = player.xInt - (Constants.tileSize / 2) - (Constants.canvasWidth / 2)
    val indendedY = player.yInt - (Constants.tileSize) - (Constants.canvasHeight / 2)
    (math.max(0,math.min(indendedX, level.width * Constants.tileSize - Constants.canvasWidth)),
      math.max(0, math.min(indendedY, level.height * Constants.tileSize - Constants.canvasHeight))
      )
  }

  private lazy val movePlayer: GameState = {
    val dx = player.vx
    val dy = player.vy
    val playerXMove = if (dx != 0) {
      val newX = player.x + dx 
      lazy val tiles = Set(
        level.tiles(player.yInt / Constants.tileSize)(newX.toInt / Constants.tileSize),
        level.tiles(player.yInt / Constants.tileSize)((newX.toInt + 15) / Constants.tileSize),
        level.tiles((player.yInt + 31) / Constants.tileSize)(newX.toInt / Constants.tileSize),
        level.tiles((player.yInt + 31) / Constants.tileSize)((newX.toInt + 15) / Constants.tileSize)
      )
      if (newX < 0 || newX + 15 >= level.width * Constants.tileSize || tiles != Set(0)) player
      else player.copy(x = newX)
    } else player

    val newY = playerXMove.y + dy
    lazy val tiles = Set(
      level.tiles(newY.toInt / Constants.tileSize)(playerXMove.xInt / Constants.tileSize),
      level.tiles((newY.toInt) / Constants.tileSize)((playerXMove.xInt + 15) / Constants.tileSize),
      level.tiles((newY.toInt + 31) / Constants.tileSize)(playerXMove.xInt / Constants.tileSize),
      level.tiles((newY.toInt + 31) / Constants.tileSize)((playerXMove.xInt + 15) / Constants.tileSize)
    )
    val newPlayer = if (newY < 0 || newY + 31 >= level.height * Constants.tileSize || tiles != Set(0))
      playerXMove
    else playerXMove.copy(y = newY)
    copy(player = newPlayer)
  }

  private lazy val updateVelocity: GameState = {
    val nextVx =
      if (player.vx >= Constants.acceleration) player.vx - Constants.drag
      else if (player.vx <= -Constants.acceleration) player.vx + Constants.drag
      else 0.0
    val nextVy =
      if (canJump) 0.0
      else math.min(Constants.terminalVelocity, player.vy + Constants.gravity)
    copy(player = player.copy(vx = nextVx, vy = nextVy))
  }

  lazy val canJump: Boolean = Set(
      level.tiles((player.yInt + 32) / Constants.tileSize)(player.xInt / Constants.tileSize),
      level.tiles((player.yInt + 32) / Constants.tileSize)((player.xInt + 15) / Constants.tileSize)
    ).exists(_ != 0)


  private def processInput(key: KeyboardInput): GameState = 
    if (key.isDown(KeyboardInput.Key.Space) && canJump)
      copy(player = player.copy(vy = -Constants.jumpSpeed))
    else if (key.isDown(KeyboardInput.Key.Right)) {
      val newVx =
        if (player.vx < 0) Constants.acceleration
        else math.min(player.vx + Constants.acceleration, Constants.maxSpeed)
      copy(player = player.copy(vx = newVx, lastDirX = 1))
    }
    else if (key.isDown(KeyboardInput.Key.Left)) {
      val newVx = 
        if (player.vx > 0) -Constants.acceleration
        else math.max(-Constants.maxSpeed, player.vx - Constants.acceleration)
      copy(player = player.copy(vx = newVx, lastDirX = -1))
    }
    else this

  def nextState(key: KeyboardInput): GameState = {
    def aux(acc: GameState, loop: Int): GameState =
     if (loop <= 0) acc
     else aux(acc.processInput(key).movePlayer.updateVelocity, loop - 1)
    aux(this, Constants.speedMultiplier).copy(frame = frame + 1)
  }
}

object GameState {
  final case class Player(x: Double, y: Double, vx: Double, vy: Double, lastDirX: Int) {
    lazy val xInt = x.toInt
    lazy val yInt = y.toInt
  }
}
