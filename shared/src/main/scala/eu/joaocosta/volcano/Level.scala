package eu.joaocosta.volcano

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image._
import eu.joaocosta.minart.runtime._

final case class Level(tiles: Vector[Vector[Int]], tileset: SpriteSheet) {
  val height = tiles.size
  val width = tiles.map(_.size).maxOption.getOrElse(0)

  lazy val surface: RamSurface = {
    val acc = new RamSurface(Vector.fill(height * 16)(Array.fill(width * 16)(Color(0, 0, 0))))
    for {
      (line, y) <- tiles.zipWithIndex
      (sprite, x) <- line.zipWithIndex
      if (sprite != 0)
    } acc.blit(tileset.getSprite(sprite - 1))(x * 16, y * 16)
    acc
  }
}

object Level {
  def load(levelFile: Resource, tileset: SpriteSheet): Level = {
    Level(levelFile.withSource { source =>
      source.getLines().map { line =>
        line.map(c => c.toString.toInt).toVector
      }.toVector
    }.get, tileset)
  }
}
