package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.runtime.pure._

object Main extends MinartApp {

  val tileset = SpriteSheet(Image.loadBmpImage(Resource("assets/tileset.bmp")).get, 16, 16)
  val character = Image.loadBmpImage(Resource("assets/character.bmp")).get
  val background = Image.loadBmpImage(Resource("assets/background.bmp")).get
  val level: Vector[Vector[Int]] = Resource("assets/level.txt").withSource { source =>
    source.getLines().map { line =>
      line.map(c => c.toString.toInt).toVector
    }.toVector
  }.get
  val levelHeight = level.size
  val levelWidth = level.head.size

  val levelSurface = {
    val surface = new RamSurface(Vector.fill(levelHeight * 16)(Array.fill(levelWidth * 16)(Color(0, 0, 0))))
    for {
      (line, y) <- level.zipWithIndex
      (sprite, x) <- line.zipWithIndex
      if (sprite != 0)
    } surface.blit(tileset.getSprite(sprite - 1))(x * 16, y * 16)
    surface
  }

  case class GameState(charX: Int, charY: Int) {
    val charTileX = charX / 16
    val charTileY = charY / 16
  }

  type State = GameState
  val loopRunner     = LoopRunner()
  val canvasSettings = Canvas.Settings(width = 320, height = 180, scale = 4)
  val canvasManager  = CanvasManager()
  val initialState   = GameState(0, 0)
  val frameRate      = LoopFrequency.hz60
  val terminateWhen  = (_: State) => false
  val renderFrame = (gs: State) => for {
    _ <- CanvasIO.redraw
    _ <- CanvasIO.clear()
    _ <- CanvasIO.blit(background)(0, 0)
    _ <- CanvasIO.blit(character, Some(Color(255, 255, 255)))(gs.charX, gs.charY)
    _ <- CanvasIO.blit(levelSurface, Some(Color(0, 0, 0)))(0, 0)
    newState =
      if (level(gs.charTileY + 2)(gs.charTileX) == 0) gs.copy(charY = gs.charY + 1)
      else gs
  } yield newState
}
