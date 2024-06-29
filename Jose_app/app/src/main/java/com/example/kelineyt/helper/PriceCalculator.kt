package com.example.kelineyt.helper

fun Float?.getProductPrice(price: Float): Float {
    //this --> PErcentage
    if (this == null) {
        return price
    }

    val remainingPricePercentage = 1f - this
    return remainingPricePercentage * price
}