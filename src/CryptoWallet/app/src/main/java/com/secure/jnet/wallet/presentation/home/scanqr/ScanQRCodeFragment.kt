//package com.secure.jnet.wallet.presentation.home.scanqr
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.ImageDecoder
//import android.graphics.ImageDecoder.OnHeaderDecodedListener
//import android.graphics.PointF
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.google.zxing.BinaryBitmap
//import com.google.zxing.DecodeHintType
//import com.google.zxing.MultiFormatReader
//import com.google.zxing.common.HybridBinarizer
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentScanQrCodeBinding
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.home.scanqr.view.QRCodeReaderView
//import com.secure.jnet.wallet.util.BitmapLuminanceSource
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//import timber.log.Timber
//import java.util.EnumMap
//
//@AndroidEntryPoint
//class ScanQRCodeFragment : BaseFragment<FragmentScanQrCodeBinding>(
//    R.layout.fragment_scan_qr_code
//), QRCodeReaderView.OnQRCodeReadListener {
//
//    private val viewModel: ScanQRCodeViewModel by viewModels()
//
//    private val cameraPermissionResult =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (granted) {
//                initQRCodeReaderView()
//            }
//        }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivClose.setOnClickListener { findNavController().popBackStack() }
//
//            ivPickFromGallery.setOnClickListener {
//                pickFromGallery()
//            }
//
//            qrDecoderView.isVisible = false
//        }
//
//        if (isCameraPermissionGranted()) {
//            initQRCodeReaderView()
//        } else {
//            cameraPermissionResult.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.navigateBack) {
//            navigateBack(it)
//        }
//    }
//
//    private fun isCameraPermissionGranted(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.qrDecoderView.startCamera()
//    }
//
//    override fun onPause() {
//        binding.qrDecoderView.stopCamera()
//        super.onPause()
//    }
//
//    override fun onQRCodeRead(text: String, points: Array<PointF>) {
//        viewModel.onQrCodeScanned(text)
//    }
//
//    private fun initQRCodeReaderView() {
//        binding.apply {
////            qrDecoderView.apply {
////                isVisible = true
////                setAutofocusInterval(500L)
////                setOnQRCodeReadListener(this@ScanQRCodeFragment)
////                setBackCamera()
////                startCamera()
////            }
//        }
//    }
//
//    private val pickMedia =
//        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            if (uri != null) {
//                Timber.d("-----> Selected URI: $uri")
//                decodeQRCode(uri)
//            } else {
//                Timber.d("-----> No media selected")
//            }
//        }
//
//    private fun pickFromGallery() {
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }
//
//    private fun decodeQRCode(imageUri: Uri?) {
//        val source = ImageDecoder.createSource(
//            requireActivity().contentResolver,
//            imageUri!!
//        )
//        val listener = OnHeaderDecodedListener { decoder, _, _ -> decoder.isMutableRequired = true }
//
//        val bitmap = ImageDecoder.decodeBitmap(source, listener)
//
//        val multiFormatReader = MultiFormatReader()
//        val hints = EnumMap<DecodeHintType, Any>(com.google.zxing.DecodeHintType::class.java)
//        hints[DecodeHintType.TRY_HARDER] = true
//
//        try {
//            val binaryBitmap = BinaryBitmap(HybridBinarizer(BitmapLuminanceSource(bitmap)))
//            val result = multiFormatReader.decode(binaryBitmap, hints)
//            val qrText = result.text
//            Timber.d("-----> $qrText")
//
//            viewModel.onQrCodeScanned(qrText)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun navigateBack(qrCode: String) {
//        val bundle = Bundle().apply {
//            putString(QR_CODE_EXTRA, qrCode)
//        }
//        parentFragmentManager.setFragmentResult(SCAN_QR_CODE_KEY, bundle)
//        findNavController().popBackStack()
//    }
//
//    companion object {
//        const val SCAN_QR_CODE_KEY = "scanQRCode"
//        const val QR_CODE_EXTRA = "qrCode"
//    }
//}