package com.gas.flexapp.local

import android.security.keystore.KeyProperties
import android.util.Base64
import com.gas.flexapp.BuildConfig
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class CipherServiceImpl @Inject constructor() : CipherService {

    private val keyValue: ByteArray = BuildConfig.CIPHER_KEY.toByteArray(Charsets.UTF_8)
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

    override fun encrypt(data: String): String {
        val inputText: ByteArray = data.toByteArray(Charsets.UTF_8)
        val iv = generateRandomIV(cipher.blockSize)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(keyValue, KeyProperties.KEY_ALGORITHM_AES), IvParameterSpec(iv))

        val encryptedBytes = cipher.doFinal(inputText)
        val encryptedDataWithIV = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, encryptedDataWithIV, iv.size, encryptedBytes.size)
        return Base64.encodeToString(encryptedDataWithIV, Base64.DEFAULT)
    }

    override fun decrypt(data: String): String {
        val encryptedDataWithIV = Base64.decode(data, Base64.DEFAULT)
        val iv = encryptedDataWithIV.copyOfRange(0, cipher.blockSize)
        val encryptedData = encryptedDataWithIV.copyOfRange(cipher.blockSize, encryptedDataWithIV.size)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(keyValue, KeyProperties.KEY_ALGORITHM_AES), IvParameterSpec(iv))

        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun generateRandomIV(size: Int): ByteArray {
        val random = SecureRandom()
        val iv = ByteArray(size)
        random.nextBytes(iv)
        return iv
    }
}