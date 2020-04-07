package ch.silvannellen.umvvm.view

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.silvannellen.umvvm.viewmodel.BaseViewModel

abstract class BaseViewActivity : FragmentActivity() {
    // Used internally to keep track of all the view models that were created and have to be observed.
    // Visibility "protected" because it's used in inline function.
    protected val viewModels = mutableSetOf<BaseViewModel>()

    /**
     * The layout id of the layout resource to be inflated in onCreate.
     */
    protected abstract val layoutResourceId: Int

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutResourceId)
        initialiseView(savedInstanceState)
        createViewModels()
        viewModels.forEach { observeViewModel(it) }
    }


    /**
     * Initialize the view: set click listeners, configure child views, set adapters, default values in fields, restore state...
     * Called before the view models are created.
     */
    protected abstract fun initialiseView(savedInstanceState: Bundle?)

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