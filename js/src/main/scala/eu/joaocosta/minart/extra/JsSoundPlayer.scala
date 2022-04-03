package eu.joaocosta.minart.extra

import java.io.InputStream

import org.scalajs.dom
import org.scalajs.dom.html.Audio

import eu.joaocosta.minart.runtime.Resource
import eu.joaocosta.minart.runtime.pure.RIO

object JsSoundPlayer extends SoundPlayer {

  type AudioResource = Audio

  def loadClip(resource: Resource): AudioResource = {
    val elem = dom.document.createElement("audio").asInstanceOf[Audio]
    elem.src = resource.path
    elem
  }

  def newChannel(): SoundPlayer.SoundChannel[AudioResource] = new SoundPlayer.SoundChannel[AudioResource] {

    private[this] var currentClip: Option[Audio] = None

    def playOnce(clip: Audio): RIO[Any, Unit] = RIO.suspend {
      currentClip.foreach(_.pause())
      currentClip = Some(clip)
      currentClip.foreach { clip =>
        clip.currentTime = 0
        clip.loop = false
        clip.play()
      }
    }

    def playLooped(clip: Audio): RIO[Any, Unit] = RIO.suspend {
      currentClip.foreach(_.pause())
      currentClip = Some(clip)
      currentClip.foreach { clip =>
        clip.currentTime = 0
        clip.loop = true
        clip.play()
      }
    }

    val stop: RIO[Any, Unit] = RIO.suspend(currentClip.foreach(_.pause()))
  }
}
