package eu.joaocosta.volcano

import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._

final case class GameState(charX: Int, charY: Int, level: Level) {
  val charTileX = charX / 16
  val charTileY = charY / 16
}
