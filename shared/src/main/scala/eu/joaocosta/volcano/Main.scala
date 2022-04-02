package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.runtime.pure._

object Main extends MinartApp {

  type State = GameState
  val loopRunner     = LoopRunner()
  val canvasSettings = Canvas.Settings(width = 320, height = 180, scale = 4)
  val canvasManager  = CanvasManager()
  val initialState   = GameState(0, 0, Resources.level)
  val frameRate      = LoopFrequency.hz60
  val terminateWhen  = (_: State) => false
  val renderFrame = (state: State) => for {
    _ <- CanvasIO.redraw
    _ <- CanvasIO.clear()
    _ <- CanvasIO.blit(Resources.background)(0, 0)
    _ <- CanvasIO.blit(Resources.character, Some(Color(255, 255, 255)))(state.charX, state.charY)
    _ <- CanvasIO.blit(state.level.surface, Some(Color(0, 0, 0)))(0, 0)
    newState =
      if (state.level.tiles(state.charTileY + 2)(state.charTileX) == 0) state.copy(charY = state.charY + 1)
      else state
  } yield newState
}
