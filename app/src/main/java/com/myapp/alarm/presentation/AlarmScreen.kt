package com.myapp.alarm.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.myapp.alarm.R
import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.util.AlarmPermissionContract
import com.myapp.alarm.util.Constants
import com.myapp.alarm.util.openAppSettings
import com.myapp.alarm.util.pickedTimeformat
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "InlinedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(
    viewmodel: AlarmViewmodel
) {
    val state by viewmodel.alarmstate.collectAsState()
    val  context = LocalContext.current
    var alarm = viewmodel.alarm
     var addLabeldialog by remember {
         mutableStateOf(false)
     }
    var showReminderPermissionRationale: Boolean by remember {
        mutableStateOf(false)
    }

    var showNotificationPermissionRationale: Boolean by remember {
        mutableStateOf(false)
    }

    var isNotificationPermissionDeclined: Boolean by remember {
        mutableStateOf(false)
    }

    val scheduleExactAlarmPermissionLauncher = rememberLauncherForActivityResult(
        contract = AlarmPermissionContract(),
        onResult = {isGranted->
            if (isGranted){
                alarm.let {
                    viewmodel.insertAlarm(it)
                }
            }

        }
    )
    val postNotificationsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                alarm.let {
                    viewmodel.insertAlarm(it)
                }
            } else {
                isNotificationPermissionDeclined = true
            }
        }
    )
    LaunchedEffect(key1 = true) {
        viewmodel.eventflow.collect { event ->
            when (event) {
                is AlarmScreenEvent.AlarmSetSuccessfully -> {

                }

                is AlarmScreenEvent.AlarmNotSet -> {

                }

                is AlarmScreenEvent.PermissionToSetReminderNotGranted -> {
                    showReminderPermissionRationale = true
                }

                AlarmScreenEvent.PermissionToSendNotificationsNotGranted -> {
                    showNotificationPermissionRationale = true
                }

                AlarmScreenEvent.PermissionsToSendNotificationsAndSetReminderNotGranted -> {
                    showReminderPermissionRationale = true
                    showNotificationPermissionRationale = true
                }

                else -> {}
            }
        }
    }


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
                           {viewmodel.deleteAlarm(alarm)}, onToggle = { scheduled ->
                               viewmodel.updateAlarm(Alarm(id = alarm.id ,time=alarm.time, isScheduled = scheduled, isVibrate = true))

                           },

                           )



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
                initialTime = LocalTime.now(),
                title = "Pick a time",
                timeRange = LocalTime.MIDNIGHT ..LocalTime.MAX
            ) {
                pickedTime = it
            }
        }
        if (showReminderPermissionRationale) {
            ShowPermissionRationaleDialog(
                title = R.string.reminder_permission_required,
                content = R.string.alarm_permission_rationale,
                onDismissClick = {
                    showReminderPermissionRationale = false
                },
                onConfirmClick = {
                    showReminderPermissionRationale = false
                    scheduleExactAlarmPermissionLauncher.launch(
                        Manifest.permission.SCHEDULE_EXACT_ALARM
                    )
                },
                modifier = Modifier
            )
        }

        if (showNotificationPermissionRationale) {
            ShowPermissionRationaleDialog(
                title = R.string.notification_permission_required,
                content = R.string.notification_permission_rationale,
                onDismissClick = {
                    showNotificationPermissionRationale = false
                },
                onConfirmClick = {
                    showNotificationPermissionRationale = false
                    if (!isNotificationPermissionDeclined) {
                        postNotificationsPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } else {
                        (context as Activity).openAppSettings()
                    }
                }
            )
        }

        if (isNotificationPermissionDeclined) {
            ShowPermissionRationaleDialog(
                title = R.string.notification_permission_required,
                content = R.string.notification_permission_mandatory_rationale,
                onDismissClick = {
                },
                onConfirmClick = {
                    (context as Activity).openAppSettings()
                }
            )
        }
    }

}
@Composable
fun ShowPermissionRationaleDialog(
    @StringRes
    title: Int,
    @StringRes
    content: Int,
    modifier: Modifier = Modifier,
    onDismissClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissClick = onDismissClick,
        onConfirmClick = onConfirmClick,
        title = title,
        content = content,
        modifier = modifier
    )
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmItem(alarm: Alarm, onclick : () -> Unit, onToggle:(Boolean)->Unit, ){

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),) {

//        Row(modifier = Modifier.padding(start = 0.5.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
//            IconButton(onClick = addLabel) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "add label")
//            }
//            Text(text = "add label", fontSize = 14.sp, fontWeight = FontWeight.ExtraLight)
//        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = pickedTimeformat(alarm.time), fontSize = 35.sp,
                fontWeight = FontWeight.Bold,)
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
