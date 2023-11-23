package com.example.salehub.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentSalehubHomeBinding
import com.example.salehub.second_activity.SecondActivity

/**
 * Created by Thanh Long Nguyen on 6/13/2021
 */
class SalehubHomeFragment : BaseViewBindingFragment<FragmentSalehubHomeBinding>() {

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSalehubHomeBinding? {
        return FragmentSalehubHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        initViewAction()
    }

    private fun initViewAction() {
        binding?.btnNavigateToRatingBarFragment?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_ratingBarFragment)
        }
        binding?.btnAccount?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_saleHubAccountFragmentData)
        }
        binding?.fabRed?.setOnClickListener {
//            (activity as? MainActivity)?.addFragment(TransparentFragment())
//            activity?.finish()
            Intent(requireActivity(), SecondActivity::class.java).also {
                startActivity(it)
            }
        }
        binding?.btnAnim?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_animFragment)
        }

        binding?.btnMyCanvas?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_myCanvasFragment)
        }

        binding?.btnFanController?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_fanControllerFragment)
        }

        binding?.btnSlideBar?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_slideBarFragment)
        }

        binding?.btnRuler?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_rulerPickerFragment)
        }

        binding?.btnFanLottie?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_fanLottieFragment)
        }

        binding?.btnSwipeToDel?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_recyclerViewSwipeToShowDeleteFragment)
        }

        binding?.btnStepRv?.setOnClickListener {
            findNavController().navigate(R.id.action_salehubHomeFragment_to_stepRVFragment)
        }

        binding?.btnCameraX?.setOnClickListener {
            findNavController().navigate(R.id.cameraXFragment)
        }

        binding?.btnNumberPicker?.setOnClickListener {
            findNavController().navigate(R.id.numberPickerFragment)
        }

        binding?.btnOtp?.setOnClickListener {
            findNavController().navigate(R.id.otpFragment)
        }

        binding?.btnChooseAddress?.setOnClickListener {
            findNavController().navigate(R.id.chooseAddressFragment)
        }

        binding?.btnTooltip?.setOnClickListener {
            findNavController().navigate(R.id.tooltipFragment)
        }

        binding?.btnZoom?.setOnClickListener {
            findNavController().navigate(R.id.zoomFragment)
        }

        binding?.btnWebview?.setOnClickListener {
            findNavController().navigate(R.id.webViewFragment)
        }

        binding?.btnCoroutineException?.setOnClickListener {
            findNavController().navigate(R.id.coroutineFragment)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
//        binding?.checkbox?.isChecked = false
    }
}
