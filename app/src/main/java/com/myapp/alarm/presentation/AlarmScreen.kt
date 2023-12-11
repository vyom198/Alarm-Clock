package com.myapp.alarm.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapp.alarm.data.model.Alarm
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(
    viewmodel: AlarmViewmodel
) {
    val state by viewmodel.alarmstate.collectAsState()
    var pickedTime by remember {
        mutableStateOf(LocalTime.now())
    }


    val timeDialogState = rememberMaterialDialogState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Alarm")})
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { timeDialogState.show() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "null")
            }
        }
    ) { paddingValues ->
       Column(
           modifier = Modifier.padding(paddingValues)
       ) {
           
           if (state.error != null){
               Text(text = "error")
           }
           if (state.isLoading){
               Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                   CircularProgressIndicator()  
               }
           }else{
               state.data.let { alarmlist->
                   LazyColumn{
                       items(alarmlist){alarm->

                           AlarmItem (alarm, onclick =
                           {viewmodel.deleteAlarm(alarm)}, onToggle = {
                               viewmodel.insertAlarm(alarm.copy(isScheduled = it))
                           })



                       }

                   }
               } 
           }
              

       }

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                   val alarm = Alarm(time = pickedTime, isScheduled = true , isVibrate = true)
                    viewmodel.insertAlarm(alarm)
                }
                negativeButton(text = "Cancel"){
                    timeDialogState.hide()
                }
            }
        ) {
            timepicker(
                initialTime = pickedTime,
                title = "Pick a time",
                timeRange = LocalTime.MIDNIGHT..LocalTime.NOON
            ) {
                pickedTime = it
            }
        }
    }




}

@RequiresApi(Build.VERSION_CODES.O)
fun pickedTimeformat(time: LocalTime):String{
    return DateTimeFormatter
        .ofPattern("hh:mm a")
        .format(time)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmItem(alarm: Alarm, onclick : () -> Unit, onToggle:(Boolean)->Unit) {

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = pickedTimeformat(alarm.time), fontSize = 19.sp, fontWeight = FontWeight.Bold,  )
            IconButton(onClick = onclick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {

            if (alarm.isScheduled){
                Text(text = "Scheduled")
            }else{
                Text(text = "Not Scheduled")
            }
            
            Switch(checked = alarm.isScheduled, onCheckedChange ={
                onToggle(it)
            } )

        }




    }

}
