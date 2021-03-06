package eu.joaocosta.volcano

import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.input._

sealed trait AppState

final case class Loading(loaded: Int, remainingResouces: List[() => Any]) extends AppState
final case class Intro(frame: Int)                                        extends AppState
case object Menu                                                          extends AppState
case object GameOver                                                      extends AppState
case object Thanks                                                        extends AppState
final case class LevelTransition(finalState: GameState, frame: Int)       extends AppState

final case class GameState(
    player: GameState.Player,
    level: Level,
    startingFrames: Int,
    remainingFrames: Int,
    nextLevels: List[Level]
) extends AppState {

  lazy val nextLevel =
    if (nextLevels.isEmpty) Thanks
    else GameState(GameState.Player(), nextLevels.head, remainingFrames, remainingFrames, nextLevels.tail)

  val frame = startingFrames - remainingFrames

  lazy val cameraPosition = {
    val indendedX = player.xInt - (Constants.tileSize / 2) - (Constants.canvasWidth / 2)
    val indendedY = player.yInt - (Constants.tileSize) - (Constants.canvasHeight / 2)
    (
      math.max(0, math.min(indendedX, level.width * Constants.tileSize - Constants.canvasWidth)),
      math.max(0, math.min(indendedY, level.height * Constants.tileSize - Constants.canvasHeight))
    )
  }

  private def occupiedTiles(playerX: Int, playerY: Int): List[Int] =
    List(
      level.tiles(playerY / Constants.tileSize)(playerX / Constants.tileSize),
      level.tiles(playerY / Constants.tileSize)((playerX + 15) / Constants.tileSize),
      level.tiles((playerY + 15) / Constants.tileSize)(playerX / Constants.tileSize),
      level.tiles((playerY + 15) / Constants.tileSize)((playerX + 15) / Constants.tileSize),
      level.tiles((playerY + 31) / Constants.tileSize)(playerX / Constants.tileSize),
      level.tiles((playerY + 31) / Constants.tileSize)((playerX + 15) / Constants.tileSize)
    )

  private lazy val movePlayer: GameState = {
    val dx = player.vx
    val dy = player.vy
    val playerXMove = if (dx != 0) {
      val newX       = player.x + dx
      lazy val tiles = occupiedTiles(newX.toInt, player.yInt)
      if (newX < 0 || newX + 15 >= level.width * Constants.tileSize || tiles.exists(_ >= 10)) player
      else player.copy(x = newX)
    } else player

    val newY       = playerXMove.y + dy
    lazy val tiles = occupiedTiles(playerXMove.xInt, newY.toInt)
    val newPlayer =
      if (newY < 0 || newY + 31 >= level.height * Constants.tileSize || tiles.exists(_ >= 10))
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

  lazy val canJump: Boolean =
    occupiedTiles(player.xInt, player.yInt + 1).drop(4).exists(_ >= 10)

  lazy val finished: Boolean =
    canJump && occupiedTiles(player.xInt, player.yInt).exists(_ == 9)

  private def processInput(key: KeyboardInput): GameState =
    if (key.keysPressed(KeyboardInput.Key.Space) && canJump)
      copy(player = player.copy(vy = -Constants.jumpSpeed))
    else if (key.isDown(KeyboardInput.Key.Right)) {
      val newVx =
        if (player.vx < 0) Constants.acceleration
        else math.min(player.vx + Constants.acceleration, Constants.maxSpeed)
      copy(player = player.copy(vx = newVx, lastDirX = 1))
    } else if (key.isDown(KeyboardInput.Key.Left)) {
      val newVx =
        if (player.vx > 0) -Constants.acceleration
        else math.max(-Constants.maxSpeed, player.vx - Constants.acceleration)
      copy(player = player.copy(vx = newVx, lastDirX = -1))
    } else this

  def nextState(key: KeyboardInput): AppState = {
    def aux(acc: GameState, loop: Int): GameState =
      if (loop <= 0) acc
      else aux(acc.processInput(key).movePlayer.updateVelocity, loop - 1)
    if (remainingFrames <= 0) GameOver
    else if (finished) LevelTransition(this, 0)
    else aux(this, Constants.speedMultiplier).copy(remainingFrames = remainingFrames - 1)
  }
}

object GameState {
  final case class Player(x: Double = 0.0, y: Double = 0.0, vx: Double = 0.0, vy: Double = 0.0, lastDirX: Int = 0) {
    lazy val xInt = x.toInt
    lazy val yInt = y.toInt
  }

  val initialState =
    GameState(
      GameState.Player(),
      Constants.levels.head,
      Constants.maximumTime,
      Constants.maximumTime,
      Constants.levels.tail
    )
}
