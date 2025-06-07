package com.github.ivpal

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.PrintWriter
import java.util.concurrent.Executors
import kotlin.math.tan
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class Camera(
    val aspectRatio: Double = 1.0,
    val imageWidth: Int = 100,
    val samplesPerPixel: Int = 10,
    val maxDepth: Int = 10,
    val verticalFov: Double = 90.0,
    val lookFrom: Point3 = Point3(0.0, 0.0, 0.0),
    val lookAt: Point3 = Point3(0.0, 0.0, -1.0),
    val vup: Vec3 = Vec3(0.0, 1.0, 0.0),
    val defocusAngle: Double = 0.0,
    val focusDist: Double = 10.0,
    private val coroutinesCount: Int = 8
) {
    private var imageHeight: Int = 0
    private lateinit var center: Point3
    private lateinit var pixel00Loc: Vec3
    private lateinit var pixelDeltaU: Vec3
    private lateinit var pixelDeltaV: Vec3
    private var pixelsSamplesScale: Double = 0.0
    private lateinit var u: Vec3
    private lateinit var v: Vec3
    private lateinit var w: Vec3
    private lateinit var defocusDiskU: Vec3
    private lateinit var defocusDiskV: Vec3

    private lateinit var result: Array<Array<Color>>
    private var rowsInBatch: Int = 0

    fun render(world: Hittable) {
        initialize()

        val dispatcher = Executors.newFixedThreadPool(coroutinesCount).asCoroutineDispatcher()
        val millis = measureTimeMillis {
            runBlocking {
                coroutineScope {
                    for (i in 0..<coroutinesCount) {
                        launch(dispatcher)  { renderRows(world, i) }
                    }
                }
            }
        }

        println("Elapsed millis - $millis")

        val builder = StringBuilder("P3\n$imageWidth $imageHeight\n255\n")
        for (j in 0 ..< imageHeight) {
            for (i in 0 ..< imageWidth) {
                val pixelColor = result[j][i]
                builder.writeColor(pixelColor * pixelsSamplesScale)
            }
        }

        val imageName = "13_final.ppm"
        PrintWriter("./images/$imageName").use { pw ->
            pw.write(builder.toString())
        }

        dispatcher.close()
    }

    private fun renderRows(world: Hittable, n: Int) {
        val start = n * rowsInBatch
        val end =
            if (n == coroutinesCount - 1)
                imageHeight
            else
                start + rowsInBatch

        for (j in start..<end) {
            println("Coroutine $n, rows remaining - ${(end - j)}")

            for (i in 0 ..< imageWidth) {
                val pixelColor = Color(0.0, 0.0, 0.0)
                for (sample in 0..<samplesPerPixel) {
                    val ray = getRay(i, j)
                    pixelColor += rayColor(ray, world, maxDepth)
                }
                result[j][i] = pixelColor
            }
        }
    }

    private fun getRay(i: Int, j: Int): Ray {
        val offset = sampleSquare()
        val pixelSample = pixel00Loc + (pixelDeltaU * (i + offset.x)) + (pixelDeltaV * (j + offset.y))
        val rayOrigin = if (defocusAngle <= 0) center else defocusDiskSample()
        val rayDirection = pixelSample - rayOrigin
        return Ray(rayOrigin, rayDirection)
    }

    private fun sampleSquare(): Vec3 = Vec3(Random.nextDouble() - 0.5, Random.nextDouble() - 0.5, 0.0)

    private fun defocusDiskSample(): Point3 {
        val p = randomInUnitDisk()
        return center + (defocusDiskU * p.x) + (defocusDiskV * p.y)
    }

    private fun initialize() {
        imageHeight = (imageWidth / aspectRatio).toInt()
        center = lookFrom

        val theta = degreesToRadians(verticalFov)
        val h = tan(theta / 2)
        val viewportHeight = 2.0 * h * focusDist
        val viewportWidth = viewportHeight * (1.0 * imageWidth / imageHeight)

        w = unitVector(lookFrom - lookAt)
        u = unitVector(cross(vup, w))
        v = cross(w, u)

        val viewportU = u * viewportWidth
        val viewportV = -v * viewportHeight
        pixelDeltaU = viewportU / imageWidth
        pixelDeltaV = viewportV / imageHeight
        val viewportUpperLeft = center - (w * focusDist) - viewportU / 2 - viewportV / 2
        pixel00Loc = viewportUpperLeft + (pixelDeltaU + pixelDeltaV) * 0.5
        pixelsSamplesScale = 1.0 / samplesPerPixel

        val defocusRadius = focusDist * tan(degreesToRadians(defocusAngle / 2))
        defocusDiskU = u * defocusRadius
        defocusDiskV = v * defocusRadius

        result =  Array(imageHeight) { Array(imageWidth) { Color(0.0, 0.0, 0.0) }  }
        rowsInBatch = imageHeight / coroutinesCount
    }

    private fun rayColor(ray: Ray, world: Hittable, depth: Int): Color {
        if (depth <= 0) {
            return Color(0.0, 0.0, 0.0)
        }

        val hit = world.hit(ray, Interval(min = 0.001))
        if (hit != null) {
            val result = hit.material.scatter(ray, hit) ?: return Color(0.0, 0.0, 0.0)
            val (scattered, attenuation) = result
            return rayColor(scattered, world, depth - 1) * attenuation
        }

        val unitDirection = unitVector(ray.direction)
        val a = 0.5 * unitDirection.y + 1.0
        return Color(1.0, 1.0, 1.0) * (1 - a) + Color(0.5, 0.7, 1.0) * a
    }
}
