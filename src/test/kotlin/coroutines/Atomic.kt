package coroutines

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun AtomicBoolean(initial: Boolean = false) = object: ReadWriteProperty<Any?, Boolean> {
    private val internal = java.util.concurrent.atomic.AtomicBoolean(initial)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = internal.get()

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        internal.set(value)
    }
}

