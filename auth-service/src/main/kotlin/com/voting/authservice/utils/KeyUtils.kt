package com.voting.authservice.utils

class KeyUtils {
    companion object {
        fun getOTTRateKey(username: String, tokenType: String): String {
            return username.plus("_").plus(tokenType)
        }

        fun getAccessTokenKey(clientId: String): String {
            return clientId.plus("_access_token")
        }

        fun getOTTKey(clientId: String): String {
            return clientId.plus("_ott")
        }
    }


}