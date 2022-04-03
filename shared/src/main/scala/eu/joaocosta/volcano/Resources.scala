package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.extra._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.runtime._
object Resources {
  val menu     = Image.loadBmpImage(Resource("assets/menu.bmp")).get
  val gameOver = Image.loadBmpImage(Resource("assets/gameover.bmp")).get

  val goText     = Image.loadBmpImage(Resource("assets/go.bmp")).get
  val finishText = Image.loadQoiImage(Resource("assets/finish.qoi")).get
  val pressEnter = Image.loadQoiImage(Resource("assets/press-enter.qoi")).get

  val character = SpriteSheet(Image.loadBmpImage(Resource("assets/character.bmp")).get, 16, 32)

  val timer = SpriteSheet(Image.loadBmpImage(Resource("assets/timer.bmp")).get, 48, 16)

  val volcanoBackground = Image.loadBmpImage(Resource("assets/background.bmp")).get
  val volcanoTileset    = SpriteSheet(Image.loadBmpImage(Resource("assets/volcano-tiles.bmp")).get, 16, 16)
  val volcanoLevel      = Level.load(Resource("assets/level-volcano.txt"), volcanoTileset, volcanoBackground)

  val beachBackground = Image.loadBmpImage(Resource("assets/intro-bg.bmp")).get
  val beachTileset    = SpriteSheet(Image.loadBmpImage(Resource("assets/beach-tiles.bmp")).get, 16, 16)
  val introLevel      = Level.load(Resource("assets/level-intro.txt"), beachTileset, beachBackground)

  val forestBackground = Image.loadBmpImage(Resource("assets/forest-bg.bmp")).get
  val forestTileset    = SpriteSheet(Image.loadBmpImage(Resource("assets/forest-tiles.bmp")).get, 16, 16)
  val forestLevel      = Level.load(Resource("assets/level-forest.txt"), forestTileset, forestBackground)

  val templeBackground = Image.loadBmpImage(Resource("assets/temple-bg.bmp")).get
  val templeTileset    = SpriteSheet(Image.loadBmpImage(Resource("assets/temple-tiles.bmp")).get, 16, 16)
  val templeLevel      = Level.load(Resource("assets/level-temple.txt"), templeTileset, templeBackground)

  val soundPlayer     = SoundPlayer.default()
  val bgSoundChannel  = soundPlayer.newChannel()
  val sfxSoundChannel = soundPlayer.newChannel()

  val introSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/intro.mp3"
    case _           => "assets/intro.mid"
  }))
  val menuSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/menu.mp3"
    case _           => "assets/menu.mid"
  }))
  val jumpSound = soundPlayer.loadClip(Resource("assets/jump.wav"))
}
