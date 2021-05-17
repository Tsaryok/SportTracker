package by.bsuir.sporttracker.model

class Section(_segments: List<Segment>) {
    var segments = _segments
        private set
    var distanceInMeters = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        segments.stream().map { it.distanceInMeters }.reduce {s1, s2 -> s1 + s2}.get()
    } else {
        var sum: Float = 0F
        for (segment in segments){
            sum += segment.distanceInMeters
        }
        sum
    }
        private set

    var timeMilliseconds: Int = 0
        private set

    var speedKilometersPerHour: Float = 0F
        set(_speed) {
            field = _speed
            timeMilliseconds = (distanceInMeters * 3600 / _speed).toInt()
        }

    fun splitSection(id: Int): Section{
        val separatedSection = Section(segments.subList(0, id-1))
        segments = segments.subList(id, segments.size-1)
        updateDistance()
        return separatedSection
    }

    fun uniteSections(addedSection: Section){
        segments = listOf(segments, addedSection.segments).flatten()
        updateDistance()
    }

    private fun updateDistance(){
        distanceInMeters = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            segments.stream().map { it.distanceInMeters }.reduce {s1, s2 -> s1 + s2}.get()
        } else {
            var sum = 0F
            for (segment in segments){
                sum += segment.distanceInMeters
            }
            sum
        }
    }
}