package eu.joaocosta.volcano

import eu.joaocosta.minart.audio._
import eu.joaocosta.minart.audio.sound._
import eu.joaocosta.minart.backend.defaults.given
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.input._
import eu.joaocosta.minart.runtime._

object Main {

  def playerSurface(player: GameState.Player, frame: Int, singing: Boolean = false) = {
    val sprite =
      if (singing) Resources.character.getSprite(3, (frame / Constants.animationMultiplier) % 4)
      else if (player.vy != 0) Resources.character.getSprite(2, 0)
      else if (player.vx != 0) Resources.character.getSprite(1, (frame / Constants.animationMultiplier) % 4)
      else Resources.character.getSprite(0, (frame / Constants.animationMultiplier)                     % 4)
    if (player.lastDirX == -1) sprite.view.flipH else sprite
  }

  val canvasSettings = Canvas.Settings(
    width = Constants.canvasWidth,
    height = Constants.canvasHeight,
    scale = Some(4),
    clearColor = Color(0, 0, 0)
  )
  val initialState = Loading(0, Resources.allResources)
  val frameRate    = LoopFrequency.hz60

  def transitionTo(audioPlayer: AudioPlayer, state: AppState): AppState = state match {
    case Menu =>
      audioPlayer.stop()
      audioPlayer.play(Resources.menuSound.repeating, Resources.bgSoundChannel)
      state
    case _: Intro =>
      audioPlayer.stop()
      audioPlayer.play(Resources.introSound, Resources.bgSoundChannel)
      state
    case _: GameState =>
      audioPlayer.stop()
      audioPlayer.play(Resources.inGameSound.repeating, Resources.bgSoundChannel)
      state
    case _: LevelTransition =>
      audioPlayer.stop()
      audioPlayer.play(Resources.lullabySound, Resources.bgSoundChannel)
      state
    case GameOver =>
      audioPlayer.stop()
      audioPlayer.play(Resources.gameoverSound, Resources.bgSoundChannel)
      state
    case Thanks =>
      audioPlayer.stop()
      audioPlayer.play(Resources.lullabySound, Resources.bgSoundChannel)
      state
    case _ => state
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
      system => {
        import system._
        state match {
          case Loading(_, Nil) =>
            transitionTo(audioPlayer, Intro(0))
          case Loading(loaded, loadNext :: remaining) =>
            canvas.clear()
            canvas.fillRegion(
              10,
              Constants.canvasHeight - 20,
              Constants.canvasWidth - 20,
              10,
              Color(255, 255, 255)
            )
            canvas.fillRegion(
              10 + 2,
              Constants.canvasHeight - 20 + 2,
              Constants.canvasWidth - 20 - 4,
              10 - 4,
              Color(0, 0, 0)
            )
            val percentage = loaded.toDouble / (loaded + remaining.size)
            canvas.fillRegion(
              10 + 3,
              Constants.canvasHeight - 20 + 3,
              (percentage * (Constants.canvasWidth - 20 - 6)).toInt,
              10 - 6,
              Color(255, 255, 255)
            )
            canvas.redraw()
            loadNext()
            Loading(loaded + 1, remaining)
          case Intro(frame) =>
            canvas.redraw()
            val keyboardInput = canvas.getKeyboardInput()
            canvas.clear()
            canvas.blit(Resources.menu)(0, 0, 0, frame / 4)
            if (frame >= 180 * 4 || keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(audioPlayer, Menu)
            else Intro(frame + 1)
          case Menu =>
            canvas.redraw()
            val keyboardInput = canvas.getKeyboardInput()
            canvas.clear()
            canvas.blit(Resources.menu)(0, 0, 0, 180)
            canvas.blit(Resources.logo, BlendMode.ColorMask(Color(255, 255, 255)))(64, 32)
            canvas.blit(Resources.pressEnter, BlendMode.ColorMask(Color(255, 0, 255)))(137, 128)
            if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(audioPlayer, GameState.initialState)
            else state
          case GameOver =>
            canvas.redraw()
            val keyboardInput = canvas.getKeyboardInput()
            canvas.clear()
            canvas.blit(Resources.gameOver)(0, 0)
            canvas.blit(Resources.pressEnter, BlendMode.ColorMask(Color(255, 0, 255)))(137, 128)
            if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(audioPlayer, Menu)
            else state
          case Thanks =>
            canvas.redraw()
            val keyboardInput = canvas.getKeyboardInput()
            canvas.clear()
            canvas.blit(Resources.menu)(0, 0, 0, 180)
            canvas.blit(Resources.thanks, BlendMode.ColorMask(Color(255, 255, 255)))(0, 32)
            canvas.blit(Resources.pressEnter, BlendMode.ColorMask(Color(255, 0, 255)))(137, 128)
            if (keyboardInput.keysPressed(KeyboardInput.Key.Enter)) transitionTo(audioPlayer, Menu)
            else state
          case LevelTransition(gameState, frame) =>
            canvas.redraw()
            val keyboardInput = canvas.getKeyboardInput()
            canvas.clear()
            val (camX, camY) = gameState.cameraPosition
            canvas.blit(gameState.level.background)(-camX / 2, -camY / 2)
            canvas.blit(gameState.level.surface, BlendMode.ColorMask(Color(0, 0, 0)))(-camX, -camY)
            canvas
              .blit(playerSurface(gameState.player, frame, singing = true), BlendMode.ColorMask(Color(255, 0, 255)))(
                gameState.player.xInt - camX,
                gameState.player.yInt - camY
              )
            canvas.blit(Resources.timer.getSprite(1), BlendMode.ColorMask(Color(255, 0, 255)))(5, 5)
            canvas.blit(Resources.timer.getSprite(0), BlendMode.ColorMask(Color(255, 0, 255)))(
              5,
              5,
              0,
              0,
              48 * gameState.remainingFrames / Constants.maximumTime
            )
            if (frame > 10) {
              canvas.blit(Resources.finishText, BlendMode.ColorMask(Color(255, 255, 255)))(73, 32)
            }
            if (frame > Constants.timeRecharge / Constants.rechargeSpeed) {
              canvas.blit(Resources.pressEnter, BlendMode.ColorMask(Color(255, 0, 255)))(137, 128)
            }
            if (
              keyboardInput.keysPressed(
                KeyboardInput.Key.Enter
              ) && frame > Constants.timeRecharge / Constants.rechargeSpeed
            ) transitionTo(audioPlayer, gameState.nextLevel)
            else if (frame < Constants.timeRecharge / Constants.rechargeSpeed)
              LevelTransition(
                gameState.copy(
                  remainingFrames = math.min(gameState.remainingFrames + Constants.rechargeSpeed, Constants.maximumTime)
                ),
                frame + 1
              )
            else LevelTransition(gameState, frame + 1)
          case gs @ GameState(player, level, _, remainingFrames, _) =>
            canvas.redraw()
            val keyboardInput = canvas.getKeyboardInput()
            canvas.clear()
            frameCounter()
            val (camX, camY) = gs.cameraPosition
            canvas.blit(level.background)(-camX / 2, -camY / 2)
            canvas.blit(level.surface, BlendMode.ColorMask(Color(0, 0, 0)))(-camX, -camY)
            canvas.blit(playerSurface(player, remainingFrames), BlendMode.ColorMask(Color(255, 0, 255)))(
              player.xInt - camX,
              player.yInt - camY
            )
            canvas.blit(Resources.timer.getSprite(1), BlendMode.ColorMask(Color(255, 0, 255)))(5, 5)
            canvas.blit(Resources.timer.getSprite(0), BlendMode.ColorMask(Color(255, 0, 255)))(
              5,
              5,
              0,
              0,
              48 * remainingFrames / Constants.maximumTime
            )
            if (gs.frame < 60 && gs.frame % 20 < 10) {
              canvas.blit(Resources.goText, BlendMode.ColorMask(Color(255, 255, 255)))(128, 64)
            }
            if (keyboardInput.isDown(KeyboardInput.Key.Space) && gs.canJump) {
              audioPlayer.stop(Resources.sfxSoundChannel)
              audioPlayer.play(Resources.jumpSound, Resources.sfxSoundChannel)
            }
            val newState = gs.nextState(keyboardInput)
            if (!newState.isInstanceOf[GameState]) transitionTo(audioPlayer, newState)
            else newState
        }
      }
    )
    .configure((canvasSettings, AudioPlayer.Settings()), frameRate, initialState)

  def main(args: Array[String]): Unit = appLoop.run()
}
