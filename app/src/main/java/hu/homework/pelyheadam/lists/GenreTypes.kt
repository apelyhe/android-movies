package hu.homework.pelyheadam.lists

import hu.homework.pelyheadam.entities.Genre

class GenreTypes {
    private var types = ArrayList<Genre>()

    // which contains each genre with its ID
    // source: https://www.themoviedb.org/talk/5daf6eb0ae36680011d7e6ee
    init {
        types.add(Genre(28, "Akció"))
        types.add(Genre(12, "Kaland"))
        types.add(Genre(16, "Animáció"))
        types.add(Genre(35, "Vígjáték"))
        types.add(Genre(80, "Krimi"))
        types.add(Genre(99, "Dokumentum"))
        types.add(Genre(18, "Dráma"))
        types.add(Genre(10751, "Családi"))
        types.add(Genre(14, "Fantázia"))
        types.add(Genre(36, "Történelmi"))
        types.add(Genre(27, "Horror"))
        types.add(Genre(10402, "Zene"))
        types.add(Genre(9648, "Misztérikus"))
        types.add(Genre(10749, "Romantikus"))
        types.add(Genre(878, "Sci-fi"))
        types.add(Genre(10770, "TV"))
        types.add(Genre(53, "Thriller"))
        types.add(Genre(10752, "Háborús"))
        types.add(Genre(37, "Western"))

    }

    public fun getAllType() : ArrayList<Genre> {
        return types
    }

    public fun getGenreById(id: Int) : String? {
        for (genre: Genre in types) {
            if (genre.id == id) {
                return genre.name
            }
        }
        return null
    }
}