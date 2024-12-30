package linus.ardell.firedetector.DataClass

data class HistoryData(val currentTimeStart: String = "",
                       val currentTimeEnd: String = "",
                       val pumpStatus: Int = 0,
                       val sensorStatus: Int = 0)
