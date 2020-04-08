package ch.silvannellen.umvvm.view.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.silvannellen.umvvm.viewmodel.BaseViewModel

// Used internally, visibility "public" because it's used in inline function.
class BaseViewModelFactory<VM : BaseViewModel>(
    private val creator: (() -> VM),
    private val initializer: ((VM) -> Unit)?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        creator().apply(initializer ?: {}) as T
}