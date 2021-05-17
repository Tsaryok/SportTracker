package by.bsuir.sporttracker.di

import by.bsuir.sporttracker.module.TrackBuilder
import by.bsuir.sporttracker.module.Tracker
import org.koin.dsl.module

val checkLocationModule = module {
    single { TrackBuilder(get()) }
    single { Tracker(get()) }
}