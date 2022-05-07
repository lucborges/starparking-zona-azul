package com.example.starparkingzonaazul

import com.google.gson.annotations.SerializedName

class FunctionsGenericResponse {

    enum class StatusType(val type: String) {
        ERROR("ERROR"),
        SUCCESS("SUCCESS");
    }

    @SerializedName("status")
    var status: StatusType? = null;

    @SerializedName("message")
    var message: String? = null;

    @SerializedName("payload")
    var payload: Any? = null;

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FunctionsGenericResponse

        if (status != other.status) return false
        if (message != other.message) return false
        if (payload != other.payload) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status?.hashCode() ?: 0
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (payload?.hashCode() ?: 0)
        return result
    }
}

annotation class SerializedName(val value: String)
