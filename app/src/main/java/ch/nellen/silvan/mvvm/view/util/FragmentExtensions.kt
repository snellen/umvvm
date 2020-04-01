package ch.nellen.silvan.mvvm.view.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Utility function that observes live data using the correct lifecycle owner (see https://proandroiddev.com/5-common-mistakes-when-using-architecture-components-403e9899f4cb)
 */
fun <T> Fragment.observe(data: LiveData<T>, action: (T?) -> Unit) {
    data.observe(viewLifecycleOwner, Observer(action))
}