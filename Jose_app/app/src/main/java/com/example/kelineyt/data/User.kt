package com.example.kelineyt.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath: String = ""

){
    /*
    user will be able to upload profeile image after sign up thats why imagePath is an empty string
     */


    //firebase will use it
    constructor() : this("","","","")



}
