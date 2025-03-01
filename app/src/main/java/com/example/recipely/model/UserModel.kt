package com.example.recipely.model

data class UserModel(
    var userId: String = "",
    var fullName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var address: String= ""
) {
}