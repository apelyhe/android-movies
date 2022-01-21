package hu.homework.pelyheadam.entities

import hu.homework.pelyheadam.data.Result

// kotlin data class generated from json file
// source: https://plugins.jetbrains.com/plugin/9960-json-to-kotlin-class-jsontokotlinclass-
data class MovieResult(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)

