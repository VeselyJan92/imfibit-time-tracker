package com.janvesely.activitytracker.ui.activities

import android.util.Log
import androidx.lifecycle.*
import androidx.room.InvalidationTracker
import com.janvesely.activitytracker.core.swap
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.activitytracker.ui.other.dragandrop.ItemTouchHelperAdapter
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ActivitiesViewModel : ViewModel(), ItemTouchHelperAdapter {

    val rep = RepositoryTrackedActivity()

    val activities = MutableLiveData<List<TrackedActivityWithMetric>>()

    val live = rep.liveActive()


    init {
        val observer = object : InvalidationTracker.Observer(
            TrackedActivity.TABLE,
            TrackedActivitySession.TABLE,
            TrackedActivityCompletion.TABLE,
            TrackedActivityScore.TABLE
        ) {
            override fun onInvalidated(tables: MutableSet<String>) {
                viewModelScope.launch {
                    Log.e("ActivitiesViewModel", "IVALIDADED")
                    activities.postValue(rep.getAllActivities(5))

                }
            }
        }

        viewModelScope.launch {
            activities.postValue(rep.getAllActivities(5))
        }

        AppDatabase.db.invalidationTracker.addObserver(observer)

    }



    fun startSession(activity: TrackedActivity) {
        rep.update(activity.apply { in_session_since = LocalDateTime.now() })
    }

    fun stopSession(activity: TrackedActivity) {
        rep.update(activity.copy().apply { in_session_since = null})
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {

        Log.e("MOVE", "$fromPosition, $toPosition")
        val items = activities.value




        if (items != null && fromPosition != toPosition){
            val x = items.toMutableList().apply {
                swap(fromPosition, toPosition)
                forEachIndexed {index, item ->  item.activity.position = index  }
            }
            activities.value = x

            rep.transaction {
                x.forEach { rep.update(it.activity) }
            }
        }

        return false
    }

    override fun onItemDismiss(position: Int) {
        Log.e("DISMISS",  "")
    }

    override fun onCleared() {
        Log.e("CLEARED",  "")
    }



}