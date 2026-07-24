package com.example.machinetest.utils

import android.content.Context
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import com.example.machinetest.domain.dataModel.ContactModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardDataManager(private val context: Context) {

    fun getContactsCount(): Int {
        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        val count = cursor?.count ?: 0
        cursor?.close()
        return count
    }

    fun getContactsList(): List<ContactModel> {
        val contacts = mutableListOf<ContactModel>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: ""
                val number = it.getString(numberIndex) ?: ""
                contacts.add(ContactModel(name, number))
            }
        }
        return contacts
    }

    fun getCallLogsCount(): Int {
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null
        )
        val count = cursor?.count ?: 0
        cursor?.close()
        return count
    }

    fun getSmsCount(): Int {
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null, null, null, null
        )
        val count = cursor?.count ?: 0
        cursor?.close()
        return count
    }

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
