package com.example.ecommerceapp

class EProduct {
    var id:Int
    var name:String
    var price:Int
    val picture:String

    constructor(id:Int, name:String, price:Int, picture:String) {
        this.id = id
        this.name = name
        this.price = price
        this.picture = picture
    }
}