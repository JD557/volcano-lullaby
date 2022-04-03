package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.input._
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
  val initialState  = Loading
  val frameRate     = LoopFrequency.hz60
  val terminateWhen = (_: State) => false

  def transitionTo(state: AppState): CanvasIO[AppState] = state match {
    case Menu =>
      Resources.bgSoundChannel.playLooped(Resources.menuSound).as(state)
    case _: Intro =>
      Resources.bgSoundChannel.playOnce(Resources.introSound).as(state)
    /*case _: AppState.GameState =>
      Resources.bgSoundChannel.playLooped(Resources.ingameSound).as(state)*/
    case _: LevelTransition =>
      Resources.bgSoundChannel.playOnce(Resources.lullabySound).as(state)
    case GameOver =>
      Resources.bgSoundChannel.playOnce(Resources.gameoverSound).as(state)
    case Thanks =>
      Resources.bgSoundChannel.playOnce(Resources.gameoverSound).as(state)
    case _ => CanvasIO.pure(state)
  }

  val renderFrame = (state: State) =>
    state match {
      case Loading =>
        transitionTo(Intro(0))
      case Intro(frame) =>
        for {
          _             <- CanvasIO.redraw
          keyboardInput <- CanvasIO.getKeyboardInput
          _             <- CanvasIO.clear()
          _             <- CanvasIO.blit(Resources.menu)(0, 0, 0, frame / 4)
          newState <-
            if (frame >= 180 * 4 || keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(Menu)
            else CanvasIO.suspend(Intro(frame + 1))
        } yield newState
      case Menu =>
        for {
          _             <- CanvasIO.redraw
          keyboardInput <- CanvasIO.getKeyboardInput
          _             <- CanvasIO.clear()
          _             <- CanvasIO.blit(Resources.menu)(0, 0, 0, 180)
          _             <- CanvasIO.blit(Resources.logo, Some(Color(255, 255, 255)))(64, 32)
          _             <- CanvasIO.blit(Resources.pressEnter, Some(Color(255, 0, 255)))(137, 128)
          newState <-
            if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(GameState.initialState)
            else CanvasIO.suspend(state)
        } yield newState
      case GameOver =>
        for {
          _             <- CanvasIO.redraw
          keyboardInput <- CanvasIO.getKeyboardInput
          _             <- CanvasIO.clear()
          _             <- CanvasIO.blit(Resources.gameOver)(0, 0)
          _             <- CanvasIO.blit(Resources.pressEnter, Some(Color(255, 0, 255)))(137, 128)
          newState <-
            if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(Menu)
            else CanvasIO.suspend(state)
        } yield newState
      case Thanks =>
        for {
          _             <- CanvasIO.redraw
          keyboardInput <- CanvasIO.getKeyboardInput
          _             <- CanvasIO.clear()
          _             <- CanvasIO.blit(Resources.menu)(0, 0, 0, 180)
          _             <- CanvasIO.blit(Resources.thanks, Some(Color(255, 255, 255)))(0, 32)
          _             <- CanvasIO.blit(Resources.pressEnter, Some(Color(255, 0, 255)))(137, 128)
          newState <-
            if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(Menu)
            else CanvasIO.suspend(state)
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
          _ <- CanvasIO.blit(Resources.timer.getSprite(0), Some(Color(255, 0, 255)))(
            5,
            5,
            0,
            0,
            48 * gameState.remainingFrames / Constants.maximumTime
          )
          _ <- CanvasIO.when(frame > 10)(
            CanvasIO.blit(Resources.finishText, Some(Color(255, 255, 255)))(73, 32)
          )
          _ <- CanvasIO.when(frame > Constants.timeRecharge / Constants.rechargeSpeed)(
            CanvasIO.blit(Resources.pressEnter, Some(Color(255, 0, 255)))(137, 128)
          )
          newState =
            if (
              keyboardInput.keysPressed(
                KeyboardInput.Key.Enter
              ) && frame > Constants.timeRecharge / Constants.rechargeSpeed
            )
              gameState.nextLevel
            else if (frame < Constants.timeRecharge / Constants.rechargeSpeed)
              LevelTransition(
                gameState.copy(
                  remainingFrames = math.min(gameState.remainingFrames + Constants.rechargeSpeed, Constants.maximumTime)
                ),
                frame + 1
              )
            else LevelTransition(gameState, frame + 1)
        } yield newState
      case gs @ GameState(player, level, _, remainingFrames, _) =>
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
          _ <- CanvasIO.blit(Resources.timer.getSprite(0), Some(Color(255, 0, 255)))(
            5,
            5,
            0,
            0,
            48 * remainingFrames / Constants.maximumTime
          )
          _ <- CanvasIO.when(gs.frame < 60 && gs.frame % 20 < 10)(
            CanvasIO.blit(Resources.goText, Some(Color(255, 255, 255)))(128, 64)
          )
          _ <- CanvasIO.when(keyboardInput.isDown(KeyboardInput.Key.Space) && gs.canJump)(
            Resources.sfxSoundChannel.playOnce(Resources.jumpSound)
          )
          newState <- transitionTo(gs.nextState(keyboardInput))
        } yield newState
    }
}
