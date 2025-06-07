package com.github.ivpal

data class HitRecord(val p: Point3, val t: Double, val material: Material, var frontFace: Boolean = false) {
    lateinit var normal: Vec3

    fun setFaceNormal(ray: Ray, outwardNormal: Vec3) {
        frontFace = dot(ray.direction, outwardNormal) < 0
        normal = if (frontFace) outwardNormal else -outwardNormal
    }
}

interface Hittable {
    fun hit(ray: Ray, interval: Interval): HitRecord?
}

class HittableList(private val list: MutableList<Hittable>) : Hittable {
    fun add(hittable: Hittable) {
        list.add(hittable)
    }

    fun add(vararg hittable: Hittable) {
        list.addAll(hittable)
    }

    override fun hit(ray: Ray, interval: Interval): HitRecord? {
        var hitRecord: HitRecord? = null
        var hit: HitRecord?
        var closestSoFar = interval.max
        list.forEach { obj ->
            hit = obj.hit(ray, Interval(interval.min, closestSoFar))
            if (hit != null) {
                hitRecord = hit
                closestSoFar = requireNotNull(hitRecord.t)
            }
        }
        return hitRecord
    }
}