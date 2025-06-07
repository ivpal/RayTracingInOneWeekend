package com.github.ivpal

class Ray(private val orig: Point3, private val dir: Vec3) {
    val origin: Point3
        get() = orig

    val direction: Vec3
        get() = dir

    fun at(t: Double): Point3 = orig + dir * t
}
