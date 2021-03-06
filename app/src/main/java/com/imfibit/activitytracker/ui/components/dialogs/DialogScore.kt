package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.DatetimeEditor
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DialogScore(
    display: MutableState<Boolean> = mutableStateOf(true),
    score: Long,
    datetime: LocalDateTime,
    recordId: Long = 0,
    activityId: Long = 0,
    onSetSession: ((LocalDateTime, Long)->Unit)? = null
) {
    val modify = remember {recordId != 0L}
    val score = remember { mutableStateOf(if (modify) score.toInt() else 1) }
    val datetime = remember { mutableStateOf(datetime) }

    BaseDialog(display = display) {

        DialogBaseHeader(title = stringResource(id = if (modify) R.string.dialo_score_title_edit else R.string.dialo_score_title_add))

        NumberSelector(label = stringResource(id = R.string.score), number = score){
            if ( it in 1..999) score.value = it
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.height(15.dp),
                textAlign = TextAlign.Center,
                text = "time",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )

            Box(
                Modifier.height(30.dp).background(Colors.ChipGray, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                DatetimeEditor(datetime = datetime)
            }
        }


        Buttons(modify, display, recordId, activityId, onSetSession, datetime, score)
    }

}

@Composable
private fun Buttons(
    modify: Boolean,
    display: MutableState<Boolean>,
    recordId: Long,
    activityId: Long,
    onSetSession: ((LocalDateTime, Long) -> Unit)?,
    datetime: MutableState<LocalDateTime>,
    input: MutableState<Int>
) = DialogButtons {

    if (modify){
        TextButton(
            onClick = {
                display.value = false
                GlobalScope.launch {
                    RepositoryTrackedActivity().scoreDAO.deleteById(recordId)
                }
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_delete))
        }
    }

    TextButton(onClick = {  display.value = false }) {
        Text(text = stringResource(id = R.string.dialog_action_cancel))
    }

    TextButton(onClick = {
        display.value = false

        if (onSetSession == null){
            require(activityId != 0L)

            GlobalScope.launch {
                val rep = RepositoryTrackedActivity()
                val item = TrackedActivityScore(recordId, activityId, datetime.value, input.value.toLong())

                if (recordId != 0L)
                    rep.scoreDAO.update(item)
                else
                    rep.scoreDAO.insert(item)
            }
        }else{
            onSetSession.invoke(datetime.value, input.value.toLong())
        }


    }) {
        Text(text = stringResource(id = if (modify) R.string.dialog_action_edit else R.string.dialog_action_add))

    }


}