package com.example.starparkingzonaazul

import com.google.gson.annotations.SerializedName

/***
 * Supondo que cada vez que vc
 * use uma Function que retorne um documentId (docId) no payload
 * como um Json, basta tratar como um objeto de resposta.
 * Isso organiza o código e pode ser reaproveitado para
 * qualquer function que retorne no payload um docId
 * @author Mateus Dias
 */
class GenericInsertResponse {

    @SerializedName("docId")
    var docId: String? = null;

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenericInsertResponse
        if (docId != other.docId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = docId?.hashCode() ?: 0
        result = 31 * result + (docId?.hashCode() ?: 0)
        return result
    }
}