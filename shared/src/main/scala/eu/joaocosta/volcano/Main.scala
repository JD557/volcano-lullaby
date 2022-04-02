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
  val canvasSettings = Canvas.Settings(width = Constants.canvasWidth, height = Constants.canvasHeight, scale = 4)
  val canvasManager  = CanvasManager()
  val initialState   = GameState(GameState.Player(0, 0.0, 0), Resources.level)
  val frameRate      = LoopFrequency.hz60
  val terminateWhen  = (_: State) => false
  val renderFrame = (state: State) => for {
    _ <- CanvasIO.redraw
    keyboardInput <- CanvasIO.getKeyboardInput
    _ <- CanvasIO.clear()
    (camX, camY) = state.cameraPosition
    _ <- CanvasIO.blit(Resources.background)(-camX / 2, -camY / 2)
    _ <- CanvasIO.blit(Resources.character, Some(Color(255, 0, 255)))(state.player.x - camX, state.player.y.toInt - camY)
    _ <- CanvasIO.blit(state.level.surface, Some(Color(0, 0, 0)))(-camX, -camY)
    newState = state.processInput(keyboardInput).applyGravity
  } yield newState
}
