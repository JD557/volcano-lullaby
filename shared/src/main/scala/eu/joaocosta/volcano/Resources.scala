package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.runtime._

object Resources {
  val tileset = SpriteSheet(Image.loadBmpImage(Resource("assets/tileset.bmp")).get, 16, 16)
  val character = Image.loadBmpImage(Resource("assets/character.bmp")).get
  val background = Image.loadBmpImage(Resource("assets/background.bmp")).get
  val level = Level.load(Resource("assets/level.txt"), tileset)
}
