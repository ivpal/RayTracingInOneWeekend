package com.github.ivpal

import kotlin.random.Random.Default.nextDouble

fun makeWorld(): HittableList {
    val groundMaterial = Lamberitan(Color(0.5, 0.5, 0.5))
    val world = HittableList(
        mutableListOf(
            Sphere(
                center = Point3(0.0, -1000.0, 0.0),
                radius = 1000.0,
                material = groundMaterial
            )
        )
    )

    for (a in -11 ..< 11) {
        for (b in -11 ..< 11) {
            val chooseMaterial = nextDouble()
            val center = Point3(a + nextDouble() * 0.9, 0.2, b + nextDouble() * 0.9)
            if ((center - Point3(4.0, 0.2, 0.0)).length > 0.9) {
                val material = when {
                    chooseMaterial < 0.8 -> {
                        val albedo = Color.random() * Color.random()
                        Lamberitan(albedo)
                    }
                    chooseMaterial < 0.95 -> {
                        val albedo = Color.random(0.5, 1.0)
                        val fuzz = nextDouble(0.0, 0.5)
                        Metal(albedo, fuzz)
                    }
                    else -> {
                        Dielectric(1.5)
                    }
                }
                world.add(
                    Sphere(
                        center = center,
                        radius = 0.2,
                        material = material
                    )
                )
            }
        }
    }

    world.add(
        Sphere(
            center = Point3(0.0, 1.0, 0.0),
            radius = 1.0,
            material = Dielectric(1.5)
        ),
        Sphere(
            center = Point3(-4.0, 1.0, 0.0),
            radius = 1.0,
            material = Lamberitan(Color(0.4, 0.2, 0.1))
        ),
        Sphere(
            center = Point3(4.0, 1.0, 0.0),
            radius = 1.0,
            material = Metal(Color(0.7, 0.6, 0.5))
        ),
    )

    return world
}

fun main() {
    val camera = Camera(
        aspectRatio = 16.0 / 9.0,
        imageWidth = 1200,
        samplesPerPixel = 500,
        maxDepth = 50,
        lookFrom = Point3(13.2, 2.0, 3.0),
        lookAt = Point3(0.0, 0.0,  0.0),
        vup = Vec3(0.0, 1.0, 0.0),
        verticalFov = 20.0,
        defocusAngle = 0.6,
        focusDist = 10.0,
        coroutinesCount = 10
    )

    camera.render(makeWorld())
}
