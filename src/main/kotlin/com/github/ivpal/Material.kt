package com.github.ivpal

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

typealias ScatterResult = Pair<Ray, Color>

abstract class Material(open val albego: Color) {
    abstract fun scatter(rayIn: Ray, record: HitRecord): ScatterResult?
}

class Lamberitan(albego: Color): Material(albego) {
    override fun scatter(rayIn: Ray, record: HitRecord): ScatterResult {
        var scatterDirection = record.normal + randomUnitVector()
        if (scatterDirection.nearZero()) {
            scatterDirection = record.normal
        }
        return Ray(record.p, scatterDirection) to albego
    }
}

class Metal(override val albego: Color, private val fuzz: Double = 0.0): Material(albego) {
    override fun scatter(rayIn: Ray, record: HitRecord): ScatterResult? {
        var reflected = reflect(rayIn.direction, record.normal)
        reflected = unitVector(reflected) + (randomUnitVector() * fuzz)
        val scattered = Ray(record.p, reflected)
        if (dot(scattered.direction, record.normal) <= 0.0) {
            return null
        }
        return Ray(record.p, reflected) to albego
    }
}

class Dielectric(private val refractionIndex: Double): Material(Color(1.0, 1.0, 1.0)) {
    override fun scatter(rayIn: Ray, record: HitRecord): ScatterResult {
        val ri = if (record.frontFace) 1.0 / refractionIndex else refractionIndex
        val unitDirection = unitVector(rayIn.direction)

        val cosTheta = min(dot(-unitDirection, record.normal), 1.0)
        val sinTheta = sqrt(1.0 - cosTheta * cosTheta)
        val cannotRefract = ri * sinTheta > 1.0

        val direction = if (cannotRefract || reflectance(cosTheta, ri) > Random.nextDouble()) {
            reflect(unitDirection, record.normal)
        } else {
            refract(unitDirection, record.normal, ri)
        }

        return Ray(record.p, direction) to albego
    }

    private fun reflectance(cosine: Double, refractionIndex: Double): Double {
        val r0 = (1 - refractionIndex) / (1 + refractionIndex).also { it * it }
        return r0 + (1 - r0) * (1 - cosine).pow(5)
    }
}
