package ch.silvannellen.umvvm.view

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ch.silvannellen.umvvm.view.internal.BaseViewModelFactory
import ch.silvannellen.umvvm.viewmodel.BaseViewModel

/**
 * Base class for fragments that use the uMVVM framework.
 */
abstract class BaseViewFragment : Fragment() {

    // Used internally to keep track of all the view models that were created and have to be observed.
    // Visibility "protected" because it's used in inline function.
    protected val viewModels = mutableSetOf<BaseViewModel>()

    /**
     * Use this function to create view models in createViewModel().
     *
     * @param viewModelCreator a function that creates a view model.
     * @param initializer a function that initialises the view model after it's been created.
     */
    protected inline fun <reified VM : BaseViewModel> createViewModel(
        noinline viewModelCreator: (() -> VM),
        noinline initializer: ((VM) -> Unit)? = null
    ): VM = viewModels<VM>{
        BaseViewModelFactory(
            viewModelCreator,
            initializer
        )
    }.value.also { viewModels.add(it) }

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
    ): VM = activityViewModels<VM> {
        BaseViewModelFactory(
            viewModelCreator,
            initializer
        )
    }.value.also { viewModels.add(it) }

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