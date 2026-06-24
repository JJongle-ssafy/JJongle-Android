package com.ssafy.jjongle.common.domain.helper

interface MessageHelper {
    fun showToast(messageText: String)

    fun showSnackBar(messageText: String)

    fun showOneButtonDialog(
        cantIgnore: Boolean = false,
        descText: String,
        onClickButton: () -> Unit = {},
    )

    fun showTwoButtonDialog(
        descText: String,
        onClickPositive: () -> Unit,
        onClickNegative: () -> Unit = {},
    )

    object NoOp : MessageHelper {
        override fun showToast(messageText: String) = Unit

        override fun showSnackBar(messageText: String) = Unit

        override fun showOneButtonDialog(
            cantIgnore: Boolean,
            descText: String,
            onClickButton: () -> Unit,
        ) = Unit

        override fun showTwoButtonDialog(
            descText: String,
            onClickPositive: () -> Unit,
            onClickNegative: () -> Unit,
        ) = Unit
    }
}
