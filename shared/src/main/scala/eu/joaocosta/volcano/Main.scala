package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.input._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.runtime.pure._

object Main extends MinartApp {

  def playerSurface(player: GameState.Player, frame: Int, singing: Boolean = false) = {
    val sprite =
      if (singing) Resources.character.getSprite(3, (frame / Constants.animationMultiplier) % 4)
      else if (player.vy != 0) Resources.character.getSprite(2, 0)
      else if (player.vx != 0) Resources.character.getSprite(1, (frame / Constants.animationMultiplier) % 4)
      else Resources.character.getSprite(0, (frame / Constants.animationMultiplier)                     % 4)
    if (player.lastDirX == -1) Image.flipH(sprite) else sprite
  }

  type State = AppState
  val loopRunner = LoopRunner()
  val canvasSettings = Canvas.Settings(
    width = Constants.canvasWidth,
    height = Constants.canvasHeight,
    scale = 4,
    clearColor = Color(0, 0, 0)
  )
  val canvasManager = CanvasManager()
  val initialState  = Menu
  val frameRate     = LoopFrequency.hz60
  val terminateWhen = (_: State) => false
  val renderFrame = (state: State) => state match {
    case Menu =>
      for {
        _             <- CanvasIO.redraw
        keyboardInput <- CanvasIO.getKeyboardInput
        _             <- CanvasIO.clear()
        _ <- CanvasIO.blit(Resources.menu)(0, 0)
        newState =
          if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) GameState.initialState
          else state
      } yield newState
    case GameOver =>
      for {
        _             <- CanvasIO.redraw
        keyboardInput <- CanvasIO.getKeyboardInput
        _             <- CanvasIO.clear()
        _ <- CanvasIO.blit(Resources.gameOver)(0, 0)
        newState =
          if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) Menu
          else state
      } yield newState
    case LevelTransition(gameState, frame) =>
      for {
        _             <- CanvasIO.redraw
        keyboardInput <- CanvasIO.getKeyboardInput
        _             <- CanvasIO.clear()
        (camX, camY) = gameState.cameraPosition
        _ <- CanvasIO.blit(gameState.level.background)(-camX / 2, -camY / 2)
        _ <- CanvasIO.blit(gameState.level.surface, Some(Color(0, 0, 0)))(-camX, -camY)
        _ <- CanvasIO.blit(playerSurface(gameState.player, frame, singing = true), Some(Color(255, 0, 255)))(
          gameState.player.xInt - camX,
          gameState.player.yInt - camY
        )
        _ <- CanvasIO.blit(Resources.timer.getSprite(1), Some(Color(255, 0, 255)))(5, 5)
        _ <- CanvasIO.blit(Resources.timer.getSprite(0), Some(Color(255, 0, 255)))(5, 5, 0, 0, 48 * gameState.remainingFrames / Constants.maximumTime)
        newState = 
          if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) Menu
          else LevelTransition(gameState, frame + 1)
      } yield newState
    case gs@GameState(player, level, remainingFrames) =>
      for {
        _             <- CanvasIO.redraw
        keyboardInput <- CanvasIO.getKeyboardInput
        _             <- CanvasIO.clear()
        (camX, camY) = gs.cameraPosition
        _ <- CanvasIO.blit(level.background)(-camX / 2, -camY / 2)
        _ <- CanvasIO.blit(level.surface, Some(Color(0, 0, 0)))(-camX, -camY)
        _ <- CanvasIO.blit(playerSurface(player, remainingFrames), Some(Color(255, 0, 255)))(
          player.xInt - camX,
          player.yInt - camY
        )
        _ <- CanvasIO.blit(Resources.timer.getSprite(1), Some(Color(255, 0, 255)))(5, 5)
        _ <- CanvasIO.blit(Resources.timer.getSprite(0), Some(Color(255, 0, 255)))(5, 5, 0, 0, 48 * remainingFrames / Constants.maximumTime)
        newState = gs.nextState(keyboardInput)
      } yield newState
  }
}
