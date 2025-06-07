package com.github.ivpal

import kotlin.math.sqrt

class Sphere(private val center: Point3, private val radius: Double, private val material: Material): Hittable {
    override fun hit(ray: Ray, interval: Interval): HitRecord? {
        val oc = center - ray.origin
        val a = ray.direction.lengthSquared
        val h = dot(ray.direction, oc)
        val c = oc.lengthSquared - radius * radius

        val discriminant = h * h - a * c
        if (discriminant < 0) {
            return null
        }

        val sqrtd = sqrt(discriminant)
        var root = (h - sqrtd) / a
        if (!interval.surrounds(root)) {
            root = (h + sqrtd) / a
            if (!interval.surrounds(root)) {
                return null
            }
        }

        val point = ray.at(root)
        val outwardNormal =  (point - center) / radius
        return HitRecord(
            p = point,
            t = root,
            material = material
        ).also { it.setFaceNormal(ray, outwardNormal) }
    }
}
