package com.github.ivpal

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextDouble

class Vec3(val x: Double, val y: Double, val z: Double) {
    val length: Double
        get() = sqrt(lengthSquared)

    val lengthSquared: Double
        get() = x * x + y * y + z * z

    operator fun plus(v: Vec3): Vec3 = Vec3(x + v.x, y + v.y, z + v.z)

    operator fun minus(v: Vec3): Vec3 = Vec3(x - v.x, y - v.y, z - v.z)

    operator fun times(f: Double): Vec3 = Vec3(x * f, y * f, z * f)

    operator fun times(i: Int): Vec3 = Vec3(x * i, y * i, z * i)

    operator fun div(f: Double): Vec3 = Vec3(x / f, y / f, z / f)

    operator fun div(i: Int): Vec3 = Vec3(x / i, y / i, z / i)

    operator fun unaryMinus(): Vec3 = Vec3(-x, -y, -z)

    fun nearZero(): Boolean = abs(x) < 1e-8 && abs(y) < 1e-8 && abs(z) < 1e-8

    override fun toString(): String {
        return "Vec3(x=$x, y=$y, z=$z)"
    }

    companion object {
        val ZERO = Vec3(0.0, 0.0, 0.0)

        fun random(): Vec3 = Vec3(nextDouble(), nextDouble(), nextDouble())

        fun random(min: Double, max: Double): Vec3 = Vec3(nextDouble(min, max), nextDouble(min, max), nextDouble(min, max))
    }
}

fun dot(u: Vec3, v: Vec3): Double = u.x * v.x + u.y * v.y + u.z * v.z

fun cross(u: Vec3, v: Vec3): Vec3 = Vec3(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x * v.y - u.y * v.x)

fun unitVector(v: Vec3): Vec3 = v / v.length

fun reflect(v: Vec3, normal: Vec3): Vec3 = v - normal * 2 * dot(v, normal)

fun refract(uv: Vec3, normal: Vec3, etaiOverEtat: Double): Vec3 {
    val cosTheta = min(dot(-uv, normal), 1.0)
    val rOutPerp = (normal * cosTheta + uv) * etaiOverEtat
    val rOutParallel = normal * (-sqrt(abs(1.0 - rOutPerp.lengthSquared)))
    return rOutPerp + rOutParallel
}

fun randomUnitVector(): Vec3 {
    while (true) {
        val p = Vec3.random()
        val lensq = p.lengthSquared
        if (lensq > 1e-160 && lensq <= 1) {
            return p / sqrt(lensq)
        }
    }
}

fun randomInUnitDisk(): Vec3 {
    while (true) {
        val p = Vec3(nextDouble(-1.0, 1.0), nextDouble(-1.0, 1.0), 0.0)
        if (p.lengthSquared < 1) {
            return p
        }
    }
}

fun randomOnHemisphere(normal: Vec3): Vec3 {
    val onUnitSphere = randomUnitVector()
    if (dot(onUnitSphere, normal) > 0.0) {
        return onUnitSphere
    }
    return -onUnitSphere
}

typealias Point3 = Vec3
