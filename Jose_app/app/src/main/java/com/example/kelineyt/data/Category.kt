package com.example.kelineyt.data

sealed class Category(val category: String){

    object Chair: Category("Chair")
    object Accessory: Category("Accessory")
    object Cupboard: Category("Cupboard")
    object Furniture: Category("Furniture")
    object Table: Category("Table")


}