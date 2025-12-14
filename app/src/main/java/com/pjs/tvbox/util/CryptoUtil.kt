package com.pjs.tvbox.util

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.util.Base64

object CryptoUtil {

    private const val AES_MODE =
        "AES/CFB/NoPadding"
    private const val AES_TEST =
        "1234567890qwerty"

    fun encrypt(
        plaintext: String,
        key: String = AES_TEST
    ): String? =
        runCatching {
            val iv =
                key.toByteArray(
                    StandardCharsets.UTF_8
                )
            val cipher =
                Cipher.getInstance(
                    AES_MODE
                )
                    .apply {
                        init(
                            Cipher.ENCRYPT_MODE,
                            SecretKeySpec(
                                iv,
                                "AES"
                            ),
                            IvParameterSpec(
                                iv
                            )
                        )
                    }
            val ciphertext =
                cipher.doFinal(
                    plaintext.toByteArray(
                        StandardCharsets.UTF_8
                    )
                )
            return Base64.getEncoder()
                .encodeToString(
                    ciphertext
                )
        }.getOrNull()

    fun decrypt(
        base64Text: String,
        key: String = AES_TEST
    ): String? =
        runCatching {
            val iv =
                key.toByteArray(
                    StandardCharsets.UTF_8
                )
            val ciphertext =
                Base64.getDecoder()
                    .decode(
                        base64Text
                    )
            val cipher =
                Cipher.getInstance(
                    AES_MODE
                )
                    .apply {
                        init(
                            Cipher.DECRYPT_MODE,
                            SecretKeySpec(
                                iv,
                                "AES"
                            ),
                            IvParameterSpec(
                                iv
                            )
                        )
                    }
            val plaintext =
                cipher.doFinal(
                    ciphertext
                )
            String(
                plaintext,
                StandardCharsets.UTF_8
            )
        }.getOrNull()
}