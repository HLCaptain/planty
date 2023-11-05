package nest.planty.data.store.results

sealed class ClickUpdaterResult {
    sealed class Success : ClickUpdaterResult() {
        data object Incremented : Success()
        data object Reset : Success()
    }

    sealed class Failure : ClickUpdaterResult() {
        data class Error(val throwable: Throwable) : Failure()
        data object Invalid : Failure()
    }
}