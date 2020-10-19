package com.example.gim.title

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.example.gim.LoginActivity
import com.example.gim.R
import com.example.gim.databinding.FragmentTitleBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.abs


class TitleFragment : Fragment() {

    private lateinit var binding : FragmentTitleBinding

    private lateinit var titleViewModel: TitleViewModel

    //Shared preferences to save data even after the app goes off
    private val sharedPrefilName = "com.examplegim"
    private val sharedKey = "GGMII0"
    private val sharedString = "HH1BMO"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPrefilName, Context.MODE_PRIVATE)
        val preferenceEditor : SharedPreferences.Editor = sharedPreferences.edit()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_title, container, false)
        titleViewModel = ViewModelProviders.of(this).get(TitleViewModel::class.java)
        binding.lifecycleOwner = this

        //List View declaration
        val adapter = LogHoursAdapter()
        binding.recyclerView.adapter = adapter

        //Displaying user name
        binding.demoText.text = FirebaseAuth.getInstance().currentUser?.displayName

        //data transfer to list
        titleViewModel.dataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        //setting default for ability to record data
        titleViewModel.ableToRecord = sharedPreferences.getBoolean(sharedKey, false)

        //Enable or not recording of data
        binding.buttonStartTracking.setOnClickListener {
            getWifiSSIDNow()
            if(titleViewModel.ableToRecord){
                titleViewModel.postData()
                preferenceEditor.putBoolean(sharedKey, true)
                preferenceEditor.apply()
                preferenceEditor.putString(sharedString, Timestamp.now().seconds.toString())
                preferenceEditor.apply()
            }
        }

        //Stop recording button
        binding.buttonStop.setOnClickListener {
            if(titleViewModel.ableToRecord){
                val stopTrack = Timestamp.now()
                var timeDiff = stopTrack.seconds - sharedPreferences.getString(sharedString, "100101010")!!.toLong()
                timeDiff = abs(timeDiff)
                TimeCheck.totalPrevTime += timeDiff
                    preferenceEditor.putBoolean(sharedKey, false)
                preferenceEditor.apply()
                titleViewModel.addStopTime(stopTrack)
            }
        }

        //Logout button
        binding.buttonLogOut.setOnClickListener {
            logout()
        }
        return binding.root
    }

    //Function to check if wifi has the desired SSID
    private fun getWifiSSIDNow() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        //Get wifi information
        val mWifiManager =
            (requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)!!
        val info = mWifiManager!!.connectionInfo

        if (info.supplicantState === SupplicantState.COMPLETED) {
            Log.i("SGSGS", "${info.ssid}")
            if(info.ssid.trim().removeSurrounding("\"") == "AndroidWifi"){
                titleViewModel.ableToRecord = true
//                binding.demoText.text = Calendar.getInstance().timeInMillis.toString()
            }else{
                Toast.makeText(requireActivity(), "Something went wrong. Check the wifi.",
                    Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(requireActivity(), "Something is wrong. Permission issue",
                Toast.LENGTH_LONG).show()
        }
    }

    //Function to log out
    private fun logout() {
        AuthUI.getInstance()
            .signOut(requireActivity())
            .addOnCompleteListener {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
    }




}