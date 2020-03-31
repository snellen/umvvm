package ch.nellen.silvan.mvvm

import ch.nellen.silvan.mvvm.testutil.callOnCleared
import ch.nellen.silvan.mvvm.viewmodel.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Test
import java.util.concurrent.Executors
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test BaseViewModel. The tests mainly test the behaviour of the Kotlin coroutine framework, but
 * they are still useful to develop an understanding of how coroutines work under the hood.
 */
class BaseViewModelTest {

    private fun overrideMainDispatcher(mockMainDispatcher: CoroutineDispatcher) {
        Dispatchers.setMain(mockMainDispatcher)
    }

    private fun resetMainDispatcher(mockMainDispatcher: ExecutorCoroutineDispatcher? = null) {
        Dispatchers.resetMain()
        mockMainDispatcher?.close()
    }

    @Test
    fun whenCoroutineIsLaunched_itRunsOnTheMainThreadByDefault() {
        // Given
        class ThreadTester : BaseViewModel() {

            var threadName: String? = null

            fun launchCoroutineAndWaitForCompletion() {
                // 1. Launch coroutine as you would normally
                val job = launch {
                    threadName = Thread.currentThread().name
                }
                // 2. Block thread to wait for the coroutine to finish.
                runBlocking { job.join() }
            }
        }

        val mockMainThreadName = "This is the mock main thread!"
        val mockMainDispatcher =
            Executors.newSingleThreadExecutor { r -> Thread(r, mockMainThreadName) }
                .asCoroutineDispatcher()
        overrideMainDispatcher(mockMainDispatcher)
        val testee = ThreadTester()

        // When
        testee.launchCoroutineAndWaitForCompletion()

        // Then
        assertTrue(testee.threadName?.contains(mockMainThreadName) == true)
        resetMainDispatcher(mockMainDispatcher)
    }

    @Test
    fun whenCoroutineIsLaunchedAndViewModelCleared_CoroutineNeverFinishes() {
        // Given
        class CancellationTester : BaseViewModel() {

            var job: Job? = null
            val delayMs = 3600000L
            var completed = false

            fun launchCoroutine() {
                job = launch {
                    delay(delayMs)
                    completed = true
                }
            }
        }

        val mockDispatcher = TestCoroutineDispatcher()
        overrideMainDispatcher(mockDispatcher)
        val testee = CancellationTester()
        testee.launchCoroutine()

        // When
        testee.callOnCleared()
        mockDispatcher.advanceTimeBy(testee.delayMs * 2)

        // Then
        assertTrue(testee.job?.isCancelled == true)
        assertFalse(testee.completed)
        resetMainDispatcher()
    }
}
