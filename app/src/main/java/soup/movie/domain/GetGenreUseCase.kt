package soup.movie.domain

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import soup.movie.data.MoopRepository
import soup.movie.data.model.GenreFilter
import soup.movie.data.model.response.MovieListResponse

class GetGenreUseCase(
    private val repository: MoopRepository
) {

    fun getGenreList(): Observable<List<String>> {
        return Observables
            .zip(
                repository.getNowList(false).mapToGenreSet(),
                repository.getPlanList(false).mapToGenreSet(),
                ::merge
            )
            .take(1)
    }

    private fun MovieListResponse.toGenreSet(): Set<String> {
        return list
            .asSequence()
            .map { it.genre.orEmpty() }
            .flatMap { it.asSequence() }
            .toSet()
    }

    private fun Observable<MovieListResponse>.mapToGenreSet(): Observable<Set<String>> {
        return map { it.toGenreSet() }.onErrorReturnItem(emptySet())
    }

    private fun merge(set1: Set<String>, set2: Set<String>): List<String> {
        return mutableListOf<String>().apply {
            addAll(set1 + set2)
            add(GenreFilter.GENRE_ETC)
        }
    }
}
