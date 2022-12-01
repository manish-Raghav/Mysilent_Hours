package com.example.silllentkt.activity.fragment



import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions

import androidx.navigation.fragment.findNavController
import com.example.silllentkt.R
import com.example.silllentkt.activity.Util.AppConstants
import com.example.silllentkt.activity.Util.StoreSession
import com.example.silllentkt.activity.Util.Utils
import com.example.silllentkt.activity.database.Profile
import com.example.silllentkt.databinding.FragmentDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
/**
 * A simple [Fragment] subclass.
 *
 */

@AndroidEntryPoint
class DetailsFragment : BottomSheetDialogFragment() {

    @Inject lateinit var gson: Gson
    lateinit var con:Context

    private lateinit var profile: Profile
    private var days: List<Boolean> = ArrayList()
    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = _binding!!
    private val type = object : TypeToken<List<Boolean>>() {}.type
    private val navOptions by lazy {
        //NavOptions.Builder().setEnterAnim(R.anim.nav_default_enter_anim)
        NavOptions.Builder().setEnterAnim(
            androidx.navigation.ui.R.anim.nav_default_enter_anim)
            //.nav_default_enter_anim)
            .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
        //   nav_default_exit_anim)
            .setPopEnterAnim(
                androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
            //nav_default_pop_enter_anim)
            .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim).build()
            //nav_default_pop_exit_anim).build()
    }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(requireView().parent as View) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        profile = requireArguments().getParcelable("Profile")!!
        setupUI(profile)
        binding.editButton.setOnClickListener {
            editProfile()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // this forces the sheet to appear at max height even on landscape
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupUI(profile: Profile) {
        days = gson.fromJson(profile.d, type)
        Utils.selectedDays(days, binding.dayPicker)
        binding.profileTxt.text = profile.name
        binding.str.text = "${setTimeString(profile.shr)}:${setTimeString(profile.smin)}"
        binding.end.text = "${setTimeString(profile.ehr)}:${setTimeString(profile.emin)}"
        binding.profileNote.text = profile.notes
        binding.profileNote.visibility =
            if (binding.profileNote.text.isNotBlank()) VISIBLE else GONE
        if (profile.vibSwitch) binding.audioMode.setImageResource(R.drawable.vibration)
        else binding.audioMode.setImageResource(R.drawable.mute)
        binding.repeatWeeklyIcon.visibility = if (profile.repeatWeekly) VISIBLE else GONE
    }

    private fun setTimeString(i: Int): String {
        return if (i < 10) {
            "0$i"
        } else {
            "$i"
        }
    }

    private fun editProfile() {
        val bundle = Bundle().apply { putParcelable("Profile", profile) }
        StoreSession.writeLong(AppConstants.PROFILE_ID, profile.profileId)
        findNavController().navigate(R.id.newProfileFragment, bundle, navOptions)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(args: Bundle) = DetailsFragment().apply {
            arguments = args
        }
    }
}