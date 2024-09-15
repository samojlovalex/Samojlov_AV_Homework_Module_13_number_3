package com.example.samojlov_av_homework_module_13_number_3

data class Person(
    val name: String,
    val surname: String,
    val age: String,
    val post: String
) {
    override fun toString(): String {
        return "$name $surname\n$age лет"
    }
}