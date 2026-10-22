package com.example.fish_finder.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.InputStream
import kotlin.math.max

/**
 * Utilitários seguros para aquisição, cópia e decodificação de imagens da Câmera e Galeria.
 */
object ImageUtils {

    /**
     * Cria um arquivo temporário no cache do aplicativo e gera sua Uri via FileProvider para a Câmera.
     */
    fun createCameraPhotoUri(context: Context): Pair<File, Uri> {
        val storageDir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
        val photoFile = File.createTempFile(
            "fish_photo_${System.currentTimeMillis()}",
            ".jpg",
            storageDir
        )
        val photoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        return photoFile to photoUri
    }

    /**
     * Copia a imagem selecionada da Galeria para o armazenamento interno privado do aplicativo.
     * Garante durabilidade e acesso permanente sem depender de permissões temporárias de Uri.
     */
    fun copyUriToInternalStorage(context: Context, sourceUri: Uri): Uri {
        return try {
            val imagesDir = File(context.filesDir, "captured_images").apply { mkdirs() }
            val destFile = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(destFile)
        } catch (e: Exception) {
            sourceUri
        }
    }

    /**
     * Decodifica um Bitmap a partir de uma URI (content:// ou file://) com sub-amostragem
     * e correção automática de rotação EXIF.
     */
    fun loadBitmapFromUri(context: Context, uriString: String?, maxDim: Int = 1080): Bitmap? {
        if (uriString.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(uriString)

            // 1. Decodifica apenas as dimensões da imagem
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            openStream(context, uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, boundsOptions)
            }

            val origW = boundsOptions.outWidth
            val origH = boundsOptions.outHeight
            if (origW <= 0 || origH <= 0) return null

            // 2. Calcula sample size para otimização de memória
            var inSampleSize = 1
            val maxOriginal = max(origW, origH)
            while (maxOriginal / (inSampleSize * 2) >= maxDim) {
                inSampleSize *= 2
            }

            // 3. Decodifica o bitmap com o sample size
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val decodedBitmap = openStream(context, uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            } ?: return null

            // 4. Corrige orientação EXIF se necessário
            val rotation = getExifRotation(context, uri)
            if (rotation != 0) {
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                Bitmap.createBitmap(decodedBitmap, 0, 0, decodedBitmap.width, decodedBitmap.height, matrix, true)
            } else {
                decodedBitmap
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun openStream(context: Context, uri: Uri): InputStream? {
        return try {
            if (uri.scheme == "file") {
                File(uri.path ?: "").inputStream()
            } else {
                context.contentResolver.openInputStream(uri)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getExifRotation(context: Context, uri: Uri): Int {
        return try {
            openStream(context, uri)?.use { input ->
                val exif = ExifInterface(input)
                when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }
}
