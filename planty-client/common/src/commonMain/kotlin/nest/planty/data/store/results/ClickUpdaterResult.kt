package nest.planty.data.store.results

sealed class ClickUpdaterResult {
    data class Success(val currentValue: Int) : ClickUpdaterResult()

    sealed class Failure : ClickUpdaterResult() {
        data class Error(val throwable: Throwable) : Failure()
        data object Invalid : Failure()
    }
}