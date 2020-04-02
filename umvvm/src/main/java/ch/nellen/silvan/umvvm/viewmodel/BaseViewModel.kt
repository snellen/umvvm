package ch.nellen.silvan.umvvm.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Base class for view models.
 *
 * Note that this class is a coroutine scope that runs coroutines on the main thread by default. This
 * means the code in launch {...} or async {} will immediately be executed on the main thread. When the
 * view model is dismissed (i.e. destroyed for good), any coroutines launched will be cancelled.
 */
open class BaseViewModel : ViewModel(), CoroutineScope {
    private val supervisorJob = SupervisorJob()
    /**
     * Any coroutine launched in this BaseViewModel will run immediately on main thread and its
     * job will be a child of supervisorJob.
     */
    override val coroutineContext = Dispatchers.Main.immediate + supervisorJob

    override fun onCleared() {
        super.onCleared()
        supervisorJob.cancel()
    }
}