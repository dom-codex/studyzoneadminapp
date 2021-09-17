package com.sparktech.studyzoneadmin.helpers

import android.os.Bundle

object KeyComposer {
    fun getLevelPastQuestionListKey(option: HashMap<String, String>): String {
        return "${option["lid"]}${option["semester"]}"
    }

    fun getLevelDeletingPastQuestionKey(option: HashMap<String, String>): String {
        return "deleting${option["lid"]}${option["semester"]}"
    }

    fun getLevelPastQuestionPageKey(option: HashMap<String, String>): String {
        return "currentPastQuestionPage${option["lid"]}${option["semester"]}"
    }

    fun getLevelUploadingPastQuestionKey(option: HashMap<String, String>): String {
        return "uploading${option["lid"]}${option["semester"]}"
    }

    fun getLevelPastQuestionLoadingKey(option: HashMap<String, String>): String {
        return "loading${option["lid"]}${option["semester"]}"
    }

    fun getUserDetailsReferralKey(bundle: Bundle): String {
        val userHash = BundleExtractor.getUserHash(bundle)
        return "${userHash}referrals"
    }

    fun getUserDetailsTransactionsKey(bundle: Bundle): String {
        val userHash = BundleExtractor.getUserHash(bundle)
        return "${userHash}transactions"
    }

    fun getUserDetailsLoadingTransactions(bundle: Bundle): String {
        val userHash = BundleExtractor.getUserHash(bundle)
        return "loadingTransactions${userHash}"
    }

    fun getUserDetailsLoadingReferrals(bundle: Bundle): String {
        val userHash = BundleExtractor.getUserHash(bundle)
        return "loadingReferrals${userHash}"
    }

    fun getUserCurrentTransactionPage(bundle: Bundle): String {
        val userHash = BundleExtractor.getUserHash(bundle)
        return "${userHash}transactionPage"
    }

    fun getUserCurrentReferralPage(bundle: Bundle): String {
        val userHash = BundleExtractor.getUserHash(bundle)
        return "${userHash}referralPage"
    }

    //composer for withdrawal requests
    fun getCurrentRequestListKey(bundle: Bundle): String {
        return bundle.getString("type", "")!!
    }

    fun getCurrentRequestLoadingListKey(bundle: Bundle): String {
        val type = bundle.getString("type", "")!!
        return "loading_${type}"
    }

    fun getCurrentRequestListTypePage(bundle: Bundle): String {
        val type = bundle.getString("type", "")!!
        return "${type}_page"
    }
    fun getCurrentRequestListUpdatingKey(bundle: Bundle):String{
        val type = bundle.getString("type", "")!!
        return "updating_$type"
    }

}