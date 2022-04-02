package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.runtime.pure._

object Main extends MinartApp {

  val tileset = SpriteSheet(Image.loadBmpImage(Resource("assets/tileset.bmp")).get, 16, 16)
  val background = Image.loadBmpImage(Resource("assets/background.bmp")).get
  val level: List[List[Int]] = Resource("assets/level.txt").withSource { source =>
    source.getLines().map { line =>
      line.map(c => c.toString.toInt).toList
    }.toList
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

  type State = Unit
  val loopRunner     = LoopRunner()
  val canvasSettings = Canvas.Settings(width = 320, height = 180, scale = 4)
  val canvasManager  = CanvasManager()
  val initialState   = ()
  val frameRate      = LoopFrequency.Uncapped
  val terminateWhen  = (_: State) => false
  val renderFrame = (_: State) => for {
    _ <- CanvasIO.redraw
    _ <- CanvasIO.clear()
    _ <- CanvasIO.blit(background)(0, 0)
    _ <- CanvasIO.blit(levelSurface, Some(Color(0, 0, 0)))(0, 0)
  } yield ()
}
