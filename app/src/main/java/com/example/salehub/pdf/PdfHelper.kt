package com.example.salehub.pdf

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
//import com.vnptmedia.soft.vn.smartdealer.infrastructure.core.helpers.logging.DebugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

//object PdfHelper {
//    suspend fun renderSinglePage(filePath: String, width: Int, pagePosition: Int) =
//        withContext(Dispatchers.IO) {
//            try {
//                PdfRenderer(
//                    ParcelFileDescriptor.open(
//                        File(filePath),
//                        ParcelFileDescriptor.MODE_READ_ONLY
//                    )
//                ).use { renderer ->
//                    renderer.openPage(pagePosition).renderAndClose(width)
//                }
//            } catch (e: Exception) {
//                DebugLog.e(e)
//            }
//        }
//
//    suspend fun renderSinglePage(file: File, width: Int, pagePosition: Int) =
//        withContext(Dispatchers.IO) {
//            try {
//                PdfRenderer(
//                    ParcelFileDescriptor.open(
//                        file,
//                        ParcelFileDescriptor.MODE_READ_ONLY
//                    )
//                ).use { renderer ->
//                    renderer.openPage(pagePosition).renderAndClose(width)
//                }
//            } catch (e: Exception) {
//                DebugLog.e(e)
//            }
//        }
//}
