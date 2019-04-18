package com.jazz.archseekbar

internal fun <T : Number> bound(min: T, value: T, max: T) = when {
    value.toDouble() > max.toDouble() -> max
    value.toDouble() < min.toDouble() -> min
    else -> value
}