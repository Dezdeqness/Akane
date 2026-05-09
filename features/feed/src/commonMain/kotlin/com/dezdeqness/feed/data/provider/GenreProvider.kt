package com.dezdeqness.feed.data.provider

import com.dezdeqness.feed.contract.model.GenreEntity

class GenreProvider {
    fun getGenres() = GENRES

    companion object {
        private val GENRES = listOf(
            GenreEntity(15, "Боевые искусства"),
            GenreEntity(24, "Вампиры"),
            GenreEntity(32, "Гарем"),
            GenreEntity(16, "Демоны"),
            GenreEntity(25, "Детектив"),
            GenreEntity(33, "Дзёсей"),
            GenreEntity(8, "Драма"),
            GenreEntity(17, "Игры"),
            GenreEntity(34, "Исекай"),
            GenreEntity(26, "Исторический"),
            GenreEntity(30, "Киберпанк"),
            GenreEntity(1, "Комедия"),
            GenreEntity(18, "Магия"),
            GenreEntity(2, "Меха"),
            GenreEntity(9, "Мистика"),
            GenreEntity(19, "Музыка"),
            GenreEntity(36, "Пародия"),
            GenreEntity(10, "Повседневность"),
            GenreEntity(27, "Приключения"),
            GenreEntity(3, "Психологическое"),
            GenreEntity(11, "Романтика"),
            GenreEntity(28, "Сверхъестественное"),
            GenreEntity(20, "Сёдзе"),
            GenreEntity(31, "Сёдзе-ай"),
            GenreEntity(5, "Сейнен"),
            GenreEntity(4, "Сёнен"),
            GenreEntity(12, "Спорт"),
            GenreEntity(21, "Супер сила"),
            GenreEntity(6, "Триллер"),
            GenreEntity(13, "Ужасы"),
            GenreEntity(22, "Фантастика"),
            GenreEntity(29, "Фэнтези"),
            GenreEntity(7, "Школа"),
            GenreEntity(14, "Экшен"),
            GenreEntity(23, "Этти"),
        )
    }
}
