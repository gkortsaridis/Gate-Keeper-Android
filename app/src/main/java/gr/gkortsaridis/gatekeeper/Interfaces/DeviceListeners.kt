package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Device
import java.lang.Exception

interface DevicesRetrieveListener {
    fun onDevicesRetrieved(devices: ArrayList<Device>)
    fun onDeviceRetrieveError(exception: Exception)
}