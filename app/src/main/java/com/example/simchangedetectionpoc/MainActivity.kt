package com.example.simchangedetectionpoc

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import com.example.simchangedetectionpoc.ui.theme.SharedPreference
import com.example.simchangedetectionpoc.ui.theme.SimChangeDetectionPOCTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreference.init(this)

        // Request permission to access the device's SIM card.
        requestPermissionForReadPhoneState(this)

        // Setup Views
        setUpViews(this)

        if (checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        saveSimDataInSharedPreferences(this)

        setContent {

            SimChangeDetectionPOCTheme {

                val context = LocalContext.current

                Surface(
//                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(20.dp)) {
                        Greeting("Android")
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {

                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_PHONE_STATE
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show()
                                }
                                saveSimDataInSharedPreferences(context)
                            },
                            modifier = Modifier.height(80.dp)

                        ) {
                            Text(text = "Check State")
                        }

                        Spacer(modifier = Modifier.height(20.dp))


                    }



                }
            }
        }
    }

    @Override
    override fun onResume() {
    super.onResume()
        requestPermissionForReadPhoneState(this)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimChangeDetectionPOCTheme {
        Greeting("Android")
    }
}



private fun setUpViews( context: Context) {
    val sharedPreferences = context.getSharedPreferences("simData", MODE_PRIVATE)
    val simData = sharedPreferences.getStringSet("simData", setOf())
    if (simData != null) {
        if (simData.isNotEmpty() && simData.size >= 1) {
        }
    }
}

@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
private fun saveSimDataInSharedPreferences( context: Context) {
    val subscriptionManager = getSystemService(context, SubscriptionManager::class.java)
    val infoList = subscriptionManager?.activeSubscriptionInfoList


    if (infoList != null) {

        for (index in infoList.indices) {

            val value = infoList[index]
            println("the element at $index is $value")

            println("icc id is : ${value.iccId} Sim slot is : ${value.getSimSlotIndex()}")

            val key = "sim${index}"

            val sim = SharedPreference.instance?.getString(key)


            println("sim : $sim")
            println("iconTint : ${value.iconTint.toString()}")
            if(sim != value.iconTint.toString()){
                Toast.makeText( context,"Sim slot ${value.getSimSlotIndex() + 1} changed", Toast.LENGTH_SHORT).show()
                SharedPreference.instance?.save(key, value.iconTint.toString())
            }


        }

        Toast.makeText(context, "${infoList.size} SIM Card Detected", Toast.LENGTH_SHORT).show()
        SharedPreference.instance?.save("simData", infoList.toString())

    } else {
        // Storing Empty Set in Shared Preferences
        // SharedPreference.instance?.save("simData", infoList.toString())
        Toast.makeText(context, "No SIM Card Detected/No Permission", Toast.LENGTH_SHORT).show()
    }
}

private fun requestPermissionForReadPhoneState(context: Context) {
    if (checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(context as MainActivity, arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
    } else {
        simDataObserver(context)
    }
}

@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
private fun simDataObserver(context: Context) {
    val subscriptionManager = getSystemService(context, SubscriptionManager::class.java)
    val infoList = subscriptionManager?.activeSubscriptionInfoList
    var currSet = setOf<String>()
    if (infoList != null) {
        for (info in infoList)
            currSet = currSet.plus(info.subscriptionId.toString())
    }

    val sharedPreferences = context.getSharedPreferences("simData", MODE_PRIVATE)
    val prevInfoList = sharedPreferences.getStringSet("simData", setOf())

    if (prevInfoList == null || infoList == null || infoList.size == 0) {
        // Could happen if no SIM detected or if the user had logged in prior to setting SIM state.
        Toast.makeText(context, "LOGOUT USER, NO SIM DETECTED", Toast.LENGTH_SHORT).show()
    } else {
        for (prevInfo in prevInfoList) {
            if (prevInfo !in currSet) {
                Toast.makeText(context, "LOGOUT USER, NO SIM DETECTED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}