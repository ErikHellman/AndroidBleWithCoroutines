package se.hellsoft.androidble

import android.bluetooth.BluetoothDevice
import java.lang.reflect.Method

fun BluetoothDevice.releaseBond() {
    val method: Method = this.javaClass.getMethod("removeBond")
    method.invoke(this)
}
