package eu.joaocosta.volcano

import eu.joaocosta.minart.audio._
import eu.joaocosta.minart.audio.pure._
import eu.joaocosta.minart.audio.sound._
import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.extra._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.graphics.pure._
import eu.joaocosta.minart.input._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.runtime.pure._

object Main extends MinartApp[AppState, AppLoop.LowLevelAllSubsystems] {

  def playerSurface(player: GameState.Player, frame: Int, singing: Boolean = false) = {
    val sprite =
      if (singing) Resources.character.getSprite(3, (frame / Constants.animationMultiplier) % 4)
      else if (player.vy != 0) Resources.character.getSprite(2, 0)
      else if (player.vx != 0) Resources.character.getSprite(1, (frame / Constants.animationMultiplier) % 4)
      else Resources.character.getSprite(0, (frame / Constants.animationMultiplier)                     % 4)
    if (player.lastDirX == -1) sprite.view.flipH else sprite
  }

  val loopRunner      = LoopRunner()
  val createSubsystem = () => LowLevelCanvas.create() ++ LowLevelAudioPlayer.create()
  val canvasSettings = Canvas.Settings(
    width = Constants.canvasWidth,
    height = Constants.canvasHeight,
    scale = Some(4),
    clearColor = Color(0, 0, 0)
  )
  val initialState = Loading(0, Resources.allResources)
  val frameRate    = LoopFrequency.hz60

  def transitionTo(state: AppState): AudioPlayerIO[AppState] = state match {
    case Menu =>
      for {
        _ <- AudioPlayerIO.stop
        _ <- AudioPlayerIO.play(Resources.menuSound.repeating, Resources.bgSoundChannel)
      } yield state
    case _: Intro =>
      for {
        _ <- AudioPlayerIO.stop
        _ <- AudioPlayerIO.play(Resources.introSound, Resources.bgSoundChannel)
      } yield state
    case _: GameState =>
      for {
        _ <- AudioPlayerIO.stop
        _ <- AudioPlayerIO.play(Resources.inGameSound.repeating, Resources.bgSoundChannel)
      } yield state
    case _: LevelTransition =>
      for {
        _ <- AudioPlayerIO.stop
        _ <- AudioPlayerIO.play(Resources.lullabySound, Resources.bgSoundChannel)
      } yield state
    case GameOver =>
      for {
        _ <- AudioPlayerIO.stop
        _ <- AudioPlayerIO.play(Resources.gameoverSound, Resources.bgSoundChannel)
      } yield state
    case Thanks =>
      for {
        _ <- AudioPlayerIO.stop
        _ <- AudioPlayerIO.play(Resources.lullabySound, Resources.bgSoundChannel)
      } yield state
    case _ => RIO.pure(state)
  }

  val frameCounter = {
    var frameNumber: Int = 0
    var timer            = System.currentTimeMillis
    () => {
      frameNumber += 1
      if (frameNumber % 10 == 0) {
        val currTime = System.currentTimeMillis()
        val fps      = 10.0 / ((currTime - timer) / 1000.0)
        println("FPS:" + fps)
        timer = System.currentTimeMillis()
      }
    }
  }

  val appLoop = AppLoop
    .statefulAppLoop((state: AppState) =>
      state match {
        case Loading(_, Nil) =>
          transitionTo(Intro(0))
        case Loading(loaded, loadNext :: remaining) =>
          for {
            _ <- CanvasIO.clear()
            _ <- CanvasIO.fillRegion(
              10,
              Constants.canvasHeight - 20,
              Constants.canvasWidth - 20,
              10,
              Color(255, 255, 255)
            )
            _ <- CanvasIO.fillRegion(
              10 + 2,
              Constants.canvasHeight - 20 + 2,
              Constants.canvasWidth - 20 - 4,
              10 - 4,
              Color(0, 0, 0)
            )
            percentage = loaded.toDouble / (loaded + remaining.size)
            _ <- CanvasIO.fillRegion(
              10 + 3,
              Constants.canvasHeight - 20 + 3,
              (percentage * (Constants.canvasWidth - 20 - 6)).toInt,
              10 - 6,
              Color(255, 255, 255)
            )
            _ <- CanvasIO.redraw
            _ = loadNext()
          } yield Loading(loaded + 1, remaining)
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
            newState <-
              if (
                keyboardInput.keysPressed(
                  KeyboardInput.Key.Enter
                ) && frame > Constants.timeRecharge / Constants.rechargeSpeed
              )
                transitionTo(gameState.nextLevel)
              else if (frame < Constants.timeRecharge / Constants.rechargeSpeed)
                CanvasIO.suspend(
                  LevelTransition(
                    gameState.copy(
                      remainingFrames =
                        math.min(gameState.remainingFrames + Constants.rechargeSpeed, Constants.maximumTime)
                    ),
                    frame + 1
                  )
                )
              else CanvasIO.suspend(LevelTransition(gameState, frame + 1))
          } yield newState
        case gs @ GameState(player, level, _, remainingFrames, _) =>
          for {
            _             <- CanvasIO.redraw
            keyboardInput <- CanvasIO.getKeyboardInput
            _             <- CanvasIO.clear()
            _            = frameCounter()
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
            _ <- AudioPlayerIO.when(keyboardInput.isDown(KeyboardInput.Key.Space) && gs.canJump)(
              AudioPlayerIO
                .stop(Resources.sfxSoundChannel)
                .andThen(
                  AudioPlayerIO.play(Resources.jumpSound, Resources.sfxSoundChannel)
                )
            )
            newState = gs.nextState(keyboardInput)
            _ <- AudioPlayerIO.when(!newState.isInstanceOf[GameState])(transitionTo(newState).unit)
          } yield newState
      }
    )
    .configure((canvasSettings, AudioPlayer.Settings()), frameRate, initialState)
}
