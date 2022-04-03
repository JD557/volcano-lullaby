package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.extra._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.runtime._
object Resources {
  val menu     = Image.loadQoiImage(Resource("assets/menu.qoi")).get
  val gameOver = Image.loadQoiImage(Resource("assets/gameover.qoi")).get

  val logo       = Image.loadQoiImage(Resource("assets/logo.qoi")).get
  val thanks     = Image.loadQoiImage(Resource("assets/thanks.qoi")).get
  val goText     = Image.loadQoiImage(Resource("assets/go.qoi")).get
  val finishText = Image.loadQoiImage(Resource("assets/finish.qoi")).get
  val pressEnter = Image.loadQoiImage(Resource("assets/press-enter.qoi")).get

  val character = SpriteSheet(Image.loadQoiImage(Resource("assets/character.qoi")).get, 16, 32)

  val timer = SpriteSheet(Image.loadQoiImage(Resource("assets/timer.qoi")).get, 48, 16)

  val volcanoBackground = Image.loadQoiImage(Resource("assets/volcano-bg.qoi")).get
  val volcanoTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/volcano-tiles.qoi")).get, 16, 16)
  val volcanoLevel      = Level.load(Resource("assets/level-volcano.txt"), volcanoTileset, volcanoBackground)

  val beachBackground = Image.loadQoiImage(Resource("assets/intro-bg.qoi")).get
  val beachTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/beach-tiles.qoi")).get, 16, 16)
  val introLevel      = Level.load(Resource("assets/level-intro.txt"), beachTileset, beachBackground)

  val forestBackground = Image.loadQoiImage(Resource("assets/forest-bg.qoi")).get
  val forestTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/forest-tiles.qoi")).get, 16, 16)
  val forestLevel      = Level.load(Resource("assets/level-forest.txt"), forestTileset, forestBackground)

  val templeBackground = Image.loadQoiImage(Resource("assets/temple-bg.qoi")).get
  val templeTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/temple-tiles.qoi")).get, 16, 16)
  val templeLevel      = Level.load(Resource("assets/level-temple.txt"), templeTileset, templeBackground)

  val soundPlayer     = SoundPlayer.default()
  val bgSoundChannel  = soundPlayer.newChannel()
  val sfxSoundChannel = soundPlayer.newChannel()

  val introSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/intro.mp3"
    case _           => "assets/intro.mid"
  }))
  // Clear this after the jam
  val menuSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/ingame.mp3"
    case _           => "assets/ingame.mid"
  }))
  val inGameSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/menu.mp3"
    case _           => "assets/menu.mid"
  }))
  val gameoverSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/gameOver.mp3"
    case _           => "assets/gameOver.mid"
  }))
  val lullabySound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/lullaby.mp3"
    case _           => "assets/lullaby.mid"
  }))
  val jumpSound = soundPlayer.loadClip(Resource("assets/jump.wav"))
}
