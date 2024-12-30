package linus.ardell.firedetector.DataClass

data class MainData(
    var autoMode: Boolean = false,
    var pumpStatus : Boolean = false,
    var sensorStatus : Boolean = false,
    var dateTime : String = ""
)
