package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.extra._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.runtime._

object Resources {
  lazy val menu     = Image.loadQoiImage(Resource("assets/menu.qoi")).get
  lazy val gameOver = Image.loadQoiImage(Resource("assets/gameover.qoi")).get

  lazy val logo       = Image.loadQoiImage(Resource("assets/logo.qoi")).get
  lazy val thanks     = Image.loadQoiImage(Resource("assets/thanks.qoi")).get
  lazy val goText     = Image.loadQoiImage(Resource("assets/go.qoi")).get
  lazy val finishText = Image.loadQoiImage(Resource("assets/finish.qoi")).get
  lazy val pressEnter = Image.loadQoiImage(Resource("assets/press-enter.qoi")).get

  lazy val character = SpriteSheet(Image.loadQoiImage(Resource("assets/character.qoi")).get, 16, 32)

  lazy val timer = SpriteSheet(Image.loadQoiImage(Resource("assets/timer.qoi")).get, 48, 16)

  lazy val volcanoBackground = Image.loadQoiImage(Resource("assets/volcano-bg.qoi")).get
  lazy val volcanoTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/volcano-tiles.qoi")).get, 16, 16)
  lazy val volcanoLevel      = Level.load(Resource("assets/level-volcano.txt"), volcanoTileset, volcanoBackground)

  lazy val beachBackground = Image.loadQoiImage(Resource("assets/intro-bg.qoi")).get
  lazy val beachTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/beach-tiles.qoi")).get, 16, 16)
  lazy val introLevel      = Level.load(Resource("assets/level-intro.txt"), beachTileset, beachBackground)

  lazy val forestBackground = Image.loadQoiImage(Resource("assets/forest-bg.qoi")).get
  lazy val forestTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/forest-tiles.qoi")).get, 16, 16)
  lazy val forestLevel      = Level.load(Resource("assets/level-forest.txt"), forestTileset, forestBackground)

  lazy val templeBackground = Image.loadQoiImage(Resource("assets/temple-bg.qoi")).get
  lazy val templeTileset    = SpriteSheet(Image.loadQoiImage(Resource("assets/temple-tiles.qoi")).get, 16, 16)
  lazy val templeLevel      = Level.load(Resource("assets/level-temple.txt"), templeTileset, templeBackground)

  lazy val soundPlayer     = SoundPlayer.default()
  lazy val bgSoundChannel  = soundPlayer.newChannel()
  lazy val sfxSoundChannel = soundPlayer.newChannel()

  lazy val introSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/intro.mp3"
    case _           => "assets/intro.mid"
  }))
  // Clear this after the jam
  lazy val menuSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/ingame.mp3"
    case _           => "assets/ingame.mid"
  }))
  lazy val inGameSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/menu.mp3"
    case _           => "assets/menu.mid"
  }))
  lazy val gameoverSound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/gameover.mp3"
    case _           => "assets/gameover.mid"
  }))
  lazy val lullabySound = soundPlayer.loadClip(Resource(Platform() match {
    case Platform.JS => "assets/lullaby.mp3"
    case _           => "assets/lullaby.mid"
  }))
  lazy val jumpSound = soundPlayer.loadClip(Resource("assets/jump.wav"))

  val allResources: List[() => Any] = List(
    () => menu,
    () => gameOver,
    () => logo,
    () => thanks,
    () => goText,
    () => finishText,
    () => pressEnter,
    () => character,
    () => timer,
    () => volcanoBackground,
    () => volcanoTileset,
    () => volcanoLevel,
    () => beachBackground,
    () => beachTileset,
    () => introLevel,
    () => forestBackground,
    () => forestTileset,
    () => forestLevel,
    () => templeBackground,
    () => templeTileset,
    () => templeLevel,
    () => introSound,
    () => menuSound,
    () => inGameSound,
    () => gameoverSound,
    () => lullabySound,
    () => jumpSound
  )
}
