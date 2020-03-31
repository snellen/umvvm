package ch.nellen.silvan.mvvm.view

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.nellen.silvan.mvvm.viewmodel.BaseViewModel

/**
 * Base class for fragments that use this MVVM framework.
 */
abstract class BaseViewFragment : Fragment() {

    // Used internally to keep track of all the view models that were created and have to be observed.
    // Visibility "protected" because it's used in inline function.
    protected val viewModels = mutableSetOf<BaseViewModel>()

    protected class BaseViewModelFactory<VM : BaseViewModel>(
        private val creator: (() -> VM),
        private val initializer: ((VM) -> Unit)?
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>) =
            creator().apply(initializer ?: {}) as T
    }

    /**
     * Use this function to create view models in createViewModel().
     *
     * @param viewModelCreator a function that creates a view model.
     * @param initializer a function that initialises the view model after it's been created.
     */
    protected inline fun <reified VM : BaseViewModel> createViewModel(
        noinline viewModelCreator: (() -> VM),
        noinline initializer: ((VM) -> Unit)? = null
    ): VM = ViewModelProvider(
        this,
        BaseViewModelFactory(viewModelCreator, initializer)
    ).get(VM::class.java).also { viewModels.add(it) }

    /**
     * Use this function to create view models in createViewModel(). The view model returned by this method
     * are scoped to the activity. This means that the same instance is shared between all the fragments hosted by
     * an activity. See https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
     *
     * @param viewModelCreator a function that creates a view model.
     * @param initializer a function that initialises the view model after it's been created.
     */
    protected inline fun <reified VM : BaseViewModel> createActivityScopedViewModel(
        noinline viewModelCreator: (() -> VM),
        noinline initializer: ((VM) -> Unit)? = null
    ): VM = activity?.run {
        ViewModelProvider(
            this,
            BaseViewModelFactory(viewModelCreator, initializer)
        ).get(VM::class.java).also { viewModels.add(it) }
    }
        ?: throw IllegalStateException("Cannot create activity scoped view model at this point in the Fragment lifecycle. This method is designed to be used inside createViewModels().")


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createViewModels()
        viewModels.forEach { observeViewModel(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseView(view, savedInstanceState)
    }

    /**
     * Initialize the view: set click listeners, configure child views, set adapters, default values in fields, restore state...
     * Called before the view models are created.
     */
    protected abstract fun initialiseView(view: View, savedInstanceState: Bundle?)

    /**
     * Create all your view models in this function.
     */
    protected abstract fun createViewModels()

    /**
     * Called to observe a view model that was created in createViewModels() using the createViewModel(...) helper function.
     *
     * @param viewModel the view model that was created in createViewModel()
     */
    @CallSuper
    protected open fun observeViewModel(viewModel: BaseViewModel) {
    }

}