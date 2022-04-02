package eu.joaocosta.volcano

import eu.joaocosta.minart.input._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._

final case class GameState(charX: Int, charY: Int, level: Level) {
  val charTileX = charX / 16
  val charTileY = charY / 16

  val applyGravity = 
    if (level.tiles(charTileY + 2)(charTileX) == 0) movePlayer(0, 1)
    else this

  def movePlayer(dx: Int, dy: Int) =
    copy(charX = charX + dx, charY = charY + dy)

  def processInput(key: KeyboardInput) = 
    if (key.isDown(KeyboardInput.Key.Right)) movePlayer(1, 0)
    else if (key.isDown(KeyboardInput.Key.Left)) movePlayer(-1, 0)
    else this
}
