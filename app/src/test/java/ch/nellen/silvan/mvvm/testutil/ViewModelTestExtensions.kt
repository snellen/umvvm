package ch.nellen.silvan.mvvm.testutil

import androidx.lifecycle.ViewModel
import kotlin.reflect.jvm.isAccessible

/**
 * Unfortunately, the Android devs decided to make ViewModel.onCleared() private.
 * This extension function allows the tests to call onCleared on a view model.
 */
fun ViewModel.callOnCleared() {
    ViewModel::class.members
        .single { it.name == "onCleared" }
        .apply { isAccessible = true }
        .call(this)
}