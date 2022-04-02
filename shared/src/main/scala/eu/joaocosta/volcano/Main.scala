package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.runtime.pure._

object Main extends MinartApp {

  def playerSurface(player: GameState.Player, frame: Int) = {
    val sprite =
      if (player.vy != 0) Resources.character.getSprite(2, 0)
      else if (player.vx != 0) Resources.character.getSprite(1, (frame / Constants.animationMultiplier) % 4)
      else Resources.character.getSprite(0, (frame / Constants.animationMultiplier)                     % 4)
    if (player.lastDirX == -1) Image.flipH(sprite) else sprite
  }

  type State = GameState
  val loopRunner = LoopRunner()
  val canvasSettings = Canvas.Settings(
    width = Constants.canvasWidth,
    height = Constants.canvasHeight,
    scale = 4,
    clearColor = Color(0, 0, 0)
  )
  val canvasManager = CanvasManager()
  val initialState  = GameState(GameState.Player(0, 0, 0, 0, 0), Resources.introLevel, Constants.maximumTime)
  val frameRate     = LoopFrequency.hz60
  val terminateWhen = (_: State) => false
  val renderFrame = (state: State) =>
    for {
      _             <- CanvasIO.redraw
      keyboardInput <- CanvasIO.getKeyboardInput
      _             <- CanvasIO.clear()
      (camX, camY) = state.cameraPosition
      _ <- CanvasIO.blit(state.level.background)(-camX / 2, -camY / 2)
      _ <- CanvasIO.blit(state.level.surface, Some(Color(0, 0, 0)))(-camX, -camY)
      _ <- CanvasIO.blit(playerSurface(state.player, state.remainingFrames), Some(Color(255, 0, 255)))(
        state.player.xInt - camX,
        state.player.yInt - camY
      )
      _ <- CanvasIO.blit(Resources.timer.getSprite(1), Some(Color(255, 0, 255)))(5, 5)
      _ <- CanvasIO.blit(Resources.timer.getSprite(0), Some(Color(255, 0, 255)))(5, 5, 0, 0, 48 * state.remainingFrames / Constants.maximumTime)
      newState = state.nextState(keyboardInput)
    } yield newState
}
