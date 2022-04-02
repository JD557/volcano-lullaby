package eu.joaocosta.volcano

import eu.joaocosta.minart.input._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._

final case class GameState(charX: Int, charY: Int, level: Level) {
  val applyGravity = movePlayer(0, 1)

  def movePlayer(dx: Int, dy: Int): GameState =
    if (dx != 0) {
      val newX = charX + dx 
      lazy val tiles = Set(
        level.tiles(charY / 16)(newX / 16),
        level.tiles((charY + 31) / 16)((newX + 15) / 16),
        level.tiles(charY / 16)(newX / 16),
        level.tiles((charY + 31) / 16)((newX + 15) / 16)
      )
      if (newX < 0 || newX + 15 > level.width * 16 || tiles != Set(0))
        movePlayer(0, dy)
      else copy(charX = newX).movePlayer(0, dy)
    }
    else if (dy != 0) {
      val newY = charY + dy
      lazy val tiles = Set(
        level.tiles(newY / 16)(charX / 16),
        level.tiles((newY + 31) / 16)((charX + 15) / 16),
        level.tiles(newY / 16)(charX / 16),
        level.tiles((newY + 31) / 16)((charX + 15) / 16)
      )
      if (newY < 0 || newY + 31 > level.height * 16 || tiles != Set(0))
        movePlayer(0, 0)
      else copy(charY = newY)
    }
    else this

  def processInput(key: KeyboardInput) = 
    if (key.isDown(KeyboardInput.Key.Right)) movePlayer(1, 0)
    else if (key.isDown(KeyboardInput.Key.Left)) movePlayer(-1, 0)
    else this
}
