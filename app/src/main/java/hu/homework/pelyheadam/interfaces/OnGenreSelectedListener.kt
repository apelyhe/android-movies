package hu.homework.pelyheadam.interfaces

import hu.homework.pelyheadam.entities.Genre

interface OnGenreSelectedListener {
    fun onGenreSelected(genre: Genre?)
}