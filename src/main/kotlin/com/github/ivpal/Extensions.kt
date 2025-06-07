package com.github.ivpal

import kotlin.math.PI

fun StringBuilder.writeColor(c: Color) {
    val intensity = Interval(0.0, 0.999)
    val ir = (256 * intensity.clamp(linearToGamma(c.r))).toInt()
    val ig = (256 * intensity.clamp(linearToGamma(c.g))).toInt()
    val ib = (256 * intensity.clamp(linearToGamma(c.b))).toInt()
    append("$ir $ig $ib\n")
}

fun degreesToRadians(d: Double): Double = d * PI / 180.0
